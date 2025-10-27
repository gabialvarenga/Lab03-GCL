package com.labGCL03.moeda_estudantil.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Student extends User {

    @Column(unique = true)
    private String cpf;

    private String rg;

    @Column(length = 500)
    private String address;

    private String course;

    @Column(name = "coin_balance", nullable = false)
    private Integer coinBalance = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Coupon> coupons;

    @Override
    public void login() {
        // Implementação específica para login do estudante
        System.out.println("Estudante " + getName() + " logou no sistema");
    }

    @Override
    public void logout() {
        // Implementação específica para logout do estudante
        System.out.println("Estudante " + getName() + " deslogou do sistema");
    }

    public void receiveCoin(Integer amount, String reason) {
        this.coinBalance += amount;
    }

    public Coupon redeemAdvantage(Advantage advantage) {
        if (this.coinBalance >= advantage.getCostInCoins()) {
            this.coinBalance -= advantage.getCostInCoins();
            // Criar e retornar cupom
            return new Coupon();
        }
        throw new IllegalArgumentException("Saldo insuficiente");
    }

    public List<Transaction> getTransactionHistory() {
        return getReceivedTransactions();
    }

    public Integer getBalance() {
        return this.coinBalance;
    }

    @PrePersist
    @PreUpdate
    private void validateBalance() {
        if (coinBalance < 0) {
            throw new IllegalArgumentException("Saldo não pode ser negativo");
        }
    }
}