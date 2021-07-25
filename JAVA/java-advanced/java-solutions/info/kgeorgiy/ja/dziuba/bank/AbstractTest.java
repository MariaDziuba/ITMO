package info.kgeorgiy.ja.dziuba.bank;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public abstract class AbstractTest extends Assert {
    protected static Bank bank;

    private static final int BANK_PORT = 8888;
    private static final String BANK_ADDRESS = "//localhost:8888/bank";
    static final String NAME = "Maria";
    static final String SURNAME = "Dziuba";
    static final String PASSPORT = "12345678";
    static final String SUB_ID = "0000";
    static final String ACCOUNT_ID = "999999";
    private static final int ACCOUNT_INCOME = 1000;

    @BeforeClass
    public static void beforeAll() throws RemoteException {
        createRegistry();
    }

    @Before
    public void beforeEach() throws RemoteException, MalformedURLException {
        bank = new RemoteBank(BANK_PORT);
        UnicastRemoteObject.exportObject(bank, BANK_PORT);
        Naming.rebind(BANK_ADDRESS, bank);
    }

    static List<String> generateTestIds(final int cnt) {
        return IntStream.range(0, cnt).mapToObj(i -> "11111" + i).collect(Collectors.toCollection(ArrayList::new));
    }

    static void createRegistry() throws RemoteException {
        LocateRegistry.createRegistry(BANK_PORT);
    }

    public static Account createRemoteAccount(final String id) throws RemoteException {
        final Account account = bank.createAccountIfMissing(id);
        assertNotNull(account);
        return account;
    }

    static Account relatedAccount(final String passport, final String subId) throws RemoteException {
        final Person person = bank.getRemotePerson(passport);
        assertNotNull(person);
        final Account account = person.getRelatedAccount(subId);
        assertNotNull(account);
        return account;
    }


    static Account relatedAccount(final Person person) throws RemoteException {
        final Account account = person.getRelatedAccount(SUB_ID);
        assertNotNull(account);
        return account;
    }

    static Account createPersonWithRelatedAccount() throws RemoteException {
        final Person person = createPerson();
        final Account account = addRelatedAccount(person);
        account.setAmount(account.getAmount() + ACCOUNT_INCOME);
        return account;
    }

    private static void createMultipleAccounts(final List<String> ids) throws RemoteException {
        try {
            for (String id : ids) {
                try {
                    bank.createAccountIfMissing(id);
                } catch (final RemoteException e) {
                    throw new UncheckedIOException(e);
                }
            }
        } catch (final UncheckedIOException e) {
            throw new RemoteException("Remote exception occurred", e);
        }
    }

    static Person createPerson() throws RemoteException {
        final Person person = bank.createPersonIfMissing(NAME, SURNAME, PASSPORT);
        assertNotNull(person);
        return person;
    }

    private static void createMultiplePersonsWithMultipleAccounts(final List<String> passports,
                                                                  final List<String> subIds)
            throws RemoteException {
        final RemoteException exception = new RemoteException();

        try {
            passports.forEach(passport -> {
                try {
                    final Person person = bank.createPersonIfMissing(NAME, SURNAME, passport);
                    assertNotNull(person);
                    subIds.forEach(subId -> {
                        try {
                            assertNotNull(person.createRelatedAccount(subId));
                        } catch (final RemoteException e) {
                            exception.addSuppressed(e);
                        }
                    });
                } catch (final RemoteException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (final UncheckedIOException e) {
            throw new RemoteException("Remote exception occurred", e.getCause());
        }
    }

    static Account addRelatedAccount(final Person person) throws RemoteException {
        final Account account = person.createRelatedAccount(SUB_ID);
        assertNotNull(account);
        return account;
    }

    public static Account getLocalAccount() throws RemoteException {
        final Account account = bank.getLocalAccount(ACCOUNT_ID);
        assertNotNull(account);
        return account;
    }

    static Account getRemoteAccount(final String id) throws RemoteException {
        final Account account = bank.getRemoteAccount(id);
        assertNotNull(account);
        return account;
    }

    static void checkAccountAmounts(final int countOfAccounts, final List<String> ids,
                                    final Function<Integer, Integer> expected) throws RemoteException {
        for (int i = 0; i < countOfAccounts; i++) {
            final Account account = getRemoteAccount(ids.get(i));
            assertEquals((int) expected.apply(i), account.getAmount());
        }
    }

    static void checkPersonAccountAmounts(final int countOfPersons, final int countOfAccounts,
                                          final List<String> passports,
                                          final List<String> subIds,
                                          final BiFunction<Integer, Integer, Integer> expected)
            throws RemoteException {
        try {
            IntStream.range(0, countOfPersons).forEach(i -> IntStream.range(0, countOfAccounts).forEach(j -> {
                try {
                    final Account account = relatedAccount(passports.get(i), subIds.get(j));
                    assertEquals((int) expected.apply(i, j), account.getAmount());
                } catch (final RemoteException e) {
                    throw new UncheckedIOException(e);
                }
            }));
        } catch (final UncheckedIOException e) {
            throw new RemoteException("Remote exception occurred", e.getCause());
        }
    }


    static void checkDefaultPerson(final Person person) throws RemoteException {
        assertNotNull(person);
        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
        assertEquals(PASSPORT, person.getPassport());
    }


    static void checkParticularPerson(final Person person, String passport) throws RemoteException {
        assertNotNull(person);
        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
        assertEquals(passport, person.getPassport());
    }

    static void checkSynchronizationOfAccounts(final Account account1, final Account account2) throws RemoteException {
        assertNotNull(account1);
        assertNotNull(account2);

        assertEquals(ACCOUNT_INCOME, account1.getAmount());
        assertEquals(ACCOUNT_INCOME, account2.getAmount());

        account2.setAmount(account2.getAmount() + ACCOUNT_INCOME);
        assertEquals(2 * ACCOUNT_INCOME, account1.getAmount());
        assertEquals(2 * ACCOUNT_INCOME, account2.getAmount());
    }

    static void checkDesynchronizationOfAccounts(final Account account1, final Account account2) throws RemoteException {
        assertNotNull(account1);
        assertNotNull(account2);

        assertEquals(ACCOUNT_INCOME, account1.getAmount());
        assertEquals(ACCOUNT_INCOME, account2.getAmount());

        account1.setAmount(2 * ACCOUNT_INCOME);
        assertEquals(2 * ACCOUNT_INCOME, account1.getAmount());
        assertEquals(ACCOUNT_INCOME, account2.getAmount());

        account2.setAmount(3 * ACCOUNT_INCOME);
        assertEquals(2 * ACCOUNT_INCOME, account1.getAmount());
        assertEquals(3 * ACCOUNT_INCOME, account2.getAmount());
    }

    static void checkLocalAndRemoteBehavior(final Account remoteAccount, final Account localAccount1,
                                            final Account localAccount2) throws RemoteException {
        remoteAccount.setAmount(ACCOUNT_INCOME);

        assertEquals(ACCOUNT_INCOME, remoteAccount.getAmount());
        assertEquals(0, localAccount1.getAmount());
        assertEquals(0, localAccount2.getAmount());

        localAccount1.setAmount(2 * ACCOUNT_INCOME);

        assertEquals(ACCOUNT_INCOME, remoteAccount.getAmount());
        assertEquals(2 * ACCOUNT_INCOME, localAccount1.getAmount());
        assertEquals(0, localAccount2.getAmount());

        localAccount2.setAmount(3 * ACCOUNT_INCOME);

        assertEquals(ACCOUNT_INCOME, remoteAccount.getAmount());
        assertEquals(2 * ACCOUNT_INCOME, localAccount1.getAmount());
        assertEquals(3 * ACCOUNT_INCOME, localAccount2.getAmount());
    }

    static void multiThreadOperation(final int countOfThreads, final int requestsPerItem, final int countOfPersons,
                                     final int countOfAccounts, final BiConsumer<Integer, Integer> jobs) throws RemoteException, InterruptedException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(countOfThreads);

        IntStream.range(0, requestsPerItem).forEach(u -> IntStream.range(0, countOfPersons)
                .forEach(i -> IntStream.range(0, countOfAccounts)
                        .forEach(j -> threadPool.submit(() -> jobs.accept(i, j)))));

        threadPool.shutdown();
        while (!threadPool.awaitTermination(1, TimeUnit.MILLISECONDS)) ;
    }

    static void multiThreadAccountQueries(final int countOfThreads, final int requestsPerItem,
                                          final int countOfAccounts) throws InterruptedException, RemoteException {
        final List<Integer> incomes = IntStream.range(0, countOfAccounts).boxed()
                .collect(Collectors.toCollection(ArrayList::new));

        final List<String> ids = generateTestIds(countOfAccounts);

        createMultipleAccounts(ids);

        multiThreadOperation(countOfThreads, requestsPerItem, 1, countOfAccounts, (i, j) -> {
            try {
                final Account account = getRemoteAccount(ids.get(j));
                account.addAmount(incomes.get(j));
            } catch (final RemoteException e) {
                throw new UncheckedIOException(e);
            }
        });

        checkAccountAmounts(countOfAccounts, ids, i -> incomes.get(i) * requestsPerItem);
    }


    static void multiThreadPersonQueries(final int countOfThreads, final int requestsPerItem, final int countOfPersons,
                                         final int countOfAccounts) throws InterruptedException, RemoteException {


        final List<List<Integer>> incomes = IntStream.range(0, countOfPersons).boxed()
                .map(i -> IntStream.range(i * countOfAccounts + 3, (i + 2) * countOfAccounts + 3).boxed()
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(ArrayList::new));
        List<String> passports = generateTestIds(countOfPersons);
        List<String> subIds = generateTestIds(countOfAccounts);

        multiThreadOperation(countOfThreads, requestsPerItem, countOfPersons, countOfAccounts, (passports_, subIds_) ->
        {
            try {
                createMultiplePersonsWithMultipleAccounts(passports, subIds);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        for (int i = 0; i < countOfPersons; i++) {
            checkParticularPerson(bank.getRemotePerson(passports.get(i)), passports.get(i));
        }

        multiThreadOperation(countOfThreads, requestsPerItem, countOfPersons, countOfAccounts, (i, j) -> {
            try {
                final Account account = relatedAccount(passports.get(i), subIds.get(j));
                account.addAmount(incomes.get(i).get(j));
            } catch (final RemoteException e) {
                throw new UncheckedIOException(e);
            }
        });

        checkPersonAccountAmounts(countOfPersons, countOfAccounts, passports, subIds,
                (i, j) -> incomes.get(i).get(j) * requestsPerItem);
    }
}