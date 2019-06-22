package org.grapheus.jarscanner

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * Extracts dependencies from the specified class.
 */
class ClassDependenciesExtractingVisitor(val fieldCallback:(String, String) -> Unit) : ClassVisitor(Opcodes.ASM7) {
    
    override fun visitField(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        value: Any?
    ): FieldVisitor? {
        // Visit field type
        visitField(name, access, Type.getType(descriptor).className)
        
        if(signature != null) {
            // Visit field type
            SignatureNode.of(signature).typeArguments.forEach {
                visitField(name, access, it)
            }
        }


        return null
    }
    
    private fun visitField(fieldName:String, access: Int, fieldType:String) {
          if (isEligibleField(access, fieldType)) {
            fieldCallback(fieldName, fieldType)
        }
    }

    
    private fun isEligibleField(access: Int, fieldType: String):Boolean = (access and Opcodes.ACC_STATIC) == 0

}