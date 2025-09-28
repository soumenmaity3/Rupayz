package com.soumen.upi.Repository;

import com.soumen.upi.Model.User.BankAccount;
import com.soumen.upi.Model.User.UPIUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
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
}
