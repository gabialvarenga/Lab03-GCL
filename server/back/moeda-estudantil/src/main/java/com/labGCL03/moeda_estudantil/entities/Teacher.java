package com.labGCL03.moeda_estudantil.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User {

    @Column(unique = true)
    private String cpf;

    @Column(length = 200)
    private String department;

    @Column(name = "current_balance", nullable = false)
    private Integer currentBalance = 1000;

    @Column(name = "last_credit_period", length = 10)
    private String lastCreditPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Override
    public void login() {
        // Implementação específica para login do professor
        System.out.println("Professor " + getName() + " logou no sistema");
    }

    @Override
    public void logout() {
        // Implementação específica para logout do professor
        System.out.println("Professor " + getName() + " deslogou do sistema");
    }

    public boolean sendCoin(Student student, Integer amount, String reason) {
        if (this.currentBalance >= amount) {
            this.currentBalance -= amount;
            student.receiveCoin(amount, reason);
            return true;
        }
        return false;
    }

    public Integer getBalance() {
        return this.currentBalance;
    }

    public List<Transaction> getTransactionHistory() {
        return getSentTransactions();
    }

    public void receiveSemesterCoins() {
        this.currentBalance += 1000;
    }

    @PrePersist
    protected void onCreate() {
        if (currentBalance == null) {
            currentBalance = 1000;
        }
        validateBalance();
    }

    @PreUpdate
    protected void onUpdate() {
        validateBalance();
    }

    private void validateBalance() {
        if (currentBalance != null && currentBalance < 0) {
            throw new IllegalArgumentException("Saldo não pode ser negativo");
        }
    }
}