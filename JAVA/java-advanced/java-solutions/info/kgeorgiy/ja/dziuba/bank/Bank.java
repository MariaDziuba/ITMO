package info.kgeorgiy.ja.dziuba.bank;

import java.rmi.Remote;

import java.rmi.RemoteException;

/**
 * Bank interface
 */
public interface Bank extends Remote {

    Account createAccountIfMissing(String id) throws RemoteException;

    Account getRemoteAccount(String id) throws RemoteException;

    Account getLocalAccount(String id) throws RemoteException;

    Person createPersonIfMissing(String firstName, String lastName, String passport) throws RemoteException;

    Person getLocalPerson(String passport) throws RemoteException;

    Person getRemotePerson(String passport) throws RemoteException;
}