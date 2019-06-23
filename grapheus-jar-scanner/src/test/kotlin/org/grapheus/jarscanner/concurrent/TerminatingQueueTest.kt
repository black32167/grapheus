package org.grapheus.jarscanner.concurrent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class TerminatingQueueTest {
    @Test
    fun emptyHasNextShouldReturnFalseWhenClosed() {
        val queue = TerminatingQueue<String>()
        queue.close()
        val hasNext = queue.iterator().hasNext()
        assertFalse("No next elements are expected!", hasNext)
    }

    @Test
    fun emptyHasNextShouldReturnFalseTwice() {
        val queue = TerminatingQueue<String>()
        queue.close()
        val iterator = queue.iterator()
        assertFalse("No next elements are expected!", iterator.hasNext())
        assertFalse("No next elements are expected!", iterator.hasNext())
    }


    @Test(expected = NoSuchElementException::class)
    fun emptyNextShouldThrow() {
        val queue = TerminatingQueue<String>()
        queue.close()
        queue.iterator().next()
    }

    @Test
    fun emptyHasNextShouldBlockOnEmptyNotclosedQueue() {
        val queue = TerminatingQueue<String>()

        val hasNextFuture:Future<Boolean> = Executors.newSingleThreadExecutor {r-> Thread(r).also { it.isDaemon = true }}
                .submit(object: Callable<Boolean> {
                    override fun call():Boolean {
                        val next = queue.iterator().hasNext()
                        return next
                    }
                })

        try {
            hasNextFuture.get(100, TimeUnit.MILLISECONDS)
            fail("hasNext should block on empty non-closed queue")
        } catch (e: TimeoutException) {
            // Nothing
        }
    }


    @Test
    fun fullHasNextShouldReturnFalseAfterLastWhenClosed() {
        val queue = TerminatingQueue<String>()
        queue.put("el1")
        queue.close()

        val it = queue.iterator()

        assertTrue("No next elements are expected!", it.hasNext())
        assertEquals("el1", it.next())
        assertFalse("Next element is expected!", it.hasNext())
    }


    @Test
    fun fullNextShouldProgress() {
        val queue = TerminatingQueue<String>()
        queue.put("el1")
        queue.put("el2")
        queue.close()

        val it = queue.iterator()

        assertTrue("No next elements are expected!", it.hasNext())
        assertEquals("el1", it.next())
        assertEquals("el2", it.next())
        assertFalse("Next element is expected!", it.hasNext())
    }


}