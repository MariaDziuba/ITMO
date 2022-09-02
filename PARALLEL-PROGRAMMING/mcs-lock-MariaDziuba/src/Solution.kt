import java.util.concurrent.atomic.*

class Solution(private val environment: Environment) : Lock<Solution.Node> {
    val tail = AtomicReference<Node?>(null)

    override fun lock(): Node {
        val newNode = Node(true)
        val nextNode = tail.getAndSet(newNode)?.next ?: return newNode
        nextNode.value = newNode
        while (newNode.isLocked.value) {
            environment.park()
        }
        return newNode
    }

    override fun unlock(node: Node) {
        if (node.next.value == null) {
            if (!tail.compareAndSet(node, null)) {
                while (node.next.value == null) {
                    continue
                }
            } else {
                return
            }
        }
        val next = node.next.value ?: return
        next.isLocked.value = false
        environment.unpark(next.thread)
    }

    class Node(isLocked: Boolean) {
        val isLocked: AtomicReference<Boolean> = AtomicReference(isLocked)
        val next: AtomicReference<Node?> = AtomicReference(null)
        val thread: Thread = Thread.currentThread()
    }
}