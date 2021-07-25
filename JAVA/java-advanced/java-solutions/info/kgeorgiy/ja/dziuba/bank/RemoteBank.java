package info.kgeorgiy.ja.dziuba.bank;

import java.io.UncheckedIOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Class for remote bank
 */
public class RemoteBank implements Bank {

    private final int port;
    private final ConcurrentMap<String, RemoteAccount> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RemotePerson> people = new ConcurrentHashMap<>();

    public RemoteBank(final int port) {
        this.port = port;
    }

    /**
     * Getter for {@link RemoteAccount}
     * @param id - id of needed account
     * @return - needed {@link RemoteAccount}
     */
    @Override
    public Account getRemoteAccount(final String id) {
        return accounts.get(id);
    }

    /**
     * Getter for {@link LocalAccount}
     * @param id - id of needed account
     * @return - needed {@link LocalAccount}
     */
    @Override
    public Account getLocalAccount(final String id) throws RemoteException {
        if (!accounts.containsKey(id)) {
            return null;
        } else {
            final Account account = accounts.get(id);
            return new LocalAccount(account.getId(), account.getAmount());
        }
    }

     private <T extends Remote> T addInstance(final String id, final Function<String, T> function,
                                              final ConcurrentMap<String, T> map) throws RemoteException {
        try {
            return map.computeIfAbsent(id, instanceId -> {
                try {
                    final T instance = function.apply(instanceId);
                    UnicastRemoteObject.exportObject(instance, port);
                    return instance;
                } catch (final RemoteException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (final UncheckedIOException e) {
            throw new RemoteException("Could not export instance", e);
        }
    }

    /**
     * Creates an {@link Account} if and only if it is absent in bank.
     * @param id - number of a bank account
     * @return - created {@link Account}
     * @throws RemoteException if error occurs during the execution of a remote method call.
     */
    @Override
    public Account createAccountIfMissing(final String id) throws RemoteException {
        // :NOTE: Вернуть счёт?
        return addInstance(id, RemoteAccount::new, accounts);
    }

    /**
     * Creates a {@link Person} if and only if it is absent in bank.
     *
     * @param name - name of a person
     * @param surname - surname of a person
     * @param passport - passport data of a person
     * @return - created {@link Person}
     * @throws RemoteException if error occurs during the execution of a remote method call.
     */
    @Override
    public Person createPersonIfMissing(final String name, final String surname, final String passport)
            throws RemoteException {
        return addInstance(passport, p -> new RemotePerson(name, surname, p, this), people);
    }

    /**
     * Getter for {@link LocalPerson}
     * @param passport - passport data of needed person
     * @return - needed {@link LocalPerson}
     */
    @Override
    public Person getLocalPerson(final String passport) {
        if (!people.containsKey(passport)) {
            return null;
        } else {
            RemotePerson person = people.get(passport);
            return new LocalPerson(person, LocalPerson.exportAccounts(person));
        }
    }

    /**
     * Getter for {@link RemotePerson}
     * @param passport - passport data of needed person
     * @return - needed {@link RemotePerson}
     */
    @Override
    public Person getRemotePerson(final String passport) {
        return people.get(passport);
    }
}