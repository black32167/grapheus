package org.grapheus.jarscanner

import java.nio.file.Path

/**
 * Event listener is invoked in course of traversing jars/classes/fields
 */
interface JarDependenciesVisitor {
    fun onJarStart(jarPath:Path):Boolean
    fun onClassStart(className:String):Boolean
    fun onClassEnd(className: String)
    fun onField(className:String, fieldName:String, fieldType:String)
    fun onScanningFinished()
}