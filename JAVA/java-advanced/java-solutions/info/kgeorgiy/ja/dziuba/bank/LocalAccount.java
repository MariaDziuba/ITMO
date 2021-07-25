package info.kgeorgiy.ja.dziuba.bank;

/**
 * Local account of an individual, is sent via serialization
 */
// :NOTE: Бесполезен
public class LocalAccount extends AbstractAccount {
    public LocalAccount(String id, int amount) {
        super(id, amount);
    }
}
