package info.kgeorgiy.ja.dziuba.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Account interface of individual in a bank
 */
public interface Account extends Remote {

    String getId() throws RemoteException;

    int getAmount() throws RemoteException;

    void addAmount(int amount) throws RemoteException;

    void setAmount(final int amount) throws RemoteException;
}