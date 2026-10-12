package bank;

import bank.exception.AuthenticationException;
import bank.exception.DuplicateUserException;
import bank.exception.InsufficientFundsException;
import bank.exception.UnauthorizedAccessException;
import bank.model.Account;
import bank.model.User;
import bank.persistence.AccountRepository;
import bank.persistence.TransactionRepository;
import bank.persistence.UserRepository;
import bank.service.AccountService;
import bank.service.AuthService;
import bank.service.TransactionService;

import java.util.List;
import java.util.Scanner;

/**
 * Console entry point. Main is deliberately a thin controller: it reads
 * input, prints output, and hands every real decision to a service class.
 * No file I/O and no business rules live here.
 */
public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private final AuthService authService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    private User currentUser;

    public Main() {
        UserRepository userRepository = new UserRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        this.authService = new AuthService(userRepository);
        this.accountService = new AccountService(accountRepository);
        this.transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("==================================");
        System.out.println(" Secure Banking Application");
        System.out.println("==================================");

        boolean running = true;
        while (running) {
            running = (currentUser == null) ? showLoggedOutMenu() : showLoggedInMenu();
        }

        System.out.println("Goodbye.");
    }

    private boolean showLoggedOutMenu() {
        System.out.println("\n1) Register\n2) Login\n3) Exit");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleRegister();
                return true;
            case "2":
                handleLogin();
                return true;
            case "3":
                return false;
            default:
                System.out.println("Invalid option.");
                return true;
        }
    }

    private boolean showLoggedInMenu() {
        System.out.println("\nLogged in as " + currentUser.getUsername());
        System.out.println("1) Create Account\n2) View My Accounts\n3) Deposit\n4) Withdraw"
                + "\n5) View Transaction History\n6) Logout");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": handleCreateAccount(); break;
            case "2": handleViewAccounts(); break;
            case "3": handleDeposit(); break;
            case "4": handleWithdraw(); break;
            case "5": handleViewHistory(); break;
            case "6":
                currentUser = null;
                System.out.println("Logged out.");
                break;
            default:
                System.out.println("Invalid option.");
        }
        return true;
    }

    private void handleRegister() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose a password (min 6 characters): ");
        String password = scanner.nextLine();

        try {
            authService.register(username, password);
            System.out.println("Registration successful. You can now log in.");
        } catch (DuplicateUserException | IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = authService.login(username, password);
            System.out.println("Login successful. Welcome, " + currentUser.getUsername() + ".");
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void handleCreateAccount() {
        Account account = accountService.createAccount(currentUser.getUsername());
        System.out.printf("Account created: %s (balance %.2f)%n", account.getAccountNumber(), account.getBalance());
    }

    private void handleViewAccounts() {
        List<Account> accounts = accountService.getAccountsForUser(currentUser.getUsername());
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts yet. Create one first.");
            return;
        }
        for (Account a : accounts) {
            System.out.printf("  %s   balance: %.2f%n", a.getAccountNumber(), a.getBalance());
        }
    }

    private void handleDeposit() {
        Account account = selectOwnedAccount();
        if (account == null) {
            return;
        }
        Double amount = promptPositiveAmount("Amount to deposit: ");
        if (amount == null) {
            return;
        }
        transactionService.deposit(account, amount);
        System.out.printf("Deposited %.2f. New balance: %.2f%n", amount, account.getBalance());
    }

    private void handleWithdraw() {
        Account account = selectOwnedAccount();
        if (account == null) {
            return;
        }
        Double amount = promptPositiveAmount("Amount to withdraw: ");
        if (amount == null) {
            return;
        }
        try {
            transactionService.withdraw(account, amount);
            System.out.printf("Withdrew %.2f. New balance: %.2f%n", amount, account.getBalance());
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private void handleViewHistory() {
        Account account = selectOwnedAccount();
        if (account == null) {
            return;
        }
        var history = transactionService.getHistory(account);
        if (history.isEmpty()) {
            System.out.println("No transactions yet for this account.");
            return;
        }
        history.forEach(System.out::println);
    }

    /** Prompts for an account number and confirms the current user owns it. */
    private Account selectOwnedAccount() {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        try {
            return accountService.getOwnedAccount(accountNumber, currentUser.getUsername());
        } catch (UnauthorizedAccessException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /** Reads a monetary amount from the console, returning null (and printing why) on any bad input. */
    private Double promptPositiveAmount(String label) {
        System.out.print(label);
        String raw = scanner.nextLine().trim();
        try {
            double amount = Double.parseDouble(raw);
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }
}
