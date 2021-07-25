package info.kgeorgiy.ja.dziuba.bank;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class for local person, is sent via serialization
 */
public class LocalPerson extends AbstractPerson {

    public LocalPerson(final RemotePerson remotePerson, ConcurrentMap<String, Account> map) {
        super(remotePerson.getName(), remotePerson.getSurname(), remotePerson.getPassport(), map);
    }

    /**
     * Exports related account from {@link RemotePerson}
     * @param remotePerson - {@link RemotePerson} to export from
     * @return - {@link ConcurrentMap} of accounts and their ids
     */
    static ConcurrentMap<String, Account> exportAccounts(final RemotePerson remotePerson) {
        final ConcurrentHashMap<String, Account> relatedAccounts = new ConcurrentHashMap<>();
        remotePerson.relatedAccounts.forEach((key, account) ->
                {
                    try {
                        relatedAccounts.put(key, new LocalAccount(account.getId(), account.getAmount()));
                        // :NOTE: Исключения
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
        );
        return relatedAccounts;
    }

    /**
     * Creates related account for current {@link LocalPerson}
     * @param subId - subId of {@link Account}
     * @return - created {@link Account}
     */
    @Override
    public synchronized Account createRelatedAccount(final String subId) {
        final String id = getAccountId(subId);
        return relatedAccounts.computeIfAbsent(id, (idTmp) -> new LocalAccount(id, 0));
    }
}
