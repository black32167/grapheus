package org.grapheus.jarscanner

import org.objectweb.asm.signature.SignatureVisitor
import org.objectweb.asm.Opcodes
import java.lang.IllegalStateException
import org.objectweb.asm.signature.SignatureReader

class SignatureNode : SignatureVisitor(Opcodes.ASM7) {
    private var typeParameterEncountered = false
    lateinit var className:String private set
    
    val _typeArguments = mutableListOf<String>()
    
    val typeArguments:List<String> get() = _typeArguments
    
    fun getTypeArgument():List<String> = _typeArguments
  

    override fun visitTypeArgument(wildcard: Char): SignatureVisitor? {
        typeParameterEncountered = true
        return this
    }
    
    override fun visitClassType(name: String) {
        if(typeParameterEncountered) {
            _typeArguments.add(name.replace('/', '.'))
        } else {
            if(::className.isInitialized) {
                throw IllegalStateException("className is already set to ${className} (new value is ${name}")
            }
            className = name
        }
    }
    
    companion object {
        fun of(signature:String):SignatureNode {
            val signatureNode = SignatureNode()
            val signatureReader = SignatureReader(signature)
            signatureReader.acceptType(signatureNode)
            return signatureNode
        }
    }
}

