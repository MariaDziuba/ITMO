package info.kgeorgiy.ja.dziuba.bank;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server for bank application
 */
public class Server {

    private static final int PORT = 8090;
    private static final String BANK_ADDRESS = "//localhost/bank";
    private static Registry registry;

    public static void main(String[] args) {
        try {
            final RemoteBank bank = new RemoteBank(PORT);
            try {
                registry = LocateRegistry.createRegistry(PORT);
                UnicastRemoteObject.exportObject(bank, PORT);
                registry.rebind(BANK_ADDRESS, bank);
            } catch (AccessException e) {
                System.err.println("Could not rebind localhost 8080");
                e.printStackTrace();
            }
        } catch (final RemoteException e) {
            System.err.println("Could not export object: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Server started");
    }

    public static void closeServer() {
        try {
            registry.unbind(BANK_ADDRESS);
            System.out.println("Server is down now...");
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Can't down the server: " + e.getMessage());
        }
    }
}
