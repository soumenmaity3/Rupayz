package com.soumen.upi.Repository;

import com.soumen.upi.Model.User.BankAccount;
import com.soumen.upi.Model.User.UPIUser;
import com.soumen.upi.Service.AccountInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, UUID> {
    @Query("SELECT b FROM BankAccount b WHERE b.user.email = ?1")
    boolean findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BankAccount b WHERE b.user.email = :email")
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE upi_bank_account SET balance = balance + :amount WHERE account_number = :accountNumber", nativeQuery = true)
    void addMoneyInAccount(@Param("amount") BigDecimal amount, @Param("accountNumber") String accountNumber);

    // set primary
    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.isPrimary = false WHERE b.user.email = :email")
    void resetPrimaryForUser(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.isPrimary = true WHERE b.accountNumber = :accountNo")
    void setPrimaryAccount(@Param("accountNo") String accountNo);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.accountPassword = :upiPin WHERE b.accountNumber = :accountNo")
    void setUpiPin(String accountNo, String upiPin);

    @Query("SELECT b.accountPassword FROM BankAccount b WHERE b.accountNumber = :accountNo")
    String getUpiPin(String accountNo);

    @Modifying
    @Transactional
    @Query(value = "UPDATE upi_bank_account SET balance = balance - :amount WHERE account_number = :accountNo", nativeQuery = true)
    int debit(@Param("accountNo") String accountNo, @Param("amount") BigDecimal amount);

    @Modifying
    @Transactional
    @Query(value = "UPDATE upi_bank_account SET balance = balance + :amount WHERE user_email = :toEmail AND is_primary = true", nativeQuery = true)
    int credit(@Param("toEmail") String toEmail, @Param("amount") BigDecimal amount);


    @Query(value = "SELECT account_number AS accountNumber, balance AS balance " +
            "FROM upi_bank_account " +
            "WHERE user_email = :email AND is_primary = true", nativeQuery = true)
    Optional<AccountInfo> getPrimaryAccountAndBalance(@Param("email") String email);

    @Query(
            value = "SELECT * FROM upi_bank_account WHERE user_email = :email AND account_number = :accountNo",
            nativeQuery = true
    )
    Optional<BankAccount> verifyAccount(@Param("email") String email, @Param("accountNo") String accountNo);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE upi_bank_account SET balance = balance + :amount WHERE account_number = :toAcc", nativeQuery = true)
    int creditInBank(@Param("toAcc") String toAcc, @Param("amount") BigDecimal amount);

    @Query(
            value = "SELECT * FROM upi_bank_account WHERE account_number = :toAcc",
            nativeQuery = true
    )
    Optional<BankAccount> findByAccountNumber(String toAcc);
}
