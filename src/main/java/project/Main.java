package project;

import com.bank.controller.AuthController;
import com.bank.controller.ClientController;
import com.bank.controller.UserController;
import com.bank.models.Client;
import com.bank.models.User;
import com.bank.repository.Impl.AcountRepositoryImpl;
import com.bank.repository.Impl.AuthRepositoryImpl;
import com.bank.repository.Impl.ClientRepositoryImpl;
import com.bank.repository.Impl.UserRepositoryImpl;
import com.bank.repository.interfaces.AccountRepository;
import com.bank.repository.interfaces.AuthRepository;
import com.bank.service.AccountService;
import com.bank.service.AuthService;
import com.bank.service.ClientService;
import com.bank.service.UserService;

import javax.swing.text.html.Option;
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
        AccountRepository accountRepository = new AcountRepositoryImpl();
        AccountService accountService = new AccountService(accountRepository);
// User Controller setup
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);
// Client Controller setup
        ClientRepositoryImpl clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientService(clientRepository);
        ClientController clientController = new ClientController(clientService);

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
                    case TELLER -> showTellerMenu(scanner, authService, loggedIn, loggedInUser, clientController);
                    case ADMIN -> showAdminMenu(scanner, accountService, userController);
                    case CUSTOMER -> showCustomerMenu(scanner, accountService);
                    case AUDITOR -> showAuditorMenu(scanner, accountService);
                    case MANAGER -> showManagerMenu(scanner, accountService);
                    default -> System.out.println("Unknown role.");
                }
            }
        }
    }

// ==================== TELLER MENU ====================
    private static void showTellerMenu(Scanner scanner, AuthService authService,
                                       boolean loggedIn, User loggedInUser, ClientController clientController) {
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

                    clientController.CreateClient(firstName, lastName, email, phone, address, dateOfBirth);
                }
                case 2 -> manageClients(scanner, clientController);
                case 3 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ==================== MANAGE CLIENTS ====================
    private static void manageClients(Scanner scanner, ClientController clientController) {
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
                     showClientOperations(scanner, c);
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
    private static void showClientOperations(Scanner scanner, Client client) {
        while (true) {
            System.out.println("\n--- Client Operations for Client #" + client.getFirstName() + " ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Internal transfer");
            System.out.println("4. Request credit");
            System.out.println("5. Back");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> System.out.println("Deposit (to implement)");
                case 2 -> System.out.println("Withdraw (to implement)");
                case 3 -> System.out.println("Internal transfer (to implement)");
                case 4 -> System.out.println("Credit request (to implement)");
                case 5 -> {
                    System.out.println("Returning to Manage Clients...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ==================== ADMIN MENU ====================
    private static void showAdminMenu(Scanner scanner, AccountService accountService, UserController userController) {
        System.out.println("\n=== ADMIN Menu ===");
        System.out.println("1. Manage Users");
        System.out.println("2. Manage Accounts");
        System.out.println("3. Manage Transactions");
        System.out.println("4. Manage Credits");
        System.out.println("5. Reports & Statistics");
        System.out.println("6. Fees & Commissions");
        System.out.println("7. Logout");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> manageUsers(scanner, userController);
            case 2 -> System.out.println("Manage accounts (to implement)");
            case 3 -> System.out.println("Manage transactions (to implement)");
            case 4 -> System.out.println("Manage credits (to implement)");
            case 5 -> System.out.println("Reports & statistics (to implement)");
            case 6 -> System.out.println("Fees & commissions (to implement)");
            case 7 -> {
                System.out.println("Logging out...");


            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // ==================== USER MANAGEMENT ====================
    private static void manageUsers(Scanner scanner, UserController userController) {
        System.out.println("\n--- Manage Users ---");
        System.out.println("1. Add User");
        System.out.println("2. Delete User");
        System.out.println("3. List All Users");
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
                System.out.print("Enter role (ADMIN, TELLER, CUSTOMER, AUDITOR): ");
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
                userController.DeleteUser(users.get(index - 1).getCustomerId());
                System.out.println("User deleted successfully.");
            }
            case 3 -> {
                List<User> users = userController.GetAllUsers();
                users.forEach(u -> System.out.println(u.getName() + " - " + u.getRole()));
            }
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
            case 4 -> System.out.println("Logging out...");
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
            case 4 -> System.out.println("Logging out...");
            default -> System.out.println("Invalid choice.");
        }
    }
    // ==================== MANAGER MENU ====================
    private static void showManagerMenu(Scanner scanner, AccountService accountService) {
        System.out.println("\n=== MANAGER Menu ===");
        System.out.println("1. Approve/Deny credits");
        System.out.println("2. View reports");
        System.out.println("3. Logout");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1 -> System.out.println("Approve/Deny credits (to implement)");
            case 2 -> System.out.println("View reports (to implement)");
            case 3 -> System.out.println("Logging out...");
            default -> System.out.println("Invalid choice.");
        }
    }


}



