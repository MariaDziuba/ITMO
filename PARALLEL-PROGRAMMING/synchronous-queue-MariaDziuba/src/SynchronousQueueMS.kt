import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import java.lang.IllegalStateException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SynchronousQueueMS<E> : SynchronousQueue<E> {

    private inner class Node(type: OperationType? = null, element: E? = null) {
        val element: AtomicRef<E?> = atomic(element)
        val type: AtomicRef<OperationType?> = atomic(type)
        val cont: AtomicRef<Continuation<Any?>?> = atomic(null)
        val next: AtomicRef<Node?> = atomic(null)
    }

    private enum class ResultType {
        UNIT, VALUE
    }

    private enum class OperationType {
        SENDER, RECEIVER
    }

    private val dummyNode = Node()
    private val head = atomic(dummyNode)
    private val tail = atomic(dummyNode)

    private suspend fun enqueue(tail: Node, node: Node): Boolean {
        val res = suspendCoroutine<Any?> sc@{ p ->
            node.cont.value = p
            if (tail.next.compareAndSet(null, node)) {
                this.tail.compareAndSet(tail, node)
            } else {
                this.tail.compareAndSet(tail, tail.next.value!!)
                p.resume("RETRY")
                return@sc
            }
        }
        return res != "RETRY"
    }


    private fun dequeue(head: Node, element: E?): Boolean {
        val headNext = head.next.value ?: return false
        return if (!this.head.compareAndSet(head, headNext)) {
            false
        } else {
            if (element != null) {
                headNext.element.value = element
            }
            headNext.cont.value?.resume(null)
            true
        }
    }

    override suspend fun send(element: E) =
        doAction(element, ResultType.UNIT, OperationType.SENDER) as Unit


    @Suppress("UNCHECKED_CAST")
    override suspend fun receive() =
        doAction(null, ResultType.VALUE, OperationType.RECEIVER) as E


    private suspend fun doAction(
        element: E?, resType: ResultType, operationType: OperationType
    ): Any {
        while (true) {
            val headVal = head.value
            val tailVal = tail.value

            val eNode = Node(operationType, element)

            if (tailVal == headVal || tailVal.type.value == operationType) {
                if (enqueue(tailVal, eNode)) {
                    return when (resType) {
                        ResultType.VALUE -> eNode.element.value as Any
                        ResultType.UNIT -> Unit
                    }
                }
            } else {
                if (tailVal == this.tail.value) {
                    if (dequeue(headVal, element)) {
                        val headNext = headVal.next.value ?: throw IllegalStateException()
                        return when (resType) {
                            ResultType.VALUE -> headNext.element.value as Any
                            ResultType.UNIT -> Unit
                        }
                    }
                }
            }
        }
    }
}
