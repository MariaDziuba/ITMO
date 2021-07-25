package info.kgeorgiy.ja.dziuba.bank;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class that works with individuals. If there is not any information about an
 * individual, it should be added. Else their data should be checked. If an individual
 * does not have an account with given number, it is created with zero balance. After
 * updating amount of the account, new balance is printed to console.
 */
public class Client {

    private static final String USAGE = "USAGE: <[name] [surname] [passport number] " +
            "[account number] [change in account's amount]";

    private static final int PORT = 8090;
    private static final String BANK_ADDRESS = "//localhost/bank";

    /**
     * Validates command line arguments
     *
     * @param args - command line arguments
     * @return true if args are okay, else false
     */
    private static boolean validateArgs(String[] args) {
        if (args == null || args.length < 1 || args.length > 5) {
            System.err.println("Wrong arguments. " + USAGE);
            return false;
        }
        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Arguments must be non-null");
            return false;
        }
        try {
            Integer.parseInt(args[4]);
        } catch (final NumberFormatException ignored) {
            System.err.println("Argument №" + 4 + " is not an integer value");
            return false;
        }
        return true;
    }

    /**
     * Starts client
     *
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        if (!validateArgs(args)) {
            return;
        }
        try {
            final Bank bank;
            Registry registry = LocateRegistry.getRegistry(PORT);
            bank = (Bank) registry.lookup(BANK_ADDRESS);

            String name = args[0];
            String surname = args[1];
            String passport = args[2];
            String accountId = args[3];
            int amount = Integer.parseInt(args[4]);

            Person person = bank.createPersonIfMissing(passport, name, surname);
            System.out.println("Person's surname: " + person.getSurname());
            System.out.println("Person's name: " + person.getName());
            System.out.println("Person's passport: " + person.getPassport());

            Account account = bank.createAccountIfMissing(accountId);
            if (account != null) {
                System.out.println("Account id: " + account.getId());
                System.out.println("Amount: " + account.getAmount());
                System.out.println("Adding " + amount);
                account.addAmount(amount);
                System.out.println("After adding: " + account.getAmount());
            } else {
                System.err.println("Could not create and get account!");
            }
        } catch (final NotBoundException | RemoteException e) {
            System.err.println("Could not find server URL");
            System.err.println(e.getMessage());
        }
    }
}