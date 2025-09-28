package com.soumen.upi.Repository;

import com.soumen.upi.Model.User.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface BankRepo extends JpaRepository<BankDetails, UUID> {
    @Query("SELECT b FROM BankDetails b WHERE b.ifscCode = :ifscCode")
    Optional<BankDetails> findByIfscCode(String ifscCode);
}
