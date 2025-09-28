package com.soumen.upi.Service;

import com.soumen.upi.Model.User.BankDetails;
import com.soumen.upi.Model.User.AccountType;
import com.soumen.upi.Model.User.BankAccount;
import com.soumen.upi.Model.User.UPIUser;
import com.soumen.upi.Repository.BankAccountRepo;
import com.soumen.upi.Repository.BankRepo;
import com.soumen.upi.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class BankService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    BankRepo bankRepo;
    @Autowired
    BankAccountRepo bankAccountRepo;

//    public void makeAnAccount(String email, BankDetails bank) {
//        UPIUser user = userRepo.findByEmail(email);//.orElseThrow(() -> new RuntimeException("User not found"));
//
//        // If bank details already exist in DB, fetch it, otherwise save new bank
//        BankDetails existingBank = bankRepo.findByIfscCode(bank.getIfscCode())
//                .orElseGet(() -> bankRepo.save(bank));
//
//        BankAccount account = new BankAccount();
//        account.setUser(user);
//        account.setBank(existingBank);
//        account.setBankName(existingBank.getBankName());
//        account.setAccountNumber(String.valueOf(System.currentTimeMillis()));
//        account.setIfscCode(existingBank.getIfscCode());
//        account.setAccountType(AccountType.SAVINGS); // or get from request
//        account.setBalance(BigDecimal.ZERO);
//
//        if (!bankAccountRepo.findByEmail(email)){
//            account.setIsPrimary(true);
//        } else {
//            account.setIsPrimary(false);
//        }
//
//        bankAccountRepo.save(account);
//    }

    public Object getAllBanks() {
        return bankRepo.findAll();
    }
}
