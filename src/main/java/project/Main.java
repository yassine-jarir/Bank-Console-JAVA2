package project;

import com.bank.controller.*;
import com.bank.models.Account;
import com.bank.models.Client;
import com.bank.models.Credit;
import com.bank.models.Transaction;
import com.bank.models.User;
import com.bank.repository.Impl.*;
import com.bank.repository.interfaces.AccountRepository;
import com.bank.repository.interfaces.AuthRepository;
import com.bank.service.*;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;
        User loggedInUser = null;

        // Auth Controller setup
        AuthController authController = new AuthController();
        AuthRepository authRepository = new AuthRepositoryImpl();
        AuthService authService = new AuthService(authRepository);
        // Account Service setup
        AccountRepositoryImpl accountRepositoryImpl = new AccountRepositoryImpl();
        AccountService accountService = new AccountService(accountRepositoryImpl);
        AccountController accountController = new AccountController(accountService);
        // User Controller setup
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);
        // Client Controller setup
        ClientRepositoryImpl clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientService(clientRepository , accountRepositoryImpl);
        ClientController clientController = new ClientController(clientService);
        // Transaction
        TransactionService transactionService = new TransactionService(new TransactionRepositoryImpl());
        TransactionController transactionController = new TransactionController(transactionService );
        // Credit Controller setup
        CreditRepositoryImpl creditRepository = new CreditRepositoryImpl();
        CreditService creditService = new CreditService(creditRepository);
        CreditController creditController = new CreditController(creditService);
        // Credit Payment Controller setup
        CreditPaymentRepositoryImpl creditPaymentRepository = new CreditPaymentRepositoryImpl();
        CreditPaymentService creditPaymentService = new CreditPaymentService(creditPaymentRepository);
        CreditPaymentController creditPaymentController = new CreditPaymentController(creditPaymentService);
        while (true) {
            if (!loggedIn) {
                System.out.println("\n=== Welcome to MOUSSAKA BANK ===");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();

                        User user = authController.login(email, password);
                        if (user != null) {
                            loggedIn = true;
                            loggedInUser = user;
                            System.out.println("Logged in as: " + loggedInUser.getRole());
                        } else {
                            System.out.println("Invalid email or password.");
                        }
                    }
                    case 2 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice, try again.");
                }
            } else {
                switch (loggedInUser.getRole()) {
                    case TELLER -> {
                        showTellerMenu(scanner, authService, loggedIn, loggedInUser, clientController, accountController, creditController, creditPaymentController);
                        // When menu returns, user has logged out
                        loggedIn = false;
                        loggedInUser = null;
                        System.out.println("Successfully logged out!");
                    }
                    case ADMIN -> {
                        showAdminMenu(scanner, accountService, userController, transactionController, creditController, loggedInUser);
                        loggedIn = false;
                        loggedInUser = null;
                        System.out.println("Successfully logged out!");
                    }
                    case CUSTOMER -> {
                        showCustomerMenu(scanner, accountService);
                        loggedIn = false;
                        loggedInUser = null;
                        System.out.println("Successfully logged out!");
                    }
                    case AUDITOR -> {
                        showAuditorMenu(scanner, accountService);
                        loggedIn = false;
                        loggedInUser = null;
                        System.out.println("Successfully logged out!");
                    }
                    case MANAGER -> {
                        showManagerMenu(scanner, transactionController, creditController, loggedInUser);
                        loggedIn = false;
                        loggedInUser = null;
                        System.out.println("Successfully logged out!");
                    }
                    default -> System.out.println("Unknown role.");
                }
            }
        }
    }

// ==================== TELLER MENU ====================
    private static void showTellerMenu(Scanner scanner, AuthService authService, boolean loggedIn, User loggedInUser, ClientController clientController, AccountController accountController, CreditController creditController ,CreditPaymentController creditPaymentController ) {
        while (true) {
            System.out.println("\n=== TELLER Menu ===");
            System.out.println("1. Create new client");
            System.out.println("2. Manage clients");
            System.out.println("3. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.println("=== Create New Client ===");
                    System.out.print("First name: ");
                    String firstName = scanner.nextLine();
                    System.out.print("Last name: ");
                    String lastName = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Phone: ");
                    String phone = scanner.nextLine();
                    System.out.print("Address: ");
                    String address = scanner.nextLine();
                    System.out.print("Date of birth (yyyy-MM-dd): ");
                    LocalDate dateOfBirth = LocalDate.parse(scanner.nextLine());

                    Boolean createSavingAccount = false;
                    System.out.println("create saving account ? ");
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("yes") ) {
                        createSavingAccount = true;
                    }

               Client client = clientController.CreateClient(firstName, lastName, email, phone, address, dateOfBirth, createSavingAccount);
                System.out.println("client id " + client.getId());

                }
                case 2 -> manageClients(scanner, clientController, accountController, creditController, creditPaymentController);
                case 3 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ==================== MANAGE CLIENTS ====================
    private static void manageClients(Scanner scanner, ClientController clientController, AccountController accountController, CreditController creditController, CreditPaymentController creditPaymentController  ) {
        while (true) {
            System.out.println("\n--- Manage Clients ---");
            System.out.println("1. List all clients");
            System.out.println("2. Select client for operations");
            System.out.println("3. Back to Teller Menu");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    clientController.getAllClients();
                }
                case 2 -> {
                    System.out.print("Enter client email: ");
                    String clientEmail = scanner.nextLine();
                 Optional<Client> client = clientController.getClientByEmail(clientEmail);
                 if (client.isPresent()){
                     Client c = client.get();
                     System.out.println("Selected client #" + c.getFirstName());
                     showClientOperations(scanner, c, accountController, creditController, creditPaymentController);
                 }
                }
                case 3 -> {
                    System.out.println("Returning to Teller Menu...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ==================== CLIENT OPERATIONS ====================
    private static void showClientOperations(Scanner scanner, Client client, AccountController accountController, CreditController creditController, CreditPaymentController creditPaymentController) {
        while (true) {
            System.out.println("\n--- Client Operations for Client #" + client.getFirstName() + " ---" );
            System.out.println("1. Deposit  ");
            System.out.println("2. Withdraw  ");
            System.out.println("3. Internal transfer  ");
            System.out.println("4. External transfer ");
            System.out.println("5. Request credit");
            System.out.println("6. Pay credit installment");
            System.out.println("7. View credit payment history");
            System.out.println("8. Create new account  ");
            System.out.println("9. Back");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 ->  {
                System.out.println("select the account to deposit to : ");
                List<Account> accounts = client.getAccounts();
                accounts.stream().forEach(a -> System.out.println("RIB : " + a.getAccountRib() + " | Balance : " + a.getBalance() + " | Type : " + a.getAccountType() + " | Name : " + client.getFirstName()));
                System.out.println("Enter deposit rib: ");
                String ribToDeposit = scanner.nextLine();
                System.out.println("Enter deposit amount: ");
                BigDecimal amount = scanner.nextBigDecimal();
                accountController.deposit(ribToDeposit ,amount );
                }
                case 2 -> {
                System.out.println("select the account to withdraw from : ");
                List<Account> accounts = client.getAccounts();
                accounts.stream().forEach(a -> System.out.println("RIB : " + a.getAccountRib() + " | Balance : " + a.getBalance() + " | Type : " + a.getAccountType() + " | Name : " + client.getFirstName()));
                System.out.println("Enter withdraw rib: ");
                String ribToWithdraw = scanner.nextLine();
                System.out.println("Enter withdraw amount: ");
                BigDecimal amount = scanner.nextBigDecimal();
                accountController.withdraw(ribToWithdraw, amount);
                }
                case 3 -> { // Internal transfer
                    List<Account> accounts = client.getAccounts();
                    accounts.forEach(a -> System.out.println("RIB : " + a.getAccountRib() + " | Balance : " + a.getBalance()));

                    System.out.println("Enter source RIB: ");
                    String sourceRib = scanner.nextLine();
                    System.out.println("Enter destination RIB (same client): ");
                    String destRib = scanner.nextLine();
                    System.out.println("Enter amount: ");
                    BigDecimal amount = scanner.nextBigDecimal();

                    accountController.internalTransfer(client, sourceRib, destRib, amount);
                }
                case 4 -> {
                    List <Account> accounts = client.getAccounts();
                    accounts.forEach(a -> System.out.println("RIB : " + a.getAccountRib() + " | Balance : " + a.getBalance()));
                    System.out.println("Enter source RIB: ");
                    String sourceRib = scanner.nextLine();
                    System.out.println("Enter destination RIB (external): ");
                    String destRib = scanner.nextLine();
                    System.out.println("Enter amount: ");
                    BigDecimal amount = scanner.nextBigDecimal();
                    accountController.externalTransfer(client, sourceRib, destRib, amount);
                }
                case 5 -> {
                    // Credit request - simple and easy to understand
                    System.out.println("=== Request a Credit ===");

                    // Show client's accounts so they can choose which one
                    System.out.println("Your accounts:");
                    List<Account> accounts = client.getAccounts();
                    for (int i = 0; i < accounts.size(); i++) {
                        Account account = accounts.get(i);
                        System.out.println((i + 1) + ". RIB: " + account.getAccountRib() +
                                         " | Balance: " + account.getBalance() +
                                         " | Type: " + account.getAccountType());
                    }

                    System.out.print("Select account number for the credit: ");
                    int accountChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (accountChoice > 0 && accountChoice <= accounts.size()) {
                        Account selectedAccount = accounts.get(accountChoice - 1);

                        // Ask for credit details - simple questions
                        System.out.print("Enter loan amount: ");
                        BigDecimal loanAmount = scanner.nextBigDecimal();
                        scanner.nextLine();

                        System.out.print("Enter loan term (months): ");
                        int termMonths = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Enter interest rate (%): ");
                        BigDecimal interestRate = scanner.nextBigDecimal();
                        scanner.nextLine();

                        // Calculate end date (simple math)
                        LocalDate startDate = LocalDate.now();
                        LocalDate endDate = startDate.plusMonths(termMonths);

                        // Create a simple credit object
                        Credit newCredit = new Credit();
                        newCredit.setAccountId(selectedAccount.getId());
                        newCredit.setLoanAmount(loanAmount);
                        newCredit.setInterestRate(interestRate);
                        newCredit.setLoanTermMonths(termMonths);
                        newCredit.setStartDate(startDate);
                        newCredit.setEndDate(endDate);

                        // Save the credit request
                        Credit savedCredit = creditController.createCreditRequest(newCredit);

                        if (savedCredit != null) {
                            System.out.println("---- Credit request submitted successfully! ----");
                            System.out.println("Credit ID: " + savedCredit.getCreditId());
                            System.out.println("Amount: " + loanAmount);
                            System.out.println("Term: " + termMonths + " months");
                            System.out.println("Status: PENDING (waiting for manager approval)");
                        } else {
                            System.out.println("!!! Failed to submit credit request. Please try again.");
                        }
                    } else {
                        System.out.println("Invalid account selection.");
                    }
                }
                case 6 -> {
                    // Pay credit installment - simple and easy
                    System.out.println("=== Pay Credit Installment ===");
                    System.out.print("Enter Credit ID to pay: ");
                    Long creditId = scanner.nextLong();
                    scanner.nextLine();

                    try {
                        creditPaymentController.payNextInstallment(creditId);
                    } catch (Exception e) {
                        System.out.println("!!! Payment failed: " + e.getMessage());
                    }
                }
                case 7 -> {
                    // View payment history - simple and easy
                    System.out.println("=== Credit Payment History ===");
                    System.out.print("Enter Credit ID to view history: ");
                    Long creditId = scanner.nextLong();
                    scanner.nextLine();

                    try {
                        creditPaymentController.showPaymentHistory(creditId);
                    } catch (Exception e) {
                        System.out.println("!!! Error viewing history: " + e.getMessage());
                    }
                }
                case 8 -> {
                    System.out.println("create savings account ? (yes/no) ");
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("yes") ) {
                        accountController.createNewAccount(client);
                    }
                    return;
                }
                case 9 -> {
                    System.out.println("Returning to Manage Clients...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ==================== ADMIN MENU ====================
    private static void showAdminMenu(Scanner scanner, AccountService accountService, UserController userController, TransactionController transactionController, CreditController creditController, User loggedInUser) {
        while (true) {
            System.out.println("\n=== ADMIN Menu ===");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Accounts");
            System.out.println("3. Manage Transactions");
            System.out.println("4. Manage Credits");
            System.out.println("5. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> manageUsers(scanner, userController);
                case 2 -> manageAccounts(scanner, accountService);
                case 3 -> manageTransactions(scanner, transactionController);
                case 4 -> manageCredits(scanner, creditController, loggedInUser);
                case 5 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ==================== MANAGE USERS ====================
    private static void manageUsers(Scanner scanner, UserController userController) {
        System.out.println("\n--- Manage Users ---");
        System.out.println("1. Add User");
        System.out.println("2. Delete User");
        System.out.println("3. List All Users");
        System.out.println("4. back");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                System.out.print("Enter email: ");
                String email = scanner.nextLine();
                System.out.print("Enter phone: ");
                String phone = scanner.nextLine();
                System.out.print("Enter address: ");
                String address = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                System.out.print("Enter role (ADMIN, TELLER, CUSTOMER, AUDITOR , MANAGER): ");
                String role = scanner.nextLine();

                userController.CreateUser(name, email, phone, address, password, role);
                System.out.println("User created successfully.");
            }
            case 2 -> {
                List<User> users = userController.GetAllUsers();
                IntStream.range(0, users.size()).forEach(i -> {
                    User user = users.get(i);
                    System.out.println((i + 1) + ". " + user.getName() + " - " + user.getRole());
                });
                System.out.print("Enter user index to delete: ");
                int index = scanner.nextInt();
                scanner.nextLine();
                userController.DeleteUser(users.get(index - 1).getCustomerId().intValue());
                System.out.println("User deleted successfully.");
            }
            case 3 -> {
                List<User> users = userController.GetAllUsers();
                users.forEach(u -> System.out.println(u.getName() + " - " + u.getRole()));
            }
            case 4 -> {
                System.out.println("Going back to Admin Menu...");
                return;
            }
            default -> {
                System.out.println("Invalid choice.");

            }
        }
    }

    // ==================== MANAGE ACCOUNTS ====================
    private static void manageAccounts(Scanner scanner, AccountService accountService) {
        System.out.println("\n--- Manage Accounts ---");
        System.out.println("1. View all accounts");
        System.out.println("2. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.println("=== All Bank Accounts ===");

                // Create account controller to get real data
                AccountRepositoryImpl accountRepo = new AccountRepositoryImpl();
                AccountService accService = new AccountService(accountRepo);
                AccountController accountController = new AccountController(accService);

                List<Account> allAccounts = accountController.getAllAccounts();

                if (allAccounts.isEmpty()) {
                    System.out.println("No accounts found in the system.");
                } else {
                    System.out.println("Found " + allAccounts.size() + " accounts:");
                    System.out.println("=".repeat(120));

                    for (Account account : allAccounts) {
                        System.out.println("Account ID: " + account.getId() +
                                         " | RIB: " + account.getAccountRib() +
                                         " | Type: " + account.getAccountType() +
                                         " | Status: " + account.getStatus());
                        System.out.println("   Client: " + account.getClientName() +
                                         " | Email: " + account.getClientEmail() +
                                         " | Balance: " + account.getBalance() + " " + account.getCurrency());
                        System.out.println("-".repeat(100));
                    }
                }
            }
            case 2 -> System.out.println("Going back to Admin Menu...");
            default -> System.out.println("Invalid choice.");
        }
    }

    // ==================== MANAGE TRANSACTIONS ====================
    private static void manageTransactions(Scanner scanner, TransactionController transactionController) {
        System.out.println("\n--- Manage Transactions ---");
        System.out.println("1. View all transactions");
        System.out.println("2. Approve/Deny external transfers");
        System.out.println("3. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.println("=== All Transactions ===");

                List<Transaction> allTransactions = transactionController.getAllTransactions();

                if (allTransactions.isEmpty()) {
                    System.out.println("No transactions found in the system.");
                } else {
                    System.out.println("Found " + allTransactions.size() + " transactions:");
                    System.out.println("=".repeat(120));

                    for (Transaction transaction : allTransactions) {
                        System.out.println("Transaction ID: " + transaction.getTransactionId() +
                                         " | Type: " + transaction.getTransactionType() +
                                         " | Amount: " + transaction.getAmount() +
                                         " | Status: " + transaction.getStatus());
                        System.out.println("   From: " + transaction.getSourceClientEmail() +
                                         " (RIB: " + transaction.getSourceAccountRib() + ")");
                        System.out.println("   To: " + transaction.getTargetClientEmail() +
                                         " (RIB: " + transaction.getTargetAccountRib() + ")");
                        System.out.println("   Date: " + transaction.getTransactionDate());
                        System.out.println("-".repeat(100));
                    }
                }
            }
            case 2 -> {
                System.out.println("--- Pending External Transfers ---");
                List<Transaction> pendingTransfers = transactionController.getPendingExternalTransfers();

                if (pendingTransfers.isEmpty()) {
                    System.out.println("No pending external transfers found.");
                } else {
                    System.out.println("\n📋 Pending External Transfers:");
                    System.out.println("=".repeat(60));

                    // Show all pending transfers
                    pendingTransfers.stream()
                        .forEach(transaction -> {
                            int index = pendingTransfers.indexOf(transaction) + 1;
                            System.out.println(index + ". Transaction ID: " + transaction.getTransactionId());
                            System.out.println("   Amount: " + transaction.getAmount());
                            System.out.println("   From: " + transaction.getSourceClientEmail());
                            System.out.println("   To: " + transaction.getTargetClientEmail());
                            System.out.println("   Date: " + transaction.getTransactionDate());
                            System.out.println("   Status: " + transaction.getStatus());
                            System.out.println("   ---");
                        });

                    // Simple selection
                    System.out.print("\nSelect transaction number (or 0 to go back): ");
                    int transactionChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (transactionChoice > 0 && transactionChoice <= pendingTransfers.size()) {
                        Transaction selected = pendingTransfers.get(transactionChoice - 1);

                        System.out.println("\n Selected Transaction:");
                        System.out.println("ID: " + selected.getTransactionId());
                        System.out.println("Amount: " + selected.getAmount());
                        System.out.println("From: " + selected.getSourceClientEmail());
                        System.out.println("To: " + selected.getTargetClientEmail());

                        System.out.println("\nWhat do you want to do?");
                        System.out.println("A - Approve");
                        System.out.println("D - Deny");
                        System.out.println("C - Cancel");
                        System.out.print("Your choice: ");

                        String decision = scanner.nextLine().toUpperCase();

                        switch (decision) {
                            case "A" -> {
                                try {
                                    transactionController.approveTransaction(selected.getTransactionId());
                                    System.out.println("--- Transaction APPROVED! ----");
                                    System.out.println("Money has been transferred successfully.");
                                } catch (Exception e) {
                                    System.out.println("!!! Error: " + e.getMessage());
                                }
                            }
                            case "D" -> {
                                System.out.print("Why are you denying this transaction? ");
                                String reason = scanner.nextLine();
                                System.out.println("!!! Transaction DENIED: " + reason);
                                // TODO: Add deny transaction method later
                            }
                            case "C" -> {
                                System.out.println("Operation cancelled.");
                            }
                            default -> {
                                System.out.println("Invalid choice. Please try again.");
                            }
                        }
                    } else if (transactionChoice != 0) {
                        System.out.println("Invalid number. Please try again.");
                    }
                }
            }
            case 3 -> System.out.println("Going back to Admin Menu...");
            default -> System.out.println("Invalid choice.");
        }
    }

    // ==================== MANAGE CREDITS ====================
    private static void manageCredits(Scanner scanner, CreditController creditController, User loggedInUser) {
        System.out.println("\n--- Manage Credits ---");
        System.out.println("1. View all credits");
        System.out.println("2. Approve/Deny pending credits");
        System.out.println("3. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.println("=== All Credits ===");
                System.out.println("Credit ID: 501 | Client: John Doe | Amount: 10000.00 | Status: ACTIVE");
                System.out.println("Credit ID: 502 | Client: Jane Smith | Amount: 5000.00 | Status: OVERDUE");
                System.out.println("Credit ID: 503 | Client: Bob Wilson | Amount: 15000.00 | Status: PAID");
                System.out.println("(Sample data - real system shows database records)");
            }
            case 2 -> {
                System.out.println("--- Pending Credit Requests ---");
                List<Credit> pendingCredits = creditController.getPendingCredits();

                if (pendingCredits.isEmpty()) {
                    System.out.println("No pending credit requests found.");
                } else {
                    System.out.println("\nPending Credit Requests:");
                    System.out.println("=".repeat(100));

                    // Using simple for loop as requested - easy to understand
                    for (int i = 0; i < pendingCredits.size(); i++) {
                        Credit credit = pendingCredits.get(i);
                        System.out.println((i + 1) + ". Credit ID: " + credit.getCreditId());
                        System.out.println("   Client: " + credit.getClientName() + " (" + credit.getClientEmail() + ")");
                        System.out.println("   Amount: " + credit.getLoanAmount() + " MAD");
                        System.out.println("   Term: " + credit.getLoanTermMonths() + " months");
                        System.out.println("   Status: " + credit.getRequestStatus());
                        System.out.println("   ---");
                    }

                    System.out.print("\nSelect credit number to approve/deny (or 0 to go back): ");
                    int creditChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (creditChoice > 0 && creditChoice <= pendingCredits.size()) {
                        Credit selectedCredit = pendingCredits.get(creditChoice - 1);
                        System.out.println("Selected Credit ID: " + selectedCredit.getCreditId());
                        System.out.println("Client: " + selectedCredit.getClientName());
                        System.out.println("Amount: " + selectedCredit.getLoanAmount() + " MAD");

                        System.out.println("Choose action:");
                        System.out.println("A - Approve");
                        System.out.println("D - Deny");
                        System.out.println("C - Cancel");

                        String decision = scanner.nextLine().toUpperCase();

                        switch (decision) {
                            case "A" -> {
                                try {
                                    creditController.approveCredit(selectedCredit.getCreditId(), loggedInUser.getCustomerId());
                                    System.out.println("--- Credit APPROVED! ---");
                                    System.out.println("Credit #" + selectedCredit.getCreditId() + " approved successfully.");
                                } catch (Exception e) {
                                    System.out.println("!!! Failed to approve credit: " + e.getMessage());
                                }
                            }
                            case "D" -> {
                                System.out.print("Enter reason for denial: ");
                                String reason = scanner.nextLine();
                                try {
                                    creditController.denyCredit(selectedCredit.getCreditId(), loggedInUser.getCustomerId());
                                    System.out.println("!!! Credit DENIED !!!");
                                    System.out.println("Reason: " + reason);
                                } catch (Exception e) {
                                    System.out.println("!!! Failed to deny credit: " + e.getMessage());
                                }
                            }
                            case "C" -> System.out.println("Action cancelled.");
                            default -> System.out.println("Invalid choice.");
                        }
                    } else if (creditChoice != 0) {
                        System.out.println("Invalid selection.");
                    }
                }
            }
            case 3 -> System.out.println("Going back to Admin Menu...");
            default -> System.out.println("Invalid choice.");
        }
    }

    // ==================== CUSTOMER MENU ====================
    private static void showCustomerMenu(Scanner scanner, AccountService accountService) {
        System.out.println("\n=== CUSTOMER Menu ===");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Request credit");
        System.out.println("4. Logout");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> System.out.println("Deposit (to implement)");
            case 2 -> System.out.println("Withdraw (to implement)");
            case 3 -> System.out.println("Credit request (to implement)");
            case 4 -> {
                System.out.println("Logging out...");
                return;
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // ==================== AUDITOR MENU ====================
    private static void showAuditorMenu(Scanner scanner, AccountService accountService) {
        System.out.println("\n=== AUDITOR Menu (read-only) ===");
        System.out.println("1. View accounts");
        System.out.println("2. View transactions");
        System.out.println("3. View credits");
        System.out.println("4. Logout");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1, 2, 3 -> System.out.println("Read-only feature (to implement)");
            case 4 -> {
                System.out.println("Logging out...");
                return;
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // ==================== MANAGER MENU ====================
    private static void showManagerMenu(Scanner scanner, TransactionController transactionController, CreditController creditController, User loggedInUser) {
        while (true) {
            System.out.println("\n=== MANAGER Menu ===");
            System.out.println("1. Approve/Deny pending credits");
            System.out.println("2. Approve/Deny external transfers");
            System.out.println("3. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.println("--- Pending Credit Requests ---");
                    List<Credit> pendingCredits = creditController.getPendingCredits();

                    if (pendingCredits.isEmpty()) {
                        System.out.println("No pending credit requests found.");
                    } else {
                        System.out.println("\nPending Credit Requests:");
                        System.out.println("=" .repeat(100));

                        for (int i = 0; i < pendingCredits.size(); i++) {
                            Credit credit = pendingCredits.get(i);
                            System.out.printf("%d. Credit ID: %d\n", (i + 1), credit.getCreditId());
                            System.out.printf("   Client: %s (%s)\n", credit.getClientName(), credit.getClientEmail());
                            System.out.printf("   Account RIB: %s\n", credit.getAccountRib());
                            System.out.printf("   Loan Amount: %s\n", credit.getLoanAmount());
                            System.out.printf("   Interest Rate: %s%%\n", credit.getInterestRate());
                            System.out.printf("   Term: %d months\n", credit.getLoanTermMonths());
                            System.out.printf("   Request Date: %s\n", credit.getRequestDate());
                            System.out.printf("   Status: %s\n", credit.getRequestStatus());
                            System.out.println("-" .repeat(80));
                        }

                        System.out.println("\nSelect credit number to approve/deny (or 0 to go back): ");
                        int creditChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (creditChoice > 0 && creditChoice <= pendingCredits.size()) {
                            Credit selectedCredit = pendingCredits.get(creditChoice - 1);
                            System.out.printf("Selected Credit ID: %d\n", selectedCredit.getCreditId());
                            System.out.printf("Client: %s, Amount: %s, Term: %d months\n",
                                selectedCredit.getClientName(),
                                selectedCredit.getLoanAmount(),
                                selectedCredit.getLoanTermMonths());

                            System.out.println("Choose action:");
                            System.out.println("A - Approve");
                            System.out.println("D - Deny");
                            System.out.println("C - Cancel");

                            String decision = scanner.nextLine().toUpperCase();

                            switch (decision) {
                                case "A" -> {
                                    try {
                                        creditController.approveCredit(selectedCredit.getCreditId(), loggedInUser.getCustomerId().longValue());
                                        System.out.println("Credit #" + selectedCredit.getCreditId() + " has been APPROVED.");
                                        System.out.println("The client has been notified and funds will be disbursed.");
                                    } catch (Exception e) {
                                        System.out.println("Failed to approve credit: " + e.getMessage());
                                    }
                                }
                                case "D" -> {
                                    System.out.println("Enter reason for denial: ");
                                    String reason = scanner.nextLine();
                                    try {
                                        creditController.denyCredit(selectedCredit.getCreditId(), loggedInUser.getCustomerId().longValue());
                                        System.out.println("Credit #" + selectedCredit.getCreditId() + " has been DENIED.");
                                        System.out.println("Reason: " + reason);
                                        System.out.println("The client has been notified of the decision.");
                                    } catch (Exception e) {
                                        System.out.println("Failed to deny credit: " + e.getMessage());
                                    }
                                }
                                case "C" -> {
                                    System.out.println("Action cancelled.");
                                }
                                default -> {
                                    System.out.println("Invalid choice. Action cancelled.");
                                }
                            }
                        } else if (creditChoice != 0) {
                            System.out.println("Invalid selection.");
                        }
                    }
                }
                case 2 -> {
                    System.out.println("--- Pending External Transfers ---");
                    List<Transaction> pendingTransfers = transactionController.getPendingExternalTransfers();

                    if (pendingTransfers.isEmpty()) {
                        System.out.println("No pending external transfers found.");
                    } else {
                        System.out.println("\n📋 Pending External Transfers:");
                        System.out.println("=".repeat(60));

                        // Show all pending transfers using streams
                        pendingTransfers.stream()
                            .forEach(transaction -> {
                                int index = pendingTransfers.indexOf(transaction) + 1;
                                System.out.println(index + ". Transaction ID: " + transaction.getTransactionId());
                                System.out.println("   Amount: " + transaction.getAmount());
                                System.out.println("   From: " + transaction.getSourceClientEmail());
                                System.out.println("   To: " + transaction.getTargetClientEmail());
                                System.out.println("   Date: " + transaction.getTransactionDate());
                                System.out.println("   Status: " + transaction.getStatus());
                                System.out.println("   ---");
                            });

                        // Simple selection
                        System.out.print("\nSelect transaction number (or 0 to go back): ");
                        int transactionChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (transactionChoice > 0 && transactionChoice <= pendingTransfers.size()) {
                            Transaction selected = pendingTransfers.get(transactionChoice - 1);

                            System.out.println("\n✅ Selected Transaction:");
                            System.out.println("ID: " + selected.getTransactionId());
                            System.out.println("Amount: " + selected.getAmount());
                            System.out.println("From: " + selected.getSourceClientEmail());
                            System.out.println("To: " + selected.getTargetClientEmail());

                            System.out.println("\nWhat do you want to do?");
                            System.out.println("A - Approve");
                            System.out.println("D - Deny");
                            System.out.println("C - Cancel");
                            System.out.print("Your choice: ");

                            String decision = scanner.nextLine().toUpperCase();

                            switch (decision) {
                                case "A" -> {
                                    try {
                                        transactionController.approveTransaction(selected.getTransactionId());
                                        System.out.println("✅ Transaction APPROVED!");
                                        System.out.println("Money has been transferred successfully.");
                                    } catch (Exception e) {
                                        System.out.println("❌ Error: " + e.getMessage());
                                    }
                                }
                                case "D" -> {
                                    System.out.print("Why are you denying this transaction? ");
                                    String reason = scanner.nextLine();
                                    System.out.println("❌ Transaction DENIED: " + reason);
                                    // TODO: Add deny transaction method later
                                }
                                case "C" -> {
                                    System.out.println("Operation cancelled.");
                                }
                                default -> {
                                    System.out.println("Invalid choice. Please try again.");
                                }
                            }
                        } else if (transactionChoice != 0) {
                            System.out.println("Invalid number. Please try again.");
                        }
                    }
                }
                case 3 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

}
