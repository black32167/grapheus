package org.grapheus.jarscanner
import org.grapheus.client.model.GraphStreamSerializer
import org.grapheus.client.model.graph.vertex.RVertex
import org.grapheus.jarscanner.concurrent.TerminatingQueue
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.lang.IllegalStateException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

fun usage() {
    println("Usage:")
    println("    JarScanner <root_folder>")
    exitProcess(1)
}

fun String.classNameToVertextTitle() = this.replace(".*\\.".toRegex(), "")
fun String.classNameToVertextId() = this.replace('.', '_')

fun main(args:Array<String>) {
    val log = LoggerFactory.getLogger("main")

    if (args.size < 1) {
        usage()
    }
    val scanRoot = Paths.get(args[0])

    // Queue shared between producer and consumer
    val verticesQueue = TerminatingQueue<RVertex>()

    // Scanning events listener
    class ClassDescriptor(val name:String) {
        val references =  mutableSetOf<String>()
    }
    val dependenciesVisitor = object: JarDependenciesVisitor {
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

            val title = className.classNameToVertextTitle()
            val references = currentClass!!.references.map { destinationClass->
                RVertex.RReference
                    .builder()
                    .destinationId(destinationClass.classNameToVertextId())
                    .build()
            }
            verticesQueue.put(
                    RVertex
                        .builder()
                        .localId(className.classNameToVertextId())
                        .title(title)
                        .description(title)
                        .references(references)
                        .property(RVertex.RProperty.builder().name("source").value(currentJarFileName).build())
                        .build())
        }

        override fun onField(className:String, fieldName: String, fieldType: String) {
            currentClass!!.references.add(fieldType.replace("\\$.*".toRegex(), ""))
            log.info("\t Encountered field ${className}#${fieldName} : ${fieldType}")
        }
    }

    // Scanning folder in the separate thread
    Thread {
        ClassesInJarIterator(scanRoot, "grapheus-.*\\.jar").iterate(dependenciesVisitor)
    }.start()

    // Consuming and serializing found entries
    FileOutputStream("${scanRoot.fileName.toString()}-graph.zip").use { output->
        GraphStreamSerializer()
            .graphId(scanRoot.fileName.toString())
            .verticesProducer(verticesQueue)
            .serialize(output)
    }

}