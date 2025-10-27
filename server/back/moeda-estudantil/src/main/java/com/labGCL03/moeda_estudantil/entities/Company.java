package com.labGCL03.moeda_estudantil.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "companies")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Company extends User {

    @Column(unique = true)
    private String cnpj;

    @Column(length = 500)
    private String address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Advantage> advantages;

    @Override
    public void login() {
        // Implementação específica para login da empresa
        System.out.println("Empresa " + getName() + " logou no sistema");
    }

    @Override
    public void logout() {
        // Implementação específica para logout da empresa
        System.out.println("Empresa " + getName() + " deslogou do sistema");
    }

    public void registerAdvantage(Advantage advantage) {
        advantage.setCompany(this);
        this.advantages.add(advantage);
    }

    public List<Advantage> getAdvantages() {
        return this.advantages;
    }

    public boolean processRedemption(String code) {
        // Implementação para processar resgate com código
        return true;
    }
}