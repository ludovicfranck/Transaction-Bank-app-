package com.app.bank.dto;

import lombok.*;

// represent the DTO of User Entity or Model

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    @NonNull
    private String email;
    private String phoneNumber;
    private String alternativePhoneNumber;
}
