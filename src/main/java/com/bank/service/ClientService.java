package com.bank.service;

import com.bank.models.Client;
import com.bank.repository.Impl.ClientRepositoryImpl;
import com.bank.repository.Impl.UserRepositoryImpl;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientRepositoryImpl clientRepository;

    public ClientService( ClientRepositoryImpl clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(String firstName, String lastName, String email, String phone, String address, LocalDate dateOfBirth) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPhone(phone);
        client.setAddress(address);
        client.setDateOfBirth(dateOfBirth);
        try {
            clientRepository.createClient(client);
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
    public List<Client> getAllClients(){
        return clientRepository.getAllClients();
    }
    public Optional<Client> getClientByEmail(String email){

        Optional<Client>  client = clientRepository.getClientByEmail(email);
         return client;

    }
}


