import kotlinx.atomicfu.*

class FAAQueue<T> {
    private val head: AtomicRef<Segment> // Head pointer, similarly to the Michael-Scott queue (but the first node is _not_ sentinel)
    private val tail: AtomicRef<Segment> // Tail pointer, similarly to the Michael-Scott queue

    init {
        val initialNode = Segment()
        head = atomic(initialNode)
        tail = atomic(initialNode)
    }

    /**
     * Adds the specified element [x] to the queue.
     */
    fun enqueue(x: T) {
        while (true) {
            val curTail = moveTail()
            val enqIndex = curTail.enqIndex.getAndIncrement()
            if (enqIndex >= SEGMENT_SIZE) {
                val newSegment = Segment(x)
                val isEnqueued  = curTail.next.compareAndSet(null,newSegment)
                this.tail.compareAndSet(curTail, curTail.next.value!!)
                if (isEnqueued) {
                    return
                }
            } else {
                if (curTail.elements[enqIndex].compareAndSet(null, x)) {
                    return
                }
            }
        }
    }

    /**
     * Retrieves the first element from the queue
     * and returns it; returns `null` if the queue
     * is empty.
     */
    @Suppress("UNCHECKED_CAST")
    fun dequeue(): T? {
        while (true) {
            val curHead = head.value
            if (curHead.isEmpty) {
                val nextHead = curHead.next.value ?: return null
                this.head.compareAndSet(curHead, nextHead)
            } else {
                val deqIdx = curHead.deqIndex.getAndIncrement()
                if (deqIdx >= SEGMENT_SIZE) {
                    continue
                }
                val elem = curHead.elements[deqIdx].getAndSet(DONE) ?: continue
                return elem as? T
            }
        }
    }

    /**
     * Returns `true` if this queue is empty;
     * `false` otherwise.
     */
    val isEmpty: Boolean
        get() {
            while (true) {
                val curHead = head.value
                return if (head.value.isEmpty) {
                    val next = curHead.next.value
                    if (next == null) {
                        true
                    } else {
                        head.compareAndSet(curHead, next)
                        continue
                    }
                } else {
                    false
                }
            }
        }

    private fun moveTail(): Segment {
        val curTail = tail.value
        val nextTail = curTail.next.value ?: return curTail
        this.tail.compareAndSet(curTail, nextTail)
        return moveTail()
    }
}

private class Segment {
    val next: AtomicRef<Segment?> = atomic(null)
    val enqIndex: AtomicInt = atomic(0) // index for the next enqueue operation
    val deqIndex: AtomicInt = atomic(0) // index for the next dequeue operation
    val elements: AtomicArray<Any?> = atomicArrayOfNulls(SEGMENT_SIZE)

    constructor() // for the first segment creation

    constructor(x: Any?) { // each next new segment should be constructed with an element
        enqIndex.value = 1
        elements[0].value = x
    }

    val isEmpty: Boolean
        get() = deqIndex.value >= SEGMENT_SIZE
}

private val DONE = Any() // Marker for the "DONE" slot state; to avoid memory leaks
private const val SEGMENT_SIZE = 2 // DO NOT CHANGE, IMPORTANT FOR TESTS