package com.app.bank.service;

import com.app.bank.dto.*;
import com.app.bank.exception.UserNotFoundException;
import com.app.bank.model.User;
import com.app.bank.repository.UserRepository;
import com.app.bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

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
    public BankResponse createAccount(UserRequest userRequest) throws UserNotFoundException {
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

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        // check if the provided accountNumber exists in the database
        Boolean isAccountNumberExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountNumberExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountNumberExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountNumberExist){
            return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();

    }

    // to remove a special amount in the account's user !
    // resultAmount = AccountBalance - accountAmount.getAmount ;
    @Override
    public BankResponse creditAccount(RequestAmount requestAmount) {
        Boolean isAccountNumberExist = userRepository.existsByAccountNumber(requestAmount.getAccountNumber());
        if(!isAccountNumberExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(requestAmount.getAccountNumber());
        BigDecimal resultBalance = foundUser.getAccountBalance().add(requestAmount.getAmount());
        foundUser.setAccountBalance(resultBalance);
        userRepository.save(foundUser);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .build())
                .build();
    }


    // Add an amount in the account's user !
    // resultAmount = AccountBalance + accountAmount.getAmount() ;
    @Override
    public BankResponse debitAccount(RequestAmount requestAmount) {
        Boolean isAccountNumberExists = userRepository.existsByAccountNumber(requestAmount.getAccountNumber());
        if (!isAccountNumberExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else{
            User foundUser  = userRepository.findByAccountNumber(requestAmount.getAccountNumber());
            BigInteger availableBalance = foundUser.getAccountBalance().toBigInteger();
            BigInteger amountBigInteger = requestAmount.getAmount().toBigInteger();
            // available_balance < amountToWithdraw
            if (availableBalance.intValue() < amountBigInteger.intValue()){
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                                .accountBalance(foundUser.getAccountBalance())
                                .accountNumber(foundUser.getAccountNumber())
                                .build())
                        .build();
            }
            else{
                BigDecimal resultBalance = foundUser.getAccountBalance().subtract(requestAmount.getAmount());
                foundUser.setAccountBalance(resultBalance);
                userRepository.save(foundUser);
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_SUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_SUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                                .accountBalance(foundUser.getAccountBalance())
                                .accountNumber(foundUser.getAccountNumber())
                                .build())
                        .build();
            }
        }

    }

    @Override
    public BankResponse transferAmount(TransferRequest transferRequest) throws UserNotFoundException {
        // step 1 : get the account to debit ( check if the account exists )
        // step 2 : check if the debited's amount is not more than the current Balance of the account
        // step 3 : debit the account ( subtract the current balance )
        // step 4 : get the account to credit (check if the account exists )
        // step 5 : credit the account ( add the current balance of the account )
        Boolean isSourceAccountNumberExists = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
        // if the Source account number doesn't exist ...
        if (!isSourceAccountNumberExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_SOURCE_NUMBER_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_SOURCE_NUMBER_NOT_FOUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else{ // step 2 continuation ...
            // find the Sender User
            User findSenderUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
            BigDecimal currentBalance = findSenderUser.getAccountBalance();
            // comparison between the balance and the amount to transfer ...
            if(currentBalance.compareTo(transferRequest.getAmount()) < 0){
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            else{ // the current Balance is greater than the amount to transfer ...
                Boolean isDestinationAccountNumberExists = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
                if (!isDestinationAccountNumberExists){
                    return BankResponse.builder()
                            .responseCode(AccountUtils.ACCOUNT_DESTINATION_NUMBER_NOT_FOUND_CODE)
                            .responseMessage(AccountUtils.ACCOUNT_DESTINATION_NUMBER_NOT_FOUND_MESSAGE)
                            .accountInfo(null)
                            .build();
                }
                else{
                    BigDecimal newBalanceAfterDebit  = findSenderUser.getAccountBalance().subtract(transferRequest.getAmount());
                    findSenderUser.setAccountBalance(newBalanceAfterDebit);
                    userRepository.save(findSenderUser);
                    User receiverTransferUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
                    BigDecimal newBalanceAfterCredit = receiverTransferUser.getAccountBalance().add(transferRequest.getAmount());
                    receiverTransferUser.setAccountBalance(newBalanceAfterCredit);
                    userRepository.save(receiverTransferUser);

                    // Send Email Alert after the Debit Operation of the account !
                    EmailDetails emailsDetailsDebit = EmailDetails.builder()
                            .subject("DEBIT ALERT !")
                            .recipient(findSenderUser.getEmail())
                            .messageBody("The Sum of : $" + transferRequest.getAmount() + " has been deducted from Account \n" +
                                    "Your Current Balance is : " + "$" + findSenderUser.getAccountBalance())
                            .build();

                    emailService.sendEmailAlert(emailsDetailsDebit);

                    // send Email Alert for the Credit Operation of the Account !
                    EmailDetails emailDetailsCredit = EmailDetails.builder()
                            .subject("CREDIT ALERT") // this is for the receiver ...
                            .recipient(receiverTransferUser.getEmail())
                            .messageBody("The Sum of : " + transferRequest.getAmount() + " has been sent from your Account \n" +
                                    "Your Current Balance is : " + "$" + receiverTransferUser.getAccountBalance() + "\n" +
                                    "Sender name : " + findSenderUser.getFirstName() + " " + findSenderUser.getLastName())
                            .build();
                    emailService.sendEmailAlert(emailDetailsCredit);

                    return BankResponse.builder()
                            .responseCode(AccountUtils.ACCOUNT_DESTINATION_NUMBER_FOUND_CODE)
                            .responseMessage(AccountUtils.ACCOUNT_DESTINATION_NUMBER_FOUND_MESSAGE)
                            .accountInfo(AccountInfo.builder()
                                    .accountName(receiverTransferUser.getFirstName() + " " + receiverTransferUser.getLastName())
                                    .accountBalance(receiverTransferUser.getAccountBalance())
                                    .accountNumber(receiverTransferUser.getAccountNumber())
                                    .build())
                            .build();
                }
            }
        }
    }



}
