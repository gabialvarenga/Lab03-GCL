package com.labGCL03.moeda_estudantil.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "advantages", indexes = {
    @Index(name = "idx_advantage_company", columnList = "company_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Advantage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "cost_in_coins", nullable = false)
    private Integer costInCoins;

    @Lob
    @Column(name = "photo", columnDefinition = "LONGTEXT")
    private String photo; // Armazena imagem em Base64

    @Column(name = "photo_name")
    private String photoName; // Nome original do arquivo

    @Column(name = "photo_type")
    private String photoType; // Tipo MIME (image/jpeg, image/png, etc.)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "advantage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Coupon> coupons;

    // Relacionamento Many-to-Many inverso com Student
    @ManyToMany(mappedBy = "redeemedAdvantages", fetch = FetchType.LAZY)
    private List<Student> studentsWhoRedeemed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        validateCost();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        validateCost();
    }

    private void validateCost() {
        if (costInCoins != null && costInCoins <= 0) {
            throw new IllegalArgumentException("Custo deve ser positivo");
        }
    }

    public String getDetails() {
        return String.format("%s - %d moedas", name, costInCoins);
    }

    public void validateCost(Integer studentBalance) {
        if (studentBalance < costInCoins) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
    }

    public List<Student> getStudentsWhoRedeemed() {
        return studentsWhoRedeemed != null ? studentsWhoRedeemed : List.of();
    }

    public int getTimesRedeemed() {
        return studentsWhoRedeemed != null ? studentsWhoRedeemed.size() : 0;
    }

    public List<Coupon> getCoupons() {
        return coupons != null ? coupons : List.of();
    }
}