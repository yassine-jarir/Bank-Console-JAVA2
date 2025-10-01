package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.enums.AccountType;
import com.bank.models.Account;
import com.bank.models.Client;
import com.bank.repository.interfaces.ClientInterface;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientRepositoryImpl implements ClientInterface {
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    Connection conn;

    public ClientRepositoryImpl() {
        this.conn = DatabaseConfig.getInstance().getConnection();
    }


    public Client createClient(Client client) {
        String sql = "INSERT INTO clients (first_name, last_name, email, phone, address, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getFirstName());
            stmt.setString(2, client.getLastName());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getAddress());
            stmt.setDate(6, Date.valueOf(client.getDateOfBirth()));

            stmt.executeUpdate();


            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating client failed, no ID obtained from DB.");
                }
            }

            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Client> getAllClients() {
        String sql = "SELECT c.*, a.* FROM clients c LEFT JOIN accounts a ON c.client_id = a.client_id ORDER BY c.client_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Long, Client> clientMap = new HashMap<>();

            while (rs.next()) {
                Long clientId = rs.getLong("client_id");
                Client client = clientMap.get(clientId);

                if (client == null) {
                    // Create new client only if not already exists
                    client = new Client();
                    client.setId(clientId);
                    client.setFirstName(rs.getString("first_name"));
                    client.setLastName(rs.getString("last_name"));
                    client.setEmail(rs.getString("email"));
                    client.setPhone(rs.getString("phone"));
                    client.setAddress(rs.getString("address"));
                    client.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                    client.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
                    client.setActive(rs.getBoolean("is_active"));
                    clientMap.put(clientId, client);
                }

                // Add account if exists (account_id will be null if client has no accounts)
                if (rs.getObject("account_id") != null) {
                    Account account = new Account();
                    account.setId(rs.getLong("account_id"));
                    account.setAccountType(AccountType.valueOf(rs.getString("account_type")));
                    account.setBalance(rs.getBigDecimal("balance"));
                    account.setCurrency(rs.getString("currency"));
                    account.setStatus(rs.getString("status"));
                    account.setAccountRib(rs.getString("account_rib"));
                    account.setCustomerId(clientId);
                    client.getAccounts().add(account);
                }
            }

            return new ArrayList<>(clientMap.values());
        } catch (Exception e) {
            System.err.println("Failed to get clients: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Optional<Client> getClientByEmail(String email) {
        String sql =  "SELECT c.*, a.* " +
                "FROM clients c " +
                "INNER JOIN accounts a ON c.client_id = a.client_id " +
                "WHERE c.email = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                Client client = null;
                while (rs.next()) { // Changed from if to while to process all rows
                    if (client == null) { // Only create client once
                        client = new Client();
                        client.setId(rs.getLong("client_id"));
                        client.setFirstName(rs.getString("first_name"));
                        client.setLastName(rs.getString("last_name"));
                        client.setEmail(rs.getString("email"));
                        client.setPhone(rs.getString("phone"));
                        client.setAddress(rs.getString("address"));
                        client.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                        client.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
                        client.setActive(rs.getBoolean("is_active"));
                    }
                    // Create account for each row
                    Account account = new Account();
                    account.setId(rs.getLong("account_id"));
                    account.setAccountType(AccountType.valueOf((rs.getString("account_type"))));
                    account.setBalance(rs.getBigDecimal("balance"));
                    account.setCurrency(rs.getString("currency"));
                    account.setStatus(rs.getString("status"));
                    account.setAccountRib(rs.getString("account_rib"));
                    account.setCustomerId(client.getId());
                    client.getAccounts().add(account);
                }
                if (client != null) {
                    return Optional.of(client);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    }
