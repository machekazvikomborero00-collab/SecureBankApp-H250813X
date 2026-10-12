package bank.service;

import bank.exception.UnauthorizedAccessException;
import bank.model.Account;
import bank.persistence.AccountRepository;

import java.util.List;

/** Account creation and lookup, with an ownership check on every access. */
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String username) {
        String accountNumber = accountRepository.nextAccountNumber();
        Account account = new Account(accountNumber, username, 0.0);
        accountRepository.save(account);
        return account;
    }

    public List<Account> getAccountsForUser(String username) {
        return accountRepository.findByOwner(username);
    }

    /**
     * Looks up an account and confirms {@code username} owns it before
     * returning it — every balance read or transaction goes through this
     * so one user can never act on another user's account.
     */
    public Account getOwnedAccount(String accountNumber, String username) throws UnauthorizedAccessException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("No such account: " + accountNumber));

        if (!account.getOwnerUsername().equalsIgnoreCase(username)) {
            throw new UnauthorizedAccessException("You do not have access to account " + accountNumber);
        }
        return account;
    }
}
