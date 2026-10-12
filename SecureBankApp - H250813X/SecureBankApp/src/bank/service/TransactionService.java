package bank.service;

import bank.exception.InsufficientFundsException;
import bank.model.Account;
import bank.model.Transaction;
import bank.persistence.AccountRepository;
import bank.persistence.TransactionRepository;

import java.util.List;

/** Applies deposits/withdrawals to an account and logs each one as a transaction. */
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Transaction deposit(Account account, double amount) {
        account.deposit(amount);
        accountRepository.update(account);
        return transactionRepository.save(account.getAccountNumber(), "DEPOSIT", amount);
    }

    public Transaction withdraw(Account account, double amount) throws InsufficientFundsException {
        // account.withdraw() validates and throws BEFORE mutating balance,
        // so on failure there is nothing here to roll back or persist.
        account.withdraw(amount);
        accountRepository.update(account);
        return transactionRepository.save(account.getAccountNumber(), "WITHDRAW", amount);
    }

    public List<Transaction> getHistory(Account account) {
        return transactionRepository.findByAccount(account.getAccountNumber());
    }
}
