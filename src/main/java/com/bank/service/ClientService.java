package com.bank.service;

import com.bank.models.Client;
import com.bank.repository.Impl.AccountRepositoryImpl;
import com.bank.repository.Impl.ClientRepositoryImpl;
import com.bank.repository.interfaces.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientRepositoryImpl clientRepository;
    private final AccountRepositoryImpl accountRepositoryImpl;

    public ClientService( ClientRepositoryImpl clientRepository , AccountRepositoryImpl accountRepositoryImpl){
        this.clientRepository = clientRepository;
        this.accountRepositoryImpl = accountRepositoryImpl;
    }

    public Client createClient(String firstName, String lastName, String email, String phone, String address, LocalDate dateOfBirth , Boolean createSavingAccount) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPhone(phone);
        client.setAddress(address);
        client.setDateOfBirth(dateOfBirth);
        try {
            clientRepository.createClient(client);
            AccountService.createAccountForClient(client , createSavingAccount);
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
        Optional<Client> client = clientRepository.getClientByEmail(email);
        return client;
    }
    public void createNewAccount(Client client, Boolean createSavingAccount){
        AccountService.createNewAccount(client , createSavingAccount);
    }

}
