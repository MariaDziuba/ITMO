import kotlinx.atomicfu.*

/**
 * Atomic block.
 */
fun <T> atomic(block: TxScope.() -> T): T {
    while (true) {
        val transaction = Transaction()
        try {
            val result = block(transaction)
            if (transaction.commit()) return result
            transaction.abort()
        } catch (e: AbortException) {
            transaction.abort()
        }
    }
}

/**
 * Transactional operations are performed in this scope.
 */
abstract class TxScope {
    abstract fun <T> TxVar<T>.read(): T
    abstract fun <T> TxVar<T>.write(x: T): T
}

/**
 * Transactional variable.
 */
class TxVar<T>(initial: T) {
    private val loc = atomic(Loc(initial, initial, rootTx))

    /**
     * Opens this transactional variable in the specified transaction [tx] and applies
     * updating function [update] to it. Returns the updated value.
     */
    @Suppress("UNCHECKED_CAST")
    fun openIn(tx: Transaction, update: (T) -> T): T {
        while (true) {
            val curLoc = loc.value
            val curVal = curLoc.valueIn(tx) { owner -> owner.abort() }
            if (curVal !== TxStatus.ACTIVE) {
                val updatedVal = update(curVal as T)
                val nLoc = Loc(curVal, updatedVal, tx)
                if (loc.compareAndSet(curLoc, nLoc)) {
                    if (tx.status != TxStatus.ABORTED) {
                        return updatedVal
                    } else {
                        throw AbortException
                    }
                }
            } else {
                continue
            }
        }
    }
}

/**
 * State of transactional value
 */
private class Loc<T>(
    val oldValue: T,
    val newValue: T,
    val owner: Transaction
) {
    fun valueIn(tx: Transaction, onActiveAction: (Transaction) -> Unit): Any? =
        when (owner !== tx) {
            true -> when (owner.status) {
                TxStatus.ACTIVE -> {
                    onActiveAction(owner)
                    TxStatus.ACTIVE
                }
                TxStatus.COMMITTED -> newValue
                TxStatus.ABORTED -> oldValue
            }
            else -> newValue
        }
}

private val rootTx = Transaction().apply { commit() }

/**
 * Transaction status.
 */
enum class TxStatus { ACTIVE, COMMITTED, ABORTED }

/**
 * Transaction implementation.
 */
class Transaction : TxScope() {
    private val _status = atomic(TxStatus.ACTIVE)
    val status: TxStatus get() = _status.value

    fun commit(): Boolean =
        _status.compareAndSet(TxStatus.ACTIVE, TxStatus.COMMITTED)

    fun abort() {
        _status.compareAndSet(TxStatus.ACTIVE, TxStatus.ABORTED)
    }

    override fun <T> TxVar<T>.read(): T = openIn(this@Transaction) { it }
    override fun <T> TxVar<T>.write(x: T) = openIn(this@Transaction) { x }
}

/**
 * This exception is thrown when transaction is aborted.
 */
private object AbortException : Exception() {
    override fun fillInStackTrace(): Throwable = this
}