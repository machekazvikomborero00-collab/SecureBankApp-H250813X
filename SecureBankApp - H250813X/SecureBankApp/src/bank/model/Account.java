package bank.model;

import bank.exception.InsufficientFundsException;

/**
 * A bank account. Balance is private and can only change through
 * deposit()/withdraw(), which validate before mutating state — there
 * is deliberately no setBalance().
 */
public class Account {

    private final String accountNumber;
    private final String ownerUsername;
    private double balance;

    public Account(String accountNumber, String ownerUsername, double balance) {
        this.accountNumber = accountNumber;
        this.ownerUsername = ownerUsername;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(
                    "Insufficient funds: balance is " + balance + ", requested " + amount);
        }
        // Balance only changes after every check above has passed, so a
        // failed withdrawal never leaves the account in a half-updated state.
        balance -= amount;
    }
}
