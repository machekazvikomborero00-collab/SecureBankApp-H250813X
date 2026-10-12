# Secure Banking Application

## Description
A console-based Java banking application demonstrating Object-Oriented
Programming and secure coding practices, including salted password
hashing, ownership-checked authorization, input validation, and
file-based data persistence.

## Student Details
- Name: Zvikomborero Macheka
- Registration Number: H250813X

## Features
- User registration and login with salted, SHA-256 hashed passwords (no plaintext ever stored)
- Create bank accounts and view balances
- Deposit and withdraw funds, with validation against negative amounts and insufficient funds
- Per-account transaction history
- Ownership checks — a user can only view or act on their own accounts
- All data persisted to text files and reloaded automatically on startup

## Project Structure
```
SecureBankApp-[YourRegNumber]/
├── src/
│   └── bank/
│       ├── Main.java
│       ├── model/
│       │   ├── User.java
│       │   ├── Account.java
│       │   └── Transaction.java
│       ├── service/
│       │   ├── AuthService.java
│       │   ├── AccountService.java
│       │   └── TransactionService.java
│       ├── persistence/
│       │   ├── FileManager.java
│       │   ├── UserRepository.java
│       │   ├── AccountRepository.java
│       │   └── TransactionRepository.java
│       ├── security/
│       │   └── PasswordUtils.java
│       └── exception/
│           ├── InsufficientFundsException.java
│           ├── AuthenticationException.java
│           ├── DuplicateUserException.java
│           └── UnauthorizedAccessException.java
├── data/
│   ├── users.txt
│   ├── accounts.txt
│   └── transactions.txt
├── .gitignore
└── README.md
```

## How to Compile and Run
From the project root:
```
javac -d bin $(find src -name "*.java")
java -cp bin bank.Main
```

## Security Measures
- Passwords are hashed with a per-user random salt (SHA-256) — never stored or compared as plaintext
- Password/hash comparison uses a constant-time check to avoid timing leaks
- All monetary inputs are validated before processing (positive amounts, sufficient balance)
- Every account action checks that the logged-in user actually owns the account
- Exceptions are caught and shown as clean messages — no leaked stack traces or file paths
- Login failures return the same generic message regardless of whether the username or password was wrong

## Known Limitations
- Single-currency, single-branch model
- No concurrent-user file locking (out of scope for this project)
- No password complexity rules beyond a minimum length
