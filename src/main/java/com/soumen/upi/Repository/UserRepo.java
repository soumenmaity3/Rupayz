package com.soumen.upi.Repository;

import java.util.Optional;
import java.util.UUID;

import com.soumen.upi.Model.User.UPIUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UPIUser, UUID> {

    UPIUser findByEmail(String email);

    @Query("SELECT u.password FROM UPIUser u WHERE u.email = :email")
    String findPasswordByEmail(@Param("email") String email);

    @Query("SELECT u FROM UPIUser u WHERE u.email = :email")
    Optional<UPIUser> userDetailsByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM upi_users WHERE email=:email", nativeQuery = true)
    Optional<UPIUser> existEmail(String email);

    @Query(value = "SELECT * FROM upi_users WHERE TRIM(LOWER(upi_id)) = LOWER(TRIM(:upiId))", nativeQuery = true)
    Optional<UPIUser> findByUpiId(@Param("upiId") String upiId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE upi_users SET have_account=true WHERE email=:email", nativeQuery = true)
    void updateHaveAccount(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UPIUser u WHERE u.email = :email AND u.haveAccount = true")
    boolean existsWithHaveAccount(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE upi_users SET otp=:otp,otp_used=false WHERE email=:email",nativeQuery = true)
    int storeOtp(String email, String otp);

    @Modifying
    @Transactional
    @Query(value = "UPDATE upi_users SET otp_used = true WHERE email = :email",nativeQuery = true)
    int otpUsed(String email);

    @Query(value = "SELECT otp_used FROM upi_users WHERE email=:email AND otp=:otp ", nativeQuery = true)
    Boolean checkOtp(String email, String otp);


    @Modifying
    @Transactional
    @Query(value = "UPDATE upi_users SET otp_used = true WHERE email = :email",nativeQuery = true)
    int afterUseOtpUsedOtpFalse(String email);
}
