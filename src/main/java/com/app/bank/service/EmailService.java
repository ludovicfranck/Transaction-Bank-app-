package com.app.bank.service;

import com.app.bank.dto.EmailDetails;
import com.app.bank.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails) throws UserNotFoundException;
}
