package info.kgeorgiy.ja.dziuba.bank;

import java.io.UncheckedIOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class representing an individual, is sent via RMI
 */
class RemotePerson extends AbstractPerson {
    private final Bank bank;

    public RemotePerson(final String firstName, final String lastName, final String passport, final Bank bank) {
        super(firstName, lastName, passport, new ConcurrentHashMap<>());
        this.bank = bank;
    }

    /**
     * Created related account of current {@link RemotePerson}
     * @param subId - subId of {@link Account}
     * @return - created {@link Account}
     * @throws RemoteException - if error occurs during the execution of a remote method call.
     */
    @Override
    public Account createRelatedAccount(final String subId) throws RemoteException {
        final String id = getAccountId(subId);
        try {
            return relatedAccounts.computeIfAbsent(id, accountId -> {
                try {
                    return bank.createAccountIfMissing(accountId);
                } catch (final RemoteException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (final UncheckedIOException e) {
            throw new RemoteException("Failed to create related account", e.getCause());
        }
    }
}
