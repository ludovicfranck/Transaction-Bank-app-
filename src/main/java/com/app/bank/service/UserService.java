package com.app.bank.service;

import com.app.bank.dto.BankResponse;
import com.app.bank.dto.UserRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}
