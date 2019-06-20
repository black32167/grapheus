package org.grapheus.jarscanner.concurrent

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.lang.IllegalStateException

class TerminatingQueue<T> : Iterable<T> {
    private val backingQueue = ArrayBlockingQueue<T>(100)//TODO: constant
    private val terminator = Any()

    @Volatile
    var closed = false

    fun put(element: T) {
        if (closed) {
            throw IllegalStateException("Queue is already closed")
        }

        backingQueue.put(element)
    }

    fun close() {
        synchronized(terminator) {
            if (closed) {
                return
            }
            closed = true
        }
        backingQueue.put(terminator!! as T)
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        var lastElement: T? = null

        override fun hasNext(): Boolean {
            lastElement = backingQueue.take()
            if(lastElement == terminator) {
                lastElement = null
            }
            return lastElement != null
        }

        override fun next(): T {
            val nextElement = lastElement
            lastElement = null
            return nextElement ?: throw IllegalStateException("Queue is already closed")
        }
    }
}