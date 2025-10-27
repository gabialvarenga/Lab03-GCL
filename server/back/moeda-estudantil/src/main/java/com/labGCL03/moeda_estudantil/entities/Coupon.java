package com.labGCL03.moeda_estudantil.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons", indexes = {
    @Index(name = "idx_coupon_student", columnList = "student_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "generated_date", nullable = false, updatable = false)
    private LocalDateTime generatedDate;

    @Column(nullable = false)
    private Boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advantage_id", nullable = false)
    private Advantage advantage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @PrePersist
    protected void onCreate() {
        generatedDate = LocalDateTime.now();
        if (code == null) {
            code = generateCode();
        }
    }

    public void markAsUsed() {
        this.used = true;
    }

    public String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public boolean isUsed() {
        return used;
    }
}