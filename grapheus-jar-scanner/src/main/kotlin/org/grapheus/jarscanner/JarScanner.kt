package org.grapheus.jarscanner
import kotlin.system.exitProcess
import java.nio.file.Paths
import org.objectweb.asm.ClassReader
import java.nio.file.Path

class JarScanner {
    
}
fun usage() {
    println("Usage:")
    println("    JarScanner <root_folder>")
    exitProcess(1)
}
fun main(args:Array<String>) {
    if (args.size == 0) {
        usage()
    }
    val scanRoot = Paths.get(args[0])
    
    val dependenciesVisitor = object: JarDependenciesVisitor {
        override fun onJar(jarPath: Path) {
            println("======= Jar found: ${jarPath}")
        }

        override fun onClass(className: String) {
           // println("> Traversing class: ${className}")
        }

        override fun onField(className:String, fieldName: String, fieldType: String) {
            println("\t Encountered field ${className}#${fieldName} : ${fieldType}")
        }
    }
    
    ClassesInJarIterator(scanRoot, "grapheus-.*\\.jar").iterate(dependenciesVisitor)

}