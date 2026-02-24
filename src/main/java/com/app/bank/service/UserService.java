package com.app.bank.service;

import com.app.bank.dto.*;
import com.app.bank.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    BankResponse createAccount(UserRequest userRequest) throws UserNotFoundException;
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    // remove an amount in a Bank Account ...
    BankResponse creditAccount(RequestAmount requestAmount);
    BankResponse debitAccount(RequestAmount requestAmount);
    BankResponse transferAmount(TransferRequest transferRequest) throws UserNotFoundException;
}
