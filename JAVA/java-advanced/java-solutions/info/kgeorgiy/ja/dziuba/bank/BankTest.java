package info.kgeorgiy.ja.dziuba.bank;

import org.junit.Test;

import java.io.*;
import java.rmi.RemoteException;

public class BankTest extends AbstractTest {

    @Test
    public void test00_serialization() throws RemoteException {
        final RemotePerson remotePerson = (RemotePerson) bank.createPersonIfMissing(NAME, SURNAME, PASSPORT);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            new ObjectOutputStream(outputStream)
                    .writeObject(new LocalPerson(remotePerson, remotePerson.getRelatedAccounts()));
        } catch (final IOException | SecurityException | NullPointerException e) {
            fail("Serialization failed");
        }

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        try {
            final LocalPerson localPerson = (LocalPerson) new ObjectInputStream(inputStream).readObject();
            assertEquals(remotePerson.getName(), localPerson.getName());
            assertEquals(remotePerson.getSurname(), localPerson.getSurname());
            assertEquals(remotePerson.getPassport(), localPerson.getPassport());
            assertEquals(remotePerson.getRelatedAccounts(), localPerson.getRelatedAccounts());

        } catch (final IOException | ClassNotFoundException e) {
            fail("Deserialization failed");
        }
    }

    @Test
    public void test01_createAccount() throws RemoteException {
        final Account account = createRemoteAccount(ACCOUNT_ID);
        assertEquals(ACCOUNT_ID, account.getId());
        assertEquals(0, account.getAmount());
    }

    @Test
    public void test02_nonExistingAccount() throws RemoteException {
        assertNull(bank.getRemoteAccount(ACCOUNT_ID));
    }

    @Test

    public void test03_alreadyExistingAccount() throws RemoteException {
        final int amount = 100;

        final Account account1 = createRemoteAccount(ACCOUNT_ID);
        account1.setAmount(amount);
        final Account account2 = createRemoteAccount(ACCOUNT_ID);
        assertEquals(amount, account2.getAmount());
    }

    @Test
    public void test04_createAndGetAccount() throws RemoteException {
        bank.createAccountIfMissing(ACCOUNT_ID);
        final Account account = getRemoteAccount(ACCOUNT_ID);
        assertEquals(ACCOUNT_ID, account.getId());
        assertEquals(0, account.getAmount());
    }

    @Test
    public void test05_setAmount() throws RemoteException {
        final int amount = 100;

        final Account account = createRemoteAccount(ACCOUNT_ID);
        account.setAmount(amount);
        assertEquals(amount, account.getAmount());
    }

    @Test
    public void test06_addAmount() throws RemoteException {
        final int amount = 100;

        final Account account = createRemoteAccount(ACCOUNT_ID);
        account.setAmount(amount);
        assertEquals(amount, account.getAmount());
        account.addAmount(amount);
        assertEquals(2 * amount, account.getAmount());
    }

    @Test
    public void test07_remoteAccountSynchronization() throws RemoteException {
        final int amount1 = 100;
        final int amount2 = 200;

        final Account account1 = createRemoteAccount(ACCOUNT_ID);
        final Account account2 = createRemoteAccount(ACCOUNT_ID);

        account1.setAmount(amount1);
        assertEquals(amount1, account1.getAmount());
        assertEquals(amount1, account2.getAmount());

        account2.setAmount(amount2);
        assertEquals(amount2, account1.getAmount());
        assertEquals(amount2, account2.getAmount());
    }

    @Test
    public void test08_remoteAndLocalAccountsDesynchronization() throws RemoteException {
        final Account remoteAccount = createRemoteAccount(ACCOUNT_ID);
        final Account localAccount1 = getLocalAccount();
        final Account localAccount2 = getLocalAccount();

        checkLocalAndRemoteBehavior(remoteAccount, localAccount1, localAccount2);
    }

    @Test
    public void test09_multipleAccounts() throws InterruptedException, RemoteException {
        multiThreadAccountQueries(1, 100, 10);
    }

    @Test
    public void test10_multiThreadRequestsSingle() throws InterruptedException, RemoteException {
        multiThreadAccountQueries(10, 10, 1);
    }

    @Test
    public void test11_multipleAccountMultiThread() throws InterruptedException, RemoteException {
        multiThreadAccountQueries(10, 10, 10);
    }


    @Test
    public void test12_createPerson() throws RemoteException {
        final Person person = bank.createPersonIfMissing(NAME, SURNAME, PASSPORT);
        checkDefaultPerson(person);
    }

    @Test
    public void test13_createRelatedAccount() throws RemoteException {
        final Person person = createPerson();
        final Account account = addRelatedAccount(person);
        assertEquals(0, account.getAmount());
        assertEquals(person.getPassport() + ":" + SUB_ID, account.getId());
    }

    @Test
    public void test14_nonExistingRemotePerson() throws RemoteException {
        final Person person = bank.getRemotePerson(PASSPORT);
        assertNull(person);
    }

    @Test
    public void test15_nonExistingLocalPerson() throws RemoteException {
        final Person person = bank.getLocalPerson(PASSPORT);
        assertNull(person);
    }

    @Test
    public void test16_nonExistingRelatedAccount() throws RemoteException {
        final Person person = createPerson();
        addRelatedAccount(person);
    }

    @Test
    public void test17_alreadyExistingPerson() throws RemoteException {
        final int amount = 100;

        final Person person1 = createPerson();
        final Person person2 = createPerson();

        final Account account1 = addRelatedAccount(person1);
        account1.setAmount(amount);

        final Account account2 = addRelatedAccount(person2);
        assertEquals(amount, account1.getAmount());
        assertEquals(amount, account2.getAmount());
        assertEquals(person1.getPassport() + ":" + SUB_ID, account1.getId());
        assertEquals(person2.getPassport() + ":" + SUB_ID, account2.getId());
    }

    @Test
    public void test18_remotePersonLookup() throws RemoteException {
        createPerson();
        final Person person = bank.getRemotePerson(PASSPORT);
        checkDefaultPerson(person);
    }

    @Test
    public void test19_localPersonLookup() throws RemoteException {
        createPerson();
        final Person person = bank.getLocalPerson(PASSPORT);
        checkDefaultPerson(person);
    }

    @Test
    public void test20_alreadyExistingLocalPerson() throws RemoteException {
        createPerson();
        final Person person2 = bank.createPersonIfMissing("Oleg", "Dziuba", PASSPORT);
        assertNotNull(person2);
        checkDefaultPerson(person2);
    }

    @Test
    public void test21_accountBonding() throws RemoteException {
        final int amount = 100;

        final Account account1 = createRemoteAccount(PASSPORT + ":" + SUB_ID);
        account1.setAmount(amount);
        final Person person = createPerson();
        assertNull(person.getRelatedAccount(SUB_ID));

        final Account account2 = addRelatedAccount(person);
        assertEquals(amount, account2.getAmount());
    }

    @Test
    public void test22_remotePersonSynchronizationWithBank() throws RemoteException {
        final Account account1 = createPersonWithRelatedAccount();
        final Account account2 = bank.getRemoteAccount(PASSPORT + ":" + SUB_ID);
        checkSynchronizationOfAccounts(account1, account2);
    }

    @Test
    public void test23_remotePersonSynchronizationAmongPersons() throws RemoteException {
        final Account account1 = createPersonWithRelatedAccount();
        final Person person = createPerson();
        final Account account2 = person.getRelatedAccount(SUB_ID);
        checkSynchronizationOfAccounts(account1, account2);
    }

    @Test
    public void test24_localPersonDesynchronizationWithBank() throws RemoteException {
        createPersonWithRelatedAccount();
        final Person person = bank.getLocalPerson(PASSPORT);
        assertNotNull(person);
        final Account account1 = bank.getRemoteAccount(PASSPORT + ":" + SUB_ID);
        final Account account2 = person.getRelatedAccount(SUB_ID);

        checkDesynchronizationOfAccounts(account1, account2);
    }

    @Test
    public void test25_localPersonDesynchronizationAmongPersons() throws RemoteException {
        final Account account1 = createPersonWithRelatedAccount();
        final Person person = bank.getLocalPerson(PASSPORT);
        assertNotNull(person);
        final Account account2 = person.getRelatedAccount(SUB_ID);

        checkDesynchronizationOfAccounts(account1, account2);
    }

    @Test
    public void test26_localAndRemotePersonsIndependence() throws RemoteException {
        final Person person = createPerson();
        addRelatedAccount(person);

        final Person remotePerson = bank.getRemotePerson(PASSPORT);
        assertNotNull(remotePerson);
        final Person localPerson1 = bank.getLocalPerson(PASSPORT);
        assertNotNull(localPerson1);
        final Person localPerson2 = bank.getLocalPerson(PASSPORT);
        assertNotNull(localPerson1);

        final Account remoteAccount = relatedAccount(remotePerson);
        final Account localAccount1 = relatedAccount(localPerson1);
        final Account localAccount2 = relatedAccount(localPerson2);

        checkLocalAndRemoteBehavior(remoteAccount, localAccount1, localAccount2);
    }

    @Test
    public void test27_multipleRelatedAccounts() throws InterruptedException, RemoteException {
        multiThreadPersonQueries(1, 1, 1, 50);
    }

    @Test
    public void test28_multiThreadRequestsSinglePersonSingleAccount() throws InterruptedException, RemoteException {
        multiThreadPersonQueries(10, 10, 1, 1);
    }

    @Test
    public void test29_multiThreadRequestsSinglePersonMultipleAccounts() throws InterruptedException, RemoteException {
        multiThreadPersonQueries(10, 10, 1, 10);
    }

    @Test
    public void test30_multiThreadRequestsMultiplePersonsSingleAccount() throws InterruptedException, RemoteException {
        multiThreadPersonQueries(10, 10, 10, 1);
    }

    @Test
    public void test31_multiThreadRequestsMultiplePersonsMultipleAccounts() throws InterruptedException, RemoteException {
        multiThreadPersonQueries(5, 10, 5, 5);
    }
}