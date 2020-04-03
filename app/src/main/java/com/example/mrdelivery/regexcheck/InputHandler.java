package com.example.mrdelivery.regexcheck;

import android.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InputHandler {
    private static final String FULL_NAME_PATTERN = "^ *[a-zA-Z'\\. ]+ *$";
    private static final String EMAIL_PATTERN = "^ *([\\w\\-+]*[\\w]+@[a-zA-Z0-9\\-]+).([a-zA-Z]{2,}) *$";
    private static final String PHONE_NUM_PATTERN = "^ *(\\+?\\d{1,3})?[789][\\d]{9} *$";
    private static final String PASSWORD_PATTERN = "^.{8,}$";

    private static final String INVALID_INPUT = "Please enter a valid ";
    private static final String INVALID_NAME = "Name";
    private static final String INVALID_EMAIL = "Email-ID";
    private static final String INVALID_PHONE = "Phone Number";
    private static final String INVALID_PASSWORD = "Your Password should not be less than 8 characters";

    private static boolean isValidName(String fullName)
    {
        return (Pattern.compile(FULL_NAME_PATTERN)).matcher(fullName).matches();
    }

    private static boolean isValidEmailID(String emailID)
    {
        return (Pattern.compile(EMAIL_PATTERN)).matcher(emailID).matches();
    }

    private static boolean isValidPhoneNum(String phoneNum)
    {
        return (Pattern.compile(PHONE_NUM_PATTERN)).matcher(phoneNum).matches();
    }

    private static boolean isValidPassword(String password)
    {
        return (Pattern.compile(PASSWORD_PATTERN)).matcher(password).matches();
    }

    public static Pair<Boolean, String> validateUserReg(String fullName, String emailID, String phoneNum, String password)
    {
        if(!isValidName(fullName))
        {
            return new Pair<>(false, INVALID_INPUT + INVALID_NAME);
        }
        else if(!isValidEmailID(emailID))
        {
            return new Pair<>(false, INVALID_INPUT + INVALID_EMAIL);
        }
        else if(!isValidPhoneNum(phoneNum))
        {
            return new Pair<>(false, INVALID_INPUT + INVALID_PHONE);
        }
        else if(!isValidPassword(password))
        {
            return new Pair<>(false, INVALID_PASSWORD);
        }
        else
        {
            return new Pair<>(true, "");
        }
    }

    public static Pair<Boolean, String> validateUserLogin(String emailID)
    {
        if(isValidEmailID(emailID))
        {
            return new Pair<>(true, "");
        }
        else
        {
            return new Pair<>(false, INVALID_INPUT + INVALID_EMAIL);
        }
    }

    public static String getValidEMAILID(String emailID)
    {
        Pattern pat = Pattern.compile(EMAIL_PATTERN);
        Matcher match = pat.matcher(emailID);

        if(match.find() && isValidEmailID(emailID))
        {
            return (match.group(1) + "," + match.group(2));
        }
        else
        {
            throw new RuntimeException("REGEX ERROR");
        }
    }
}