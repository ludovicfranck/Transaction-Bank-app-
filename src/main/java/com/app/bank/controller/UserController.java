package com.app.bank.controller;

import com.app.bank.dto.BankResponse;
import com.app.bank.dto.EnquiryRequest;
import com.app.bank.dto.UserRequest;
import com.app.bank.service.UserService;
import com.app.bank.service.UserServiceImplementation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImplementation userServiceImplementation){
        this.userService = userServiceImplementation ;
    }

    @PostMapping
    @ResponseBody
    public BankResponse createAnAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }
    @GetMapping("/balanceEnquiry")
    @ResponseBody
    public BankResponse getBalanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }
    @GetMapping("/nameEnquiry")
    @ResponseBody
    public String getNameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.nameEnquiry(enquiryRequest);
    }

}