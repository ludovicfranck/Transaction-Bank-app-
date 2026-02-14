package com.app.bank.utils;

import java.time.Year;

public class AccountUtils {

    /**
     * representation of a bank account number
     *  2026+xxxxxx  = e.g  = 2026101010
     */

    /**
     * Method in charge to generate an accountNumber
     */
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "The User already has an account created ! ";
    public static final String ACCOUNT_EXISTS_SUCCESS_CODE = "002";
    public static final String ACCOUNT_EXISTS_SUCCESS_MESSAGE = "User created successfully !" ;

    public static String generateAccountNumber(){

        Year year = Year.now();

        int min_number_six_digit = 100000;
        int max_number_six_digit = 999999;

        // Math.random include in  [0;1[  | max-min+1 counts all term including max

        // ranNum = (int) Math.floor(Math.random() * (max - min + 1)) + min

        int randomNumber = (int) Math.floor(Math.random() * (max_number_six_digit - min_number_six_digit + 1)) + min_number_six_digit;

        String yearString = String.valueOf(year) ;
        String randNumberString = String.valueOf(randomNumber);

        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(yearString).append(randNumberString).toString();
    }
}
