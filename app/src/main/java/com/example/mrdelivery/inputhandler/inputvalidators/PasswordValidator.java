package com.example.mrdelivery.inputhandler.inputvalidators;

import com.example.mrdelivery.inputhandler.RegexChecks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class PasswordValidator extends TextValidator {

    public PasswordValidator(TextInputLayout textInput) {
        super(textInput);;
    }

    @Override
    public void validate(String input) {
        if(!RegexChecks.isValidPassword(input))
        {
            textInput.setError(RegexChecks.INVALID_PASSWORD);
        }
        else
        {
            textInput.setError(null);
        }
    }
}
