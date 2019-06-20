package org.grapheus.jarscanner.concurrent

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class TerminatingQueueTest {
    val executorService = Executors.newSingleThreadExecutor()
    
    @Test
    fun emptyHasNextShouldReturnFalseWhenClosedSequentially() {
        val queue = TerminatingQueue<String>()
        queue.close()
        val hasNext = queue.iterator().hasNext()
        assertFalse("No next elements are expected!", hasNext)
    }

    @Test
    fun emptyHasNextShouldReturnFalseWhenClosedInParallel() {
        val queue = TerminatingQueue<String>()

        val hasNextFuture:Future<Boolean> = executorService.submit(object: Callable<Boolean> {
            override fun call() = queue.iterator().hasNext()
        })

        Thread.sleep(1000) //TODO: not good
        queue.close()
        val hasNext = hasNextFuture.get()
        assertFalse("No next elements are expected!", hasNext)
    }


    @Test
    fun hasNextShouldReturnFalseAfterLastWhenClosedSequentially() {
        val queue = TerminatingQueue<String>()
        queue.put("el1")
        queue.close()

        val it = queue.iterator()

        assertTrue("No next elements are expected!", it.hasNext())
        assertEquals("el1", it.next())
        assertFalse("Next element is expected!", it.hasNext())
    }


}