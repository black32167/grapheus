package org.grapheus.jarscanner

import org.objectweb.asm.ClassReader
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.zip.ZipInputStream

class ClassesInJarIterator(
    val rootPath:Path, jarPattern:String = ".*\\.jar", classPattern:String = "[^$]*\\.class") {
    
    private val jarPatterRegex:Regex
    private val classPatternRegex:Regex
    init {
        jarPatterRegex = jarPattern.toRegex()
        classPatternRegex = classPattern.toRegex()
    }

    val max_depth = 99
    
    fun iterate(dependenciesVisitor: JarDependenciesVisitor) {
        Files.find(rootPath, max_depth, isJarFilePredicate()).forEach { pathToJarFile->
            if(dependenciesVisitor.onJarStart(pathToJarFile)) {
                ZipInputStream(FileInputStream(pathToJarFile.toFile())).use { jis ->
                    var entry = jis.nextEntry

                    while (entry != null) {
                        if (classPatternRegex.matches(entry.name)) {
                            val classReader = ClassReader(jis)

                            val className = normalizeClassname(classReader.className)

                            val classDependenciesExtractingVisitor = ClassDependenciesExtractingVisitor { fieldName, fieldType ->
                                dependenciesVisitor.onField(className, fieldName, fieldType)
                            }

                            if (dependenciesVisitor.onClassStart(className)) {
                                classReader.interfaces.forEach { intfce->
                                    dependenciesVisitor.onInterface(normalizeClassname(intfce))
                                }
                                if(classReader.superName != null) {
                                    // 'superName' can be null in case of Java9 'module-info.class', for example
                                    dependenciesVisitor.onSuperclass(normalizeClassname(classReader.superName))
                                }

                                
                                classReader.accept(classDependenciesExtractingVisitor, ClassReader.SKIP_CODE)

                                dependenciesVisitor.onClassEnd(className)
                            }
                        }
                        entry = jis.nextEntry
                    }
                }
            }
        }

        dependenciesVisitor.onScanningFinished()
    }
    private fun normalizeClassname(className:String) = className.replace('/', '.')
    private fun isJarFilePredicate(): BiPredicate<Path, BasicFileAttributes> =
        BiPredicate<Path, BasicFileAttributes>() {path, attrs ->
            attrs.isRegularFile && jarPatterRegex.matches(path.fileName.toString())}

}