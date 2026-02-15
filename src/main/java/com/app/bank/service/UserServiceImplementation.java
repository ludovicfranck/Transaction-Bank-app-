package com.app.bank.service;

import com.app.bank.dto.AccountInfo;
import com.app.bank.dto.BankResponse;
import com.app.bank.dto.EmailDetails;
import com.app.bank.dto.UserRequest;
import com.app.bank.model.User;
import com.app.bank.repository.UserRepository;
import com.app.bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImplementation implements UserService{

    private final UserRepository userRepository;
    private final EmailService emailService ;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository , EmailServiceImplementation emailServiceImplementation){
        this.userRepository = userRepository ;
        this.emailService = emailServiceImplementation;
    }

   // create an account  = save a new user into the database ...
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        // check if a user already has an account ...
        if (userRepository.existsByEmail(userRequest.getEmail())){
             return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                     .accountInfo(null)
                    .build();
        }
        User newUser  =  User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        // ---------- code in charge to inform the creation of his bank account !
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                    .subject("BANK ACCOUNT CREATED !")
                    .messageBody("Your account was successfully created !\n Your Account Details :\n" +
                        "\tAccount Name : " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n " +
                        "\tYour Account Number : " + savedUser.getAccountNumber())
                .build();
                emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(savedUser.getAccountNumber())
                        .accountBalance(savedUser.getAccountBalance())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();


    }
}
