package dijkstra

import java.util.*
import java.util.concurrent.Phaser
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.Comparator
import kotlin.concurrent.thread


fun shortestPathParallel(start: Node) {
    val workers = Runtime.getRuntime().availableProcessors()
    val onFinish = Phaser(workers + 1)

    val multiQueue = MultiQueue(2 * workers) { o1, o2 -> o1!!.distance.compareTo(o2!!.distance) }

    start.distance = 0
    multiQueue.add(start)
    val processingNodes = AtomicInteger(1)

    repeat(workers) {
        thread {
            while (processingNodes.get() > 0) {

                val from: Node = multiQueue.poll() ?: continue

                for (e in from.outgoingEdges) {

                    while (true) {
                        val curDistance = e.to.distance
                        val newDistance = from.distance + e.weight

                        if (curDistance > newDistance) {
                            if (e.to.casDistance(curDistance, newDistance)) {
                                multiQueue.add(e.to)
                                processingNodes.getAndIncrement()
                                break
                            }
                        } else {
                            break
                        }
                    }

                }
                processingNodes.getAndDecrement()

            }
            onFinish.arrive()
        }
    }
    onFinish.arriveAndAwaitAdvance()
}

class MultiQueue(private val n: Int, private val cmp: Comparator<Node>) {

    private val queueList = ArrayList<PriorityBlockingQueue>()

    companion object {
        private val random = Random()
    }

    private val rndIdx: Int
        get() = random.nextInt(n)

    init {
        for (i in 1..n) {
            queueList.add(PriorityBlockingQueue(cmp))
        }
    }

    private inner class PriorityBlockingQueue(cmp: Comparator<Node>) {
        val rLock = ReentrantLock(true)
        val priorityQueue = PriorityQueue(cmp)

        fun peek(): Node? {
            return priorityQueue.peek()
        }
    }


    // Main operations
    fun poll(): Node? {
        while (true) {

            val fst = queueList[rndIdx]
            val snd = queueList[rndIdx]

            if (fst.rLock.tryLock()) {
                try {
                    return if (snd.rLock.tryLock()) {
                        try {
                            minOf(
                                fst,
                                snd,
                                compareBy(nullsLast(cmp), PriorityBlockingQueue::peek)
                            ).priorityQueue.poll()
                        } finally {
                            snd.rLock.unlock()
                        }
                    } else fst.priorityQueue.poll()
                } finally {
                    fst.rLock.unlock()
                }
            }
        }
    }

    fun add(node: Node) {
        while (true) {
            val idx = rndIdx
            val priorityBlockingQueue = queueList[idx]

            if (priorityBlockingQueue.rLock.tryLock()) {
                try {
                    priorityBlockingQueue.priorityQueue.add(node)
                    return
                } finally {
                    priorityBlockingQueue.rLock.unlock()
                }
            }
        }
    }
}