package com.app.bank.controller;

import com.app.bank.dto.BankResponse;
import com.app.bank.dto.UserRequest;
import com.app.bank.service.UserServiceImplementation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImplementation userServiceImplementation;

    public UserController(UserServiceImplementation userServiceImplementation){
        this.userServiceImplementation = userServiceImplementation ;
    }

    @PostMapping
    @ResponseBody
    public BankResponse createAnAccount(@RequestBody UserRequest userRequest){
        return userServiceImplementation.createAccount(userRequest);
    }
}