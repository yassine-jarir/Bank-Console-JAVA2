package com.bank.repository.interfaces;

import com.bank.models.Client;

import java.util.List;
import java.util.Optional;

public interface ClientInterface {
    Client createClient(Client client);
    List<Client> getAllClients();
    Optional<Client> getClientByEmail(String email);
}
