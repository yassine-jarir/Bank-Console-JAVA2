package com.bank.repository.Impl;

import com.bank.config.DatabaseConfig;
import com.bank.models.CreditPayment;
import com.bank.repository.interfaces.CreditPaymentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreditPaymentRepositoryImpl implements CreditPaymentRepository {
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    Connection conn;

    public CreditPaymentRepositoryImpl() {
        this.conn = databaseConfig.getConnection();
    }

    @Override
    public List<CreditPayment> getPaymentsByCreditId(Long creditId) {
        String sql = "SELECT * FROM credit_payments WHERE credit_id = ? ORDER BY due_date";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, creditId);
            ResultSet rs = stmt.executeQuery();
            List<CreditPayment> payments = new ArrayList<>();

            while (rs.next()) {
                CreditPayment payment = new CreditPayment();
                payment.setPaymentId(rs.getLong("payment_id"));
                payment.setCreditId(rs.getLong("credit_id"));
                payment.setDueDate(rs.getDate("due_date").toLocalDate());
                if (rs.getDate("payment_date") != null) {
                    payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                }
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setStatus(rs.getString("status"));
                payments.add(payment);
            }
            return payments;
        } catch (Exception e) {
            System.out.println("Error getting payments: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public CreditPayment getNextPendingPayment(Long creditId) {
        String sql = "SELECT * FROM credit_payments WHERE credit_id = ? AND status = 'PENDING' ORDER BY due_date LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, creditId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CreditPayment payment = new CreditPayment();
                payment.setPaymentId(rs.getLong("payment_id"));
                payment.setCreditId(rs.getLong("credit_id"));
                payment.setDueDate(rs.getDate("due_date").toLocalDate());
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setStatus(rs.getString("status"));
                return payment;
            }
        } catch (Exception e) {
            System.out.println("Error getting next pending payment: " + e.getMessage());
        }
        return null;
    }

    public CreditPayment getPendingPaymentByAmount(BigDecimal amount) {
        String sql = "SELECT * FROM credit_payments WHERE status = 'PENDING' AND amount = ? ORDER BY due_date LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CreditPayment payment = new CreditPayment();
                payment.setPaymentId(rs.getLong("payment_id"));
                payment.setCreditId(rs.getLong("credit_id"));
                payment.setDueDate(rs.getDate("due_date").toLocalDate());
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setStatus(rs.getString("status"));
                return payment;
            }
        } catch (Exception e) {
            System.out.println("Error getting pending payment by amount: " + e.getMessage());
        }
        return null;
    }

    public CreditPayment getPendingPaymentByClientAndAmount(Long clientId, BigDecimal amount) {
        String sql = """
            SELECT cp.* FROM credit_payments cp 
            JOIN credits c ON cp.credit_id = c.credit_id 
            JOIN accounts a ON c.account_id = a.account_id 
            WHERE a.client_id = ? AND cp.status = 'PENDING' AND cp.amount = ? 
            ORDER BY cp.due_date LIMIT 1
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            stmt.setBigDecimal(2, amount);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CreditPayment payment = new CreditPayment();
                payment.setPaymentId(rs.getLong("payment_id"));
                payment.setCreditId(rs.getLong("credit_id"));
                payment.setDueDate(rs.getDate("due_date").toLocalDate());
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setStatus(rs.getString("status"));
                return payment;
            }
        } catch (Exception e) {
            System.out.println("Error getting pending payment by client and amount: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void createPayment(CreditPayment payment) {
        String sql = "INSERT INTO credit_payments (credit_id, due_date, amount, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, payment.getCreditId());
            stmt.setDate(2, java.sql.Date.valueOf(payment.getDueDate()));
            stmt.setBigDecimal(3, payment.getAmount());
            stmt.setString(4, payment.getStatus());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error creating payment: " + e.getMessage());
        }
    }

    @Override
    public void updatePaymentStatus(Long paymentId, String status) {
        String sql = "UPDATE credit_payments SET status = ? WHERE payment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, paymentId);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating payment status: " + e.getMessage());
        }
    }

    @Override
    public void updatePaymentAsPaid(Long paymentId) {
        String sql = "UPDATE credit_payments SET status = 'PAID', payment_date = ? WHERE payment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setLong(2, paymentId);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error marking payment as paid: " + e.getMessage());
        }
    }

    @Override
    public void createMonthlyPayments(Long creditId, int months, BigDecimal monthlyAmount, LocalDate startDate , BigDecimal interestRate) {
        BigDecimal interest = monthlyAmount.multiply(interestRate).divide(new BigDecimal("100"));
        monthlyAmount = monthlyAmount.add(interest);

        for (int i = 1; i <= months; i++) {
            LocalDate dueDate = startDate.plusMonths(i);
            CreditPayment payment = new CreditPayment(creditId, dueDate, monthlyAmount);
            createPayment(payment);
        }
    }

    @Override
    public void updateOverduePayments() {
        String sql = "UPDATE credit_payments SET status = 'LATE' WHERE status = 'PENDING' AND due_date < ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Updated " + updated + " overdue payments to LATE status");
            }
        } catch (Exception e) {
            System.out.println("Error updating overdue payments: " + e.getMessage());
        }
    }
    public void addAmountToAccountBalance(Long accountId, java.math.BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setLong(2, accountId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No account found with ID: " + accountId);
            }
        } catch (Exception e) {
            System.out.println("Error adding amount to account balance: " + e.getMessage());
            throw new RuntimeException("Failed to add amount to account balance: " + e.getMessage());
        }
    }

    public void checkOverduePayments() {
        updateOverduePayments();
    }

    public CreditPayment getNextPendingPaymentByClient(Long clientId) {
        String sql = """
            SELECT cp.* FROM credit_payments cp 
            JOIN credits c ON cp.credit_id = c.credit_id 
            JOIN accounts a ON c.account_id = a.account_id 
            WHERE a.client_id = ? AND cp.status = 'PENDING' 
            ORDER BY cp.due_date LIMIT 1
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CreditPayment payment = new CreditPayment();
                payment.setPaymentId(rs.getLong("payment_id"));
                payment.setCreditId(rs.getLong("credit_id"));
                payment.setDueDate(rs.getDate("due_date").toLocalDate());
                if (rs.getDate("payment_date") != null) {
                    payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                }
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setStatus(rs.getString("status"));
                return payment;
            }
        } catch (Exception e) {
            System.out.println("Error getting next pending payment by client: " + e.getMessage());
        }
        return null;
    }

}
