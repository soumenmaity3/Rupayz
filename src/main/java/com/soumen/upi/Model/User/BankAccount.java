package com.soumen.upi.Model.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "upi_bank_account")
public class BankAccount {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID accountId;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    @JsonBackReference("user-bankAccounts")
    private UPIUser user;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    @JsonBackReference("bank-bankAccounts")
    private BankDetails bank;

    @Column(nullable = false, length = 100)
    private String bankName;

    @Column(nullable = false, unique = true, length = 15)
    private String accountNumber;

    @Column(nullable = false, length = 11)
    private String ifscCode;

    private String branchName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType accountType;

    @Column(precision = 18, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    private Boolean isPrimary = false;

    private String accountPassword;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.accountNumber == null) {
            this.accountNumber = String.valueOf(System.currentTimeMillis());
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

