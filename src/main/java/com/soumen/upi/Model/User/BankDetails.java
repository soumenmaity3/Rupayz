package com.soumen.upi.Model.User;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "upi_bank_details")
public class BankDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID bankId;

    @Column(nullable = false, length = 150)
    private String bankName;

    @Column(nullable = false, length = 150)
    private String branchName;

    @Column(nullable = false, unique = true, length = 11)
    private String ifscCode;

    @Column(length = 15)
    private String micrCode;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String contactNo;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("bank-bankAccounts")
    private List<BankAccount> bankAccounts;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "BankDetails{" +
                "bankId=" + bankId +
                ", bankName='" + bankName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", micrCode='" + micrCode + '\'' +
                ", address='" + address + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", bankAccounts=" + bankAccounts +
                '}';
    }
}

