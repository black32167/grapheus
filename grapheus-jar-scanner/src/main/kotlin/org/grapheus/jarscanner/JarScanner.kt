package org.grapheus.jarscanner
import org.grapheus.client.model.GraphStreamSerializer
import org.grapheus.client.model.graph.vertex.RVertex
import org.grapheus.jarscanner.concurrent.TerminatingQueue
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

fun usage() {
    println("Usage:")
    println("    JarScanner <root_folder>")
    exitProcess(1)
}
fun main(args:Array<String>) {
    if (args.size < 1) {
        usage()
    }
    val scanRoot = Paths.get(args[0])

    // Queue shared between producer and consumer
    val verticesQueue = TerminatingQueue<RVertex>()

    // Scanning events listener
    val dependenciesVisitor = object: JarDependenciesVisitor {

        override fun onJarStart(jarPath: Path) {
            println("======= Jar found: ${jarPath}")
        }

        override fun onScanningFinished() {
            verticesQueue.close()
        }

        override fun onClass(className: String) {
            val title = className.replace(".*\\.".toRegex(),"")
            verticesQueue.put(RVertex
                    .builder()
                    .localId(className)
                    .title(title)
                    .description(title)
                    .build())
        }

        override fun onField(className:String, fieldName: String, fieldType: String) {
            println("\t Encountered field ${className}#${fieldName} : ${fieldType}")
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