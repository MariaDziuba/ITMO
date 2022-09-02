import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.atomicArrayOfNulls
import java.util.*
import kotlin.math.max
import kotlin.random.Random

class FCPriorityQueue<E : Comparable<E>> {

    open class PQOp<E>(val value: E? = null)
    private class AddOp<E>(value: E) : PQOp<E>(value)
    private class PollOp<E> : PQOp<E>()
    private class PeekOp<E> : PQOp<E>()

    private class Done<E>(value: E? = null) : PQOp<E>(value)

    private val size = max(Runtime.getRuntime().availableProcessors(), 4) * 32
    private val ops = atomicArrayOfNulls<PQOp<E>>(size)

    private val isLocked: AtomicBoolean = atomic(false)

    private val pq = PriorityQueue<E>()

    private val randomIdx: Int
        get() = Random.nextInt(size)

    private fun lock(): Boolean {
        return isLocked.compareAndSet(update = true, expect = false)
    }

    private fun unlock() {
        isLocked.value = false
    }

    private fun applyOps() {
        for (i in 0 until size) {
            val res = when (val op = ops[i].value) {
                is AddOp -> {
                    pq.add(op.value)
                    Done()
                }
                is PeekOp -> Done(pq.peek())
                is PollOp -> Done(pq.poll())
                else -> continue
            }
            ops[i].value = res
        }
    }

    private fun insertOpAndGetIdx(op: PQOp<E>): Int {
        while (true) {
            val idx = randomIdx
            if (ops[idx].compareAndSet(null, op)) {
                return idx
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <Res> processOp(opType: PQOp<E>, queueOp: () -> Res): Res {
        if (!lock()) {
            val idx = insertOpAndGetIdx(opType)
            while (true) {
                if (lock()) {
                    val opStatus = ops[idx].value
                    ops[idx].value = null
                    val res = if (opStatus !is Done) {
                        queueOp()
                    } else {
                        opStatus.value as Res
                    }
                    applyOps()
                    unlock()
                    return res
                }
                val opStatus = ops[idx].value
                if (opStatus is Done) {
                    ops[idx].value = null
                    return opStatus.value as Res
                }
            }
        } else {
            val res = queueOp()
            applyOps()
            unlock()
            return res
        }
    }

    /**
     * Retrieves the element with the highest priority
     * and returns it as the result of this function;
     * returns `null` if the queue is empty.
     */
    fun poll(): E? {
        return processOp(PollOp()) { pq.poll() }
    }

    /**
     * Returns the element with the highest priority
     * or `null` if the queue is empty.
     */
    fun peek(): E? {
        return processOp(PeekOp()) { pq.peek() }
    }

    /**
     * Adds the specified element to the queue.
     */
    fun add(element: E) {
        processOp(AddOp(element)) { pq.add(element) }
    }
}