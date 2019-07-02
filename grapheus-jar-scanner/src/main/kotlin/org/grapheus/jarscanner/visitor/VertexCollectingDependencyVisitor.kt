package org.grapheus.jarscanner.visitor

import org.grapheus.client.model.graph.vertex.RVertex
import org.grapheus.jarscanner.JarDependenciesVisitor
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.function.Consumer

/**
 * Scanning events listener.
 */
const val TYPE_CLASS = "class"
const val TYPE_INTERFACE = "interface"
const val RTYPE_IMPLEMENTS = "implements"
const val RTYPE_EXTENDS = "extends"
const val RTYPE_REFERS = "refers"

class VertexCollectingDependencyVisitor (
        val verticesQueue : Consumer<RVertex>
): JarDependenciesVisitor {

    private val log = LoggerFactory.getLogger(VertexCollectingDependencyVisitor::class.java)
    private val exclusionPatterns = listOf(
            "java\\..*",
            "javax\\..*",
            "kotlin\\..*",
            "[^.]*",
            ".*\\.module-info")

    data class ClassReference (val targetClass:String, val referenceType:String, val reverseRelation:Boolean)
    class ClassDescriptor(val name:String, val type: String) {
        val references =  mutableSetOf<ClassReference>()
    }

    private var currentJarFileName:String? = null
    private var currentClass:ClassDescriptor? = null
    private val encounteredClasses = mutableSetOf<String>()
    private val encounteredJars = mutableSetOf<String>()

    override fun onJarStart(jarPath: Path):Boolean {
        val jarFileName = jarPath.fileName.toString()
        if(encounteredJars.add(jarFileName)) {
            log.info("======= Scanning Jar: ${jarPath}")
            currentJarFileName = jarFileName
            return true
        } else {
            log.error("======= Jar name is already registered, skipping: ${jarPath}")
            return false
        }
    }

    override fun onScanningFinished() {
        // Nothing
    }

    override fun onClassStart(className: String, classType: String):Boolean {
        if(isEligibleType(className) && encounteredClasses.add(className)) {
            log.info("\tEncountered class '${className}'")
            currentClass = ClassDescriptor(name = className, type = classType)
            return true
        } else {
            log.warn("\tClass '${className}' is already registered or is not eligible, skipping")
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
                    .classifiers(listOf(ref.referenceType))
                    .reversed(ref.reverseRelation)
                    .build()
        }
        verticesQueue.accept(
                RVertex
                        .builder()
                        .id(classNameToVertextId(className))
                        .title(title)
                        .description(title)
                        .references(references)
                        .property(RVertex.RProperty.builder().name("source").value(currentJarFileName).build())
                        .tag(currentClass!!.type)
                        .build())
    }
    override fun onInterface(intfce: String) {
        if(isEligibleType(intfce)) {
            currentClass!!.references.add(ClassReference(
                    targetClass = chompInternalClass(intfce),
                    referenceType = RTYPE_IMPLEMENTS,
                    reverseRelation = true))
            log.info("\t\tEncountered interface '${intfce}'")
        }
    }

    override fun onSuperclass(superName: String) {
        if(isEligibleType(superName)) {
            currentClass!!.references.add(ClassReference(
                    targetClass = chompInternalClass(superName),
                    referenceType = RTYPE_EXTENDS,
                    reverseRelation = true))
            log.info("\t\tEncountered superclass '${superName}'")
        }
    }
    override fun onField(className:String, fieldName: String, fieldType: String) {
        if(isEligibleType(fieldType)) {
            currentClass!!.references.add(ClassReference(
                    targetClass = chompInternalClass(fieldType),
                    referenceType = RTYPE_REFERS,
                    reverseRelation = false))
            log.info("\t\tEncountered field '${fieldName}' of type '${fieldType}'")
        }
    }

    private fun chompInternalClass(className:String) = className.replace("\\$.*".toRegex(), "")

    private fun isEligibleType(type: String):Boolean =
            !exclusionPatterns.map { it.toRegex() }.any { it.matches(type) }


    private fun classNameToVertextTitle(className:String) = className.replace(".*\\.".toRegex(), "")
    private fun classNameToVertextId(className:String) = className.replace('.', '_')

}