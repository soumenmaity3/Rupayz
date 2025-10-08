package com.soumen.upi.Controller;

import com.soumen.upi.Authorization;
import com.soumen.upi.Model.User.*;
import com.soumen.upi.Repository.BankAccountRepo;
import com.soumen.upi.Repository.BankRepo;
import com.soumen.upi.Service.AccountInfo;
import com.soumen.upi.Service.BankService;
import com.soumen.upi.Service.OTPEmailService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import com.soumen.upi.Repository.UserRepo;
import com.soumen.upi.Service.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

@RestController
@RequestMapping("/upi")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepo repo;

    @Autowired
    BankRepo bankRepo;

    @Autowired
    BankAccountRepo bankAccountRepo;

    @Autowired
    UserService service;

    @Autowired
    BankService bankService;

    @Autowired
    Authorization authorization;

    @Autowired
    private OTPEmailService otpEmailService;


    @PostMapping("/signup")
    @Operation(summary = "Register a new UPI user")
    public ResponseEntity<?> signUp(@RequestBody UPIUser user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String email = user.getEmail();

        if (email == null || email.isEmpty()) {
            return new ResponseEntity<>("Email is required", HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<UPIUser> existingUser = repo.existEmail(email);
        if (existingUser.isPresent()) {
            return new ResponseEntity<>("User already exists with this email", HttpStatus.CONFLICT);
        }

        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            return new ResponseEntity<>("Password is required for UPI", HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            return new ResponseEntity<>("Full name is required", HttpStatus.NOT_ACCEPTABLE);
        }
        UPIUser newUser = new UPIUser();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encoder.encode(password));

        try {
            UPIUser savedUser = repo.save(newUser);
            return new ResponseEntity<>("User registered successfully with ID: " + savedUser.getUserId(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginBody login) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String email = login.getEmail();
        String rawPassword = login.getPassword();
        Optional<UPIUser> optionalUser = repo.userDetailsByEmail(email);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        UPIUser user = optionalUser.get();
        String hashPassword = user.getPassword();

        if (!encoder.matches(rawPassword, hashPassword)) {
            return new ResponseEntity<>("Password does not match", HttpStatus.BAD_REQUEST);
        }

        String token = service.verify(email, rawPassword);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authorization.token(authHeader);

        try {
            String email = service.getEmailFromToken(token);
            Optional<UPIUser> user = repo.userDetailsByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/upiId")
    public ResponseEntity<?> getUpiId(@RequestHeader("Authorization") String authHeader) {
        String token = authorization.token(authHeader);

        try {
            String email = service.getEmailFromToken(token);
            Optional<UPIUser> user = repo.userDetailsByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get().getUpiId());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/user-details-by-upiId/{upiId}")
    public ResponseEntity<?> getUserDetailsByUpiId(@RequestHeader("Authorization") String authHeader,
                                                   @PathVariable("upiId") String upiId) {
        String token = authorization.token(authHeader);
        try {
            service.getEmailFromToken(token);
            Optional<UPIUser> user = repo.findByUpiId(upiId);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with UPI ID: " + upiId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/bank-account")
    public ResponseEntity<?> getBankAccountDetails(@RequestHeader("Authorization") String authHeader) {
        String token = authorization.token(authHeader);

        try {
            String email = service.getEmailFromToken(token);
            Optional<UPIUser> user = repo.userDetailsByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get().getBankAccounts());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/all-banks")
    public ResponseEntity<?> getAllBanks(@RequestHeader("Authorization") String authHeader) {
        String token = authorization.token(authHeader);

        try {
            service.getEmailFromToken(token);
            return ResponseEntity.ok(bankService.getAllBanks());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    @GetMapping("/verify-by-email")
    public ResponseEntity<?> verifyByEmail(@RequestHeader("Authorization") String authHeader, @RequestParam("email") String email) {
        String token = authorization.token(authHeader);

        try {
            String currentUserEmail = service.getEmailFromToken(token);

            if (currentUserEmail.equals(email)) {
                return new ResponseEntity<>("Cannot verify yourself", HttpStatus.BAD_REQUEST);
            }
            Optional<UPIUser> user = repo.userDetailsByEmail(email);
            if (!user.get().isHaveAccount()) {
                return new ResponseEntity<>("User does not have a bank account", HttpStatus.NOT_FOUND);
            }
            if (user.isPresent()) {
                ArrayList<String> details = new ArrayList<>();
                details.add(user.get().getFullName());
                details.add(user.get().getUpiId());
                return ResponseEntity.ok(details);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/open-bank")
    public ResponseEntity<?> openBankAccount(@RequestHeader("Authorization") String authHeader,
                                             @RequestBody BankDetails bank) {
        System.out.println("Add Bank Account");
        String token = authorization.token(authHeader);
        String email = service.getEmailFromToken(token);
        Optional<UPIUser> user = repo.existEmail(email);
        BankDetails existingBank = bankRepo.findByIfscCode(bank.getIfscCode())
                .orElseGet(() -> bankRepo.save(bank));

        BankAccount account = new BankAccount();
        account.setUser(user.get());
        account.setBank(existingBank);
        account.setBankName(existingBank.getBankName());
        account.setBranchName(existingBank.getBranchName());
        account.setAccountNumber(account.getAccountNumber());
        account.setIfscCode(existingBank.getIfscCode());
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(ZERO);
        account.setIsPrimary(!bankAccountRepo.existsByEmail(user.get().getEmail()));


        bankAccountRepo.save(account);
        if (!repo.existsWithHaveAccount(email)) {
            repo.updateHaveAccount(email);
        }


        return ResponseEntity.ok("DONE");
    }

    @PostMapping("/add-money")
    public ResponseEntity<?> addMoney(@RequestHeader("Authorization") String authHeader,
                                      @RequestParam("amount") BigDecimal amount,
                                      @RequestParam("accountNo") String accountNumber) {
        String token = authorization.token(authHeader);
        String email = service.getEmailFromToken(token);
        Optional<UPIUser> user = repo.existEmail(email);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (amount == null || amount.compareTo(ZERO) <= 0) {
            return new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST);
        }
        if (accountNumber == null || accountNumber.isEmpty()) {
            return new ResponseEntity<>("Account number is required", HttpStatus.BAD_REQUEST);
        }
        bankAccountRepo.addMoneyInAccount(amount, accountNumber);
        return ResponseEntity.ok("Money added successfully");
    }

    @PostMapping("/otp-send-store")
    public ResponseEntity<?> otpSendStore(@RequestParam("email") String email,
                                          @RequestParam("otp") String otp) {
        try {
            otpEmailService.sendOtp(email, otp);
            int done = repo.storeOtp(email, otp);
            if (done > 0) {
                return new ResponseEntity<>("Done", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Have some error", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Error", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/check-otp")
    public ResponseEntity<?> checkOtp(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp) {
        try {
            if (email.isEmpty() && otp.isEmpty()) {
                return new ResponseEntity<>("Enter all", HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> response = new HashMap<>();
            boolean isValid = repo.checkOtp(email, otp);

            response.put("valid", isValid);

            if (isValid) {
                response.put("message", "OTP is valid");
            } else {
                response.put("message", "OTP is invalid or already used");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.badRequest().body("OTP invalid");
    }

    @PutMapping("/use-otp")
    public ResponseEntity<Map<String, Object>> useOtp(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        int rows = repo.afterUseOtpUsedOtpFalse(email);

        if (rows > 0) {
            response.put("success", true);
            response.put("message", "OTP marked as used");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Email not found or OTP already used");
            return ResponseEntity.ok(response); // always 200 for easy client parsing
        }
    }


    @PostMapping("/set-as-primary")
    public ResponseEntity<?> setAsPrimary(@RequestHeader("Authorization") String authHeader,
                                          @RequestParam("accountNo") String accountNo) {
        String token = authorization.token(authHeader);
        String email = service.getEmailFromToken(token);
        bankAccountRepo.resetPrimaryForUser(email);
        bankAccountRepo.setPrimaryAccount(accountNo);
        return ResponseEntity.ok("Done");

    }

    @PostMapping("/set-upi-pin")
    public ResponseEntity<?> setUpiPin(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam("accountNo") String accountNo,
                                       @RequestParam("upiPin") String upiPin) {
        String token = authorization.token(authHeader);
        String email = service.getEmailFromToken(token);
        Optional<UPIUser> user = repo.existEmail(email);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } else {
            bankAccountRepo.setUpiPin(accountNo, upiPin);
            return ResponseEntity.ok("UPI PIN set successfully");
        }
    }

    @GetMapping("/correct-pin")
    public ResponseEntity<?> correctPin(@RequestHeader("Authorization") String authHeader,
                                        @RequestParam("accountNo") String accountNo) {
        String token = authorization.token(authHeader);
        String email = service.getEmailFromToken(token);
        Optional<UPIUser> user = repo.existEmail(email);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } else {
            String pin = bankAccountRepo.getUpiPin(accountNo);
            System.out.println(pin);
            return ResponseEntity.ok(pin);
        }
    }

    @Transactional
    @PostMapping("/transaction")
    public ResponseEntity<?> transaction(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("am") BigDecimal amount,
            @RequestParam("to") String to,
            @RequestParam("from") String accountNo) {

        String token = authorization.token(authHeader);
        String email = service.getEmailFromToken(token);

        Optional<UPIUser> user = repo.existEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Optional<AccountInfo> recipientOpt = bankAccountRepo.getPrimaryAccountAndBalance(to);
        if (recipientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Recipient has no primary account");
        }

        // Prevent self-transfer
        if (recipientOpt.get().getAccountNumber().equals(accountNo)) {
            return ResponseEntity.badRequest().body("Cannot transfer to the same account");
        }

        Optional<BankAccount> verification = bankAccountRepo.verifyAccount(email, accountNo);
        if (verification.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid sender account");
        }

        BankAccount senderAccount = verification.get();
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        }

        // Perform debit and credit
        int debit = bankAccountRepo.debit(accountNo, amount);
        if (debit <= 0) {
            throw new RuntimeException("Debit failed");
        }

        int credit = bankAccountRepo.credit(to, amount);
        if (credit <= 0) {
            throw new RuntimeException("Credit failed");
        }

        return ResponseEntity.ok("success");
    }

    @Transactional
    @PostMapping("/self-transaction")
    public ResponseEntity<?> selfTransaction(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam("to") String toAcc,
                                             @RequestParam("from") String fromAcc,
                                             @RequestParam("am") BigDecimal amount) {
        String token = authorization.token(authHeader);
        String email = service.getEmailFromToken(token);

        Optional<UPIUser> user = repo.existEmail(email);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Get sender account
        Optional<BankAccount> senderOpt = bankAccountRepo.verifyAccount(email, fromAcc);
        if (senderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid sender account");
        }

        // Get recipient account
        Optional<BankAccount> recipientOpt = bankAccountRepo.findByAccountNumber(toAcc);
        if (recipientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Recipient account not found");
        }

        BankAccount sender = senderOpt.get();
        BankAccount recipient = recipientOpt.get();

        // Check balance
        if (sender.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        }

        // Perform transaction
        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount));

        bankAccountRepo.save(sender);
        bankAccountRepo.save(recipient);

        System.out.println("Transaction Complete");
        return ResponseEntity.ok("success");
    }


}
