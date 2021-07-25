package info.kgeorgiy.ja.dziuba.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentMap;

/**
 * Interface of an individual in a bank
 */
public interface Person extends Remote {

    String getName() throws RemoteException;

    String getSurname() throws RemoteException;

    String getPassport() throws RemoteException;

    Account createRelatedAccount(String subId) throws RemoteException;

    Account getRelatedAccount(String subId) throws RemoteException;

    ConcurrentMap<String, Account> getRelatedAccounts() throws RemoteException;
}