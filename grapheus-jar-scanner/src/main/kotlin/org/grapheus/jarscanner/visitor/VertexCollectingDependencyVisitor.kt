package org.grapheus.jarscanner.visitor

import org.grapheus.client.model.graph.vertex.RVertex
import org.grapheus.jarscanner.JarDependenciesVisitor
import org.grapheus.jarscanner.concurrent.TerminatingQueue
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 * Scanning events listener.
 */
class VertexCollectingDependencyVisitor (
        val verticesQueue : TerminatingQueue<RVertex>
): JarDependenciesVisitor {
    private val log = LoggerFactory.getLogger(VertexCollectingDependencyVisitor::class.java)
    private val exclusionPatterns = listOf(
            "java\\..*",
            "javax\\..*",
            "kotlin\\..*",
            "[^.]*")

    data class ClassReference (val targetClass:String, val reverseRelation:Boolean)
    class ClassDescriptor(val name:String) {
        val references =  mutableSetOf<ClassReference>()
    }

    private var currentJarFileName:String? = null
    private var currentClass:ClassDescriptor? = null
    private val encounteredClasses = mutableSetOf<String>()
    private val encounteredJars = mutableSetOf<String>()

    override fun onJarStart(jarPath: Path):Boolean {
        val jarFileName = jarPath.fileName.toString()
        if(encounteredJars.add(jarFileName)) {
            log.info("======= Jar found: ${jarPath}")
            currentJarFileName = jarFileName
            return true
        } else {
            log.error("======= Jar is already registered: ${jarPath}")
            return false
        }
    }

    override fun onScanningFinished() {
        verticesQueue.close()
    }

    override fun onClassStart(className: String):Boolean {
        if(encounteredClasses.add(className)) {
            log.info("Encountered class ${className}")
            currentClass = ClassDescriptor(className)
            return true
        } else {
            log.error("Class ${className} is already registered")
            return false
        }
    }

    override fun onClassEnd(className: String) {
        if(currentClass == null || className != currentClass!!.name) {
            throw IllegalStateException("Closing wrong class (${className}), expected: ${currentClass?.name ?: "<null>"}")
        }

        val title = classNameToVertextTitle(className)
        val references = currentClass!!.references.map { ref->
            RVertex.RReference
                    .builder()
                    .destinationId(classNameToVertextId(ref.targetClass))
                    .reversed(ref.reverseRelation)
                    .build()
        }
        verticesQueue.put(
                RVertex
                        .builder()
                        .id(classNameToVertextId(className))
                        .title(title)
                        .description(title)
                        .references(references)
                        .property(RVertex.RProperty.builder().name("source").value(currentJarFileName).build())
                        .build())
    }
    override fun onInterface(intfce: String) {
        if(isEligibleType(intfce)) {
            currentClass!!.references.add(ClassReference(chompInternalClass(intfce), true))
        }
    }

    override fun onSuperclass(superName: String) {
        if(isEligibleType(superName)) {
            currentClass!!.references.add(ClassReference(chompInternalClass(superName), true))
        }
    }
    override fun onField(className:String, fieldName: String, fieldType: String) {
        if(isEligibleType(fieldType)) {
            currentClass!!.references.add(ClassReference(chompInternalClass(fieldType), false))
            log.info("\t Encountered field ${className}#${fieldName} : ${fieldType}")
        }
    }

    private fun chompInternalClass(className:String) = className.replace("\\$.*".toRegex(), "")

    private fun isEligibleType(type: String):Boolean =
            !exclusionPatterns.map { it.toRegex() }.any { it.matches(type) }


    private fun classNameToVertextTitle(className:String) = className.replace(".*\\.".toRegex(), "")
    private fun classNameToVertextId(className:String) = className.replace('.', '_')

}