package info.kgeorgiy.ja.dziuba.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract class for an individual in a bank
 */
public class AbstractPerson implements Serializable, Person {

    /**
     * Data of a person
     */
    protected String name, surname, passport;
    protected final ConcurrentMap<String, Account> relatedAccounts;

    public AbstractPerson(final String name, final String surname, final String passport,
                          final ConcurrentMap<String, Account> relatedAccounts) {
        this.name = name;
        this.passport = passport;
        this.surname = surname;
        this.relatedAccounts = relatedAccounts;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractPerson that = (AbstractPerson) o;
        return name.equals(that.name) && surname.equals(that.surname) &&
                passport.equals(that.passport) && relatedAccounts.equals(that.relatedAccounts);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname(){
        return surname;
    }

    @Override
    public String getPassport(){
        return passport;
    }

    // :NOTE: abstract
    @Override
    public Account createRelatedAccount(final String subId) throws RemoteException {
        return null;
    }

    @Override
    public synchronized Account getRelatedAccount(final String subId) {
        return relatedAccounts.get(getAccountId(subId));
    }

    // :NOTE: Изменяемая коллекция
    @Override
    public ConcurrentMap<String, Account> getRelatedAccounts() {
        return relatedAccounts;
    }

    String getAccountId(final String subId) {
        return passport + ':' + subId;
    }
}
