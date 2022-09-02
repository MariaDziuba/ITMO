/**
 * @author : Dziuba Maria
 */
class Solution : AtomicCounter {

    private val last: ThreadLocal<Node> = ThreadLocal()
    private val root: Node = Node(0)

    private fun getAndAddImpl(value : Int) : Int {
        while (true) {
            val prev = last.get().value
            val cur = prev + value
            val curNode = Node(cur)
            val localLast = last.get().next.decide(curNode)
            last.set(localLast)
            if (curNode == localLast) {
                return prev
            }
        }
    }

    override fun getAndAdd(x: Int): Int {
        if (last.get() == null) {
            last.set(root)
        }
        return getAndAddImpl(x)
    }

    /**
     * Node class which represents current value and
     * consensus field to decide which operation we should perform in concurrent case.
     */
    private class Node(a : Int) {
        val value : Int  = a
        val next :  Consensus<Node> =  Consensus()
    }
}