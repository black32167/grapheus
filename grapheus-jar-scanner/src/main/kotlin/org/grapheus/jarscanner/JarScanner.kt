package org.grapheus.jarscanner
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import org.grapheus.client.model.GraphStreamSerializer
import org.grapheus.client.model.graph.vertex.RVertex
import org.grapheus.jarscanner.concurrent.TerminatingQueue
import org.grapheus.jarscanner.visitor.VertexCollectingDependencyVisitor
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths


/** Convert the option values to an `Int` */
class ScanCommand : CliktCommand() {

    private val folderToIndex: Path by argument().path()

    private val outputPath:Path by option("-o", help="Output path for resuting graph dump").path()
            .defaultLazy {
                Paths.get("out-graph.zip")//"${folderToIndex.fileName.toString()}
            }
    private val jarsPattern:String by option("-p", help="Jars file pattern").default(".*\\.jar")

    override fun run() {
        // Queue shared between producer and consumer
        val verticesQueue = TerminatingQueue<RVertex>()

        // Scanning folder in the separate thread
        Thread {
            ClassesInJarIterator(folderToIndex, jarsPattern)
                    .iterate(VertexCollectingDependencyVisitor(verticesQueue))
            verticesQueue.close()
        }.start()

        // Consuming and serializing found entries
        FileOutputStream(outputPath.toFile())
                .use { output->
                    GraphStreamSerializer()
                            .graphId(folderToIndex.fileName.toString())
                            .verticesProducer(verticesQueue)
                            .serialize(output)
                }
    }
}

fun main(args:Array<String>) = ScanCommand().main(args)
