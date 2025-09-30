package com.bank.controller;

import com.bank.models.Client;
import com.bank.service.ClientService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClientController {
ClientService clientService;
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }
    public void CreateClient(String firstName, String lastName, String email, String phone, String address, LocalDate dateOfBirth){
       Client newClient =   clientService.createClient(firstName, lastName, email, phone, address, dateOfBirth);
         if (newClient != null){
             System.out.println("Client created successfully: " + newClient.getFirstName() + " " + newClient.getLastName());
    }else{
                System.out.println("Error: Could not create client.");
         }
         }

         public void getAllClients(){
             List<Client> clients = clientService.getAllClients();
             if(clients.isEmpty()){
                    System.out.println("No clients found.");
             }else{
                 System.out.println("Clients List:");
                    clients.stream().forEach(s -> System.out.println("name : " + s.getFirstName() + " " + s.getLastName() + ", email: " + s.getEmail()));
             }
         }


    public Optional<Client> getClientByEmail(String email) {
        if (email == null || email.isEmpty()) {
            System.out.println("please provide a valid email");
            return Optional.empty();
        }
        Optional<Client> client  = clientService.getClientByEmail(email);
        if(client.isPresent()){
            Client c = client.get();
            System.out.println("Client found: " + c.getFirstName() + " " + c.getLastName() + ", email: " + c.getEmail());
        }else{
            System.out.println("Client with email " + email + " not found.");
        }
        return client;
    }

}
