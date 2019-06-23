package org.grapheus.jarscanner.concurrent

import java.util.concurrent.ArrayBlockingQueue

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

        @Suppress("UNCHECKED_CAST")
        backingQueue.put(terminator as T)
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        var lastElement: T? = null
        var hadNext = true

        override fun hasNext(): Boolean {
            return pullNext() != null
        }

        override fun next(): T {
            val nextElement = pullNext()
            lastElement = null
            return nextElement ?: throw NoSuchElementException("Queue is already closed")
        }

        private fun pullNext():T? {
            if(lastElement == null && hadNext) {
                lastElement = backingQueue.take()
                if (lastElement == terminator) {
                    lastElement = null
                }

                hadNext = lastElement != null
            }
            return lastElement
        }
    }
}