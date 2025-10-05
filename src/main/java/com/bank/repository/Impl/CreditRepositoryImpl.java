package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.models.Credit;
import com.bank.repository.interfaces.CreditRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CreditRepositoryImpl implements CreditRepository {
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    Connection conn;

    public CreditRepositoryImpl() {
        this.conn = databaseConfig.getConnection();
    }

    @Override
    public List<Credit> getPendingCredits() {
        String sql = "SELECT c.*, " +
                "cl.first_name || ' ' || cl.last_name as client_name, " +
                "cl.email as client_email, " +
                "a.account_rib " +
                "FROM credits c " +
                "JOIN accounts a ON c.account_id = a.account_id " +
                "JOIN clients cl ON a.client_id = cl.client_id " +
                "WHERE c.credit_status = 'PENDING' " +
                "ORDER BY c.credit_id DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<Credit> credits = new ArrayList<>();

            while (rs.next()) {
                Credit credit = new Credit();
                credit.setCreditId(rs.getLong("credit_id"));
                credit.setAccountId(rs.getLong("account_id"));
                credit.setLoanAmount(rs.getBigDecimal("loan_amount"));
                credit.setInterestRate(rs.getBigDecimal("interest_rate"));
                credit.setLoanTermMonths(rs.getInt("loan_term_months"));
                credit.setStartDate(rs.getDate("start_date").toLocalDate());
                credit.setEndDate(rs.getDate("end_date").toLocalDate());
                credit.setCreditStatus(rs.getString("credit_status"));

                // Set display fields
                credit.setClientName(rs.getString("client_name"));
                credit.setClientEmail(rs.getString("client_email"));
                credit.setAccountRib(rs.getString("account_rib"));

                credits.add(credit);
            }
            return credits;
        } catch (Exception e) {
            System.out.println("Error fetching pending credits: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public Credit getCreditById(Long creditId) {
        String sql = "SELECT c.*, " +
                "cl.first_name || ' ' || cl.last_name as client_name, " +
                "cl.email as client_email, " +
                "a.account_rib " +
                "FROM credits c " +
                "JOIN accounts a ON c.account_id = a.account_id " +
                "JOIN clients cl ON a.client_id = cl.client_id " +
                "WHERE c.credit_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, creditId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Credit credit = new Credit();
                credit.setCreditId(rs.getLong("credit_id"));
                credit.setAccountId(rs.getLong("account_id"));
                credit.setLoanAmount(rs.getBigDecimal("loan_amount"));
                credit.setInterestRate(rs.getBigDecimal("interest_rate"));
                credit.setLoanTermMonths(rs.getInt("loan_term_months"));
                credit.setStartDate(rs.getDate("start_date").toLocalDate());
                credit.setEndDate(rs.getDate("end_date").toLocalDate());
                credit.setCreditStatus(rs.getString("credit_status"));

                // Set display fields
                credit.setClientName(rs.getString("client_name"));
                credit.setClientEmail(rs.getString("client_email"));
                credit.setAccountRib(rs.getString("account_rib"));

                return credit;
            }
        } catch (Exception e) {
            System.out.println("Error fetching credit by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateCreditStatus(Long creditId, String creditStatus, Long managerId) {
        // Since you don't need managerId, we'll ignore it and just update the status
        String sql = "UPDATE credits SET credit_status = ? WHERE credit_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, creditStatus);
            stmt.setLong(2, creditId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No credit found with ID: " + creditId);
            }
        } catch (Exception e) {
            System.out.println("Error updating credit status: " + e.getMessage());
            throw new RuntimeException("Failed to update credit status: " + e.getMessage());
        }
    }

    @Override
    public Credit createCreditRequest(Credit credit) {
        String sql = "INSERT INTO credits (account_id, loan_amount, interest_rate, loan_term_months, start_date, end_date, credit_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'PENDING') RETURNING credit_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, credit.getAccountId());
            stmt.setBigDecimal(2, credit.getLoanAmount());
            stmt.setBigDecimal(3, credit.getInterestRate());
            stmt.setInt(4, credit.getLoanTermMonths());
            stmt.setDate(5, java.sql.Date.valueOf(credit.getStartDate()));
            stmt.setDate(6, java.sql.Date.valueOf(credit.getEndDate()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                credit.setCreditId(rs.getLong("credit_id"));
                credit.setCreditStatus("PENDING");
                return credit;
            }
        } catch (Exception e) {
            System.out.println("Error creating credit request: " + e.getMessage());
        }
        return null;
    }
}
