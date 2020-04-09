package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrdelivery.inputhandler.RegexChecks;
import com.example.mrdelivery.inputhandler.inputvalidators.EmailValidator;
import com.example.mrdelivery.inputhandler.inputvalidators.PasswordValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.mrdelivery.inputhandler.RegexChecks.*;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout loginPasswordLayout, loginEmailLayout;
    private TextView forgotPassword;
    private FirebaseAuth mAuth;
    private Button loginButton;
    private List<TextInputLayout> inputList;

    private View.OnClickListener loginListener;
    private View.OnClickListener resetListener;

    private boolean resetFlag = false;

    @Override
    public void onBackPressed()
    {
        if(resetFlag)
        {
            forgotPassword.setVisibility(View.VISIBLE);
            loginPasswordLayout.setVisibility(View.VISIBLE);
            loginButton.setText(this.getString(R.string.login_id));
            loginButton.setOnClickListener(loginListener);
            resetFlag = false;
        }

        else
        {
            finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailLayout = findViewById(R.id.loginEmailLayout);
        loginPasswordLayout = findViewById(R.id.loginPasswordLayout);
        forgotPassword = findViewById(R.id.forget_password_link);
        loginButton = findViewById(R.id.login_btn);

        inputList = new ArrayList<>();
        Collections.addAll(inputList, loginEmailLayout, loginPasswordLayout);

        loginEmailLayout.getEditText().addTextChangedListener(new EmailValidator(loginEmailLayout));
        loginPasswordLayout.getEditText().addTextChangedListener(new PasswordValidator(loginPasswordLayout){
            @Override
            public void validate(String input)
            {
                if(!TextUtils.isEmpty(input))
                {
                    this.textInput.setError(null);
                }
            }
        });

        loginListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        };

        resetListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = Objects.requireNonNull(loginEmailLayout.getEditText()).getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    loginEmailLayout.setError("Please fill this field.");
                }
                else if(isValidEmailID(email)){
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("LOGINDEBUG", "Email Sent");;
                                        Toast.makeText(LoginActivity.this,"Password reset mail sent", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
        loginButton.setOnClickListener(loginListener);
    }

    private void authenticateUser(){
        final String email = loginEmailLayout.getEditText().getText().toString();
        final String password = loginPasswordLayout.getEditText().getText().toString();

        // ADD HORIZONTAL LAYOUTS

        boolean fieldsNotFilled = (TextUtils.isEmpty(email) || TextUtils.isEmpty(password));

        if(fieldsNotFilled)
        {
            for(TextInputLayout inputViews: inputList)
            {
                if(TextUtils.isEmpty(Objects.requireNonNull(inputViews.getEditText().getText()).toString()))
                {
                    inputViews.setError("Please fill this field.");
                }
            }
        }
        else
        {
            boolean isValidInput = validateUserLogin(email);

            if(isValidInput)
            {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Log.d("LOGINDEBUG", "signIn:Success");
                                    Toast.makeText(LoginActivity.this,"Login Successful!", Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // Redirect to main intent and pass user obj
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                if(e instanceof FirebaseAuthInvalidCredentialsException)
                                {
                                    Toast.makeText(LoginActivity.this, "Incorrect password, please try again", Toast.LENGTH_SHORT).show();
                                }
                                else if(e instanceof FirebaseAuthInvalidUserException)
                                {
                                    Toast.makeText(LoginActivity.this, "Incorrect Email, please try again", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                });
            }
        }
    }

    private void forgotPassword()
    {
        loginPasswordLayout.setVisibility(View.INVISIBLE);
        forgotPassword.setVisibility(View.INVISIBLE);
        loginButton.setText(this.getString(R.string.reset_pw_email));

        final String email = loginEmailLayout.getEditText().getText().toString();

        loginButton.setOnClickListener(resetListener);
        resetFlag = true;
    }
}
