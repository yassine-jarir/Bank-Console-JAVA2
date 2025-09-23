package com.bank.service;

public class AuthService {
    private String email;
    private String password;
// List<String> roles = new ArrayList<>();
//    AuthRepository authRepository = new AuthRepository() ;
    public AuthService (String email , String password){
        this.email = email;
        this.password = password;
    }
//    public User login(String email, String password) {
//        User user = userRepository.findByEmail(email);
//        if (user != null && user.getPassword().equals(password)) {
//            System.out.println("Login successful!");
//            return user;
//        } else {
//            System.out.println("Invalid email or password.");
//            return null;
//        }
//    }
}
