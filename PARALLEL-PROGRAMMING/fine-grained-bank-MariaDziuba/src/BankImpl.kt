import java.util.concurrent.locks.ReentrantLock

/**
 * Bank implementation.
 *
 * @author : Dziuba Maria
 */
class BankImpl(n: Int) : Bank {
    /**
     * An array of accounts by index.
     */
    private val accounts: Array<Account> = Array(n) { Account() }
    override val numberOfAccounts: Int
        get() = accounts.size


    override fun getAmount(index: Int): Long {
        val acc = accounts[index]
        acc.lockAccount()
        val amount = acc.amount
        acc.unlockAccount()
        return amount
    }

    override val totalAmount: Long
        get() {
            accounts.forEach{ acc ->
                acc.lockAccount()
            }
            val sum : Long = accounts.sumOf { acc ->
                acc.amount
            }
            accounts.forEach{ acc ->
                acc.unlockAccount()
            }
            return sum
        }

    override fun deposit(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val acc = accounts[index]
        acc.lockAccount()
        check(!(acc.amount + amount > Bank.MAX_AMOUNT || amount > Bank.MAX_AMOUNT)) { "Overflow" }
        acc.amount += amount
        val newAmount = acc.amount
        acc.unlockAccount()
        return newAmount
    }

    override fun withdraw(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val acc = accounts[index]
        acc.lockAccount()
        try {
            if (acc.amount - amount < 0) {
                "Underflow"
            }
            acc.amount -= amount
            return acc.amount
        } finally {
            acc.unlockAccount()
        }
    }

    override fun transfer(fromIndex: Int, toIndex: Int, amount: Long) {
        require(amount > 0) { "Invalid amount: $amount" }
        require(toIndex != fromIndex) { "fromIndex == toIndex" }
        val fst = accounts[fromIndex]
        val snd = accounts[toIndex]
        while (true) {
            fst.lockAccount()
            if (snd.lock.tryLock()) {
                break
            }
            fst.unlockAccount()
        }

        if (amount > fst.amount) {
            snd.unlockAccount()
            fst.unlockAccount()
            throw IllegalStateException("Underflow")
        } else {
            if (amount > Bank.MAX_AMOUNT || amount + snd.amount > Bank.MAX_AMOUNT) {
                snd.unlockAccount()
                fst.unlockAccount()
                throw IllegalStateException("Overflow")
            }
        }

        fst.amount -= amount
        snd.amount += amount
        snd.unlockAccount()
        fst.unlockAccount()
    }

    /**
     * Private account data structure.
     */
    internal class Account {
        /**
         * Amount of funds in this account.
         */
        var amount: Long = 0
        fun lockAccount() {
            lock.lock()
        }

        fun unlockAccount() {
            lock.unlock()
        }

        val lock = ReentrantLock(true)
    }
}