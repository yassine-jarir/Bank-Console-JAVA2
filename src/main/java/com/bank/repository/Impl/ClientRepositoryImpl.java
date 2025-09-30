package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.models.Client;
import com.bank.repository.interfaces.ClientInterface;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepositoryImpl implements ClientInterface {
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    Connection conn;

    public ClientRepositoryImpl() {
        this.conn = DatabaseConfig.getInstance().getConnection();
    }


    @Override
    public Client createClient(Client client) {
        String sql = "INSERT INTO clients (first_name, last_name, email, phone, address, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql);

        ) {
            stmt.setString(1, client.getFirstName());
            stmt.setString(2, client.getLastName());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getAddress());
            stmt.setDate(6, Date.valueOf(client.getDateOfBirth()));
            stmt.executeUpdate();

            return client;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Client> getAllClients() {
        String sql = "SELECT * FROM clients";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();) {
            List<Client> clients = new ArrayList<>();
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getLong("client_id"));
                client.setFirstName(rs.getString("first_name"));
                client.setLastName(rs.getString("last_name"));
                client.setEmail(rs.getString("email"));
                client.setPhone(rs.getString("phone"));
                client.setAddress(rs.getString("address"));
                client.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                client.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
                client.setActive(rs.getBoolean("is_active"));
                clients.add(client);
            }
            return clients;
        } catch (Exception e) {
            System.err.println("Failed to create client: " + e.getMessage());
            return null;
        }
    }

    public Optional<Client> getClientByEmail(String email) {
        String sql = "SELECT * FROM clients WHERE email = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getLong("client_id"));
                    client.setFirstName(rs.getString("first_name"));
                    client.setLastName(rs.getString("last_name"));
                    client.setEmail(rs.getString("email"));
                    client.setPhone(rs.getString("phone"));
                    client.setAddress(rs.getString("address"));
                    client.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                    client.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
                    client.setActive(rs.getBoolean("is_active"));
                    return Optional.of(client);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    }

