package project;

import com.bank.controller.AuthController;
import com.bank.controller.UserController;
import com.bank.enums.Role;
import com.bank.models.User;
import com.bank.repository.Impl.AcountRepositoryImpl;
import com.bank.repository.Impl.AuthRepositoryImpl;
import com.bank.repository.Impl.UserRepositoryImpl;
import com.bank.repository.interfaces.AccountRepository;
import com.bank.repository.interfaces.AuthRepository;
import com.bank.service.AccountService;
import com.bank.service.AuthService;
import com.bank.service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean loggedIn = false;
        Scanner scanner = new Scanner(System.in);
        // auth controller  :
        AuthController authController = new AuthController();
        // auth repo
        AuthRepository authRepository = new AuthRepositoryImpl();
        AuthService authService = new AuthService(authRepository);
        // account repo
        AccountRepository accountRepository = new AcountRepositoryImpl();
        AccountService accountService = new AccountService(accountRepository);
        // user repository
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);
         UserController userController = new UserController(userService);
        User loggedInUser = null;

        while (true) {
            if (loggedIn) {
                if (loggedInUser.getRole() == Role.TELLER) {
                    System.out.println("TELLER Menu");
                    System.out.println("1. connexion client");
                    System.out.println("2. create new client");
                    System.out.println("3. deposit");
                    System.out.println("4. withdraw");
                    System.out.println("5. virements internes, ");
                    System.out.println("6. demandes de crédits ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch(choice){
                        case 1 :
                            System.out.println("enter RIB");
                            String RIB = scanner.nextLine();
                            System.out.println("enter password");
                            String password = scanner.nextLine();

                            accountService.connexion(RIB, password);

                            break;
                        case 2 :
                            // create new client
                            break;
                        case 3 :
                            // deposit
                            break;
                        case 4 :
                            // withdraw;
                            break;
                        case 5 :
                            // virements internes
                            break;
                        case 6 :
                            // demandes de crédits
                            break;
                        default :
                            System.out.println("Invalid choice, try again.");
                    }
                }
                else if (loggedInUser.getRole() == Role.ADMIN){
                    System.out.println("=== Admin Menu ===");
                    System.out.println("1. manage users");
                    System.out.println("2. Logout");
                    System.out.println("3. get all users");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            // View profile
                            System.out.println("add new user :");
                            System.out.println("#############################");

                            System.out.println("enter the name: " );
                            String name = scanner.nextLine();
                            System.out.println("enter the email: " );
                            String email = scanner.nextLine();
                            System.out.println("enter the address: " );
                            String address = scanner.nextLine();
                            System.out.println("enter the password: " );
                            String password = scanner.nextLine();
                            System.out.println("enter the role (ADMIN, TELLER, CUSTOMER, AUDITOR): " );
                            String roleInput = scanner.nextLine();
                            System.out.println("enter the phone number : ");
                            String phoneNumber = scanner.nextLine();

                            userController.CreateUser(name , email, phoneNumber, address, password, roleInput);

                            System.out.println("Role: " + loggedInUser.getRole());
                            break;
                        case 2:
                            // Logout
                            loggedIn = false;
                            loggedInUser = null;
                            System.out.println("Logged out successfully.");
                            break;
                        case 3:
//                            System.out.println("Logged out successfully.");
                                userController.GetAllUsers();
                            break;
                        default:
                            System.out.println("Invalid choice, try again.");
                    }
                }
            } else {
                System.out.println("###########################################################");

                System.out.println("\n1. Connexion");
                System.out.println("2. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.println("Enter password: ");
                        String password = scanner.nextLine();
                        User user =  authController.login(email, password);
                      if (user != null){
                            loggedIn = true;
                            loggedInUser = user;
                        } else {
                            System.out.println("Invalid email or password.");
                      }

                        break;
                    case 2:
                        // LoginString
                        loggedInUser = null;
                        break;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            }
        }
    }
}

