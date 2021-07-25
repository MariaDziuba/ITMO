package info.kgeorgiy.ja.dziuba.bank;

import java.io.Serializable;
import java.util.Objects;

// :NOTE: Наследование
public abstract class AbstractAccount implements Serializable, Account {

    private final String id;
    private int amount;

    public AbstractAccount(final String id) {
        this(id, 0);
    }

    public AbstractAccount(final String id, final int amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractAccount that = (AbstractAccount) o;
        return amount == that.amount && id.equals(that.id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized int getAmount() {
        return amount;
    }

    @Override
    public synchronized void setAmount(final int amount) {
        this.amount = amount;
    }

    @Override
    public synchronized void addAmount(int income) {
        this.amount += income;
    }
}