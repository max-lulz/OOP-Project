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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import static com.example.mrdelivery.regexcheck.InputHandler.*;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private TextView forgotPassword;
    private FirebaseAuth mAuth;
    private Button loginButton;

    private View.OnClickListener loginListener;
    private View.OnClickListener resetListener;

    private boolean resetFlag = false;

    @Override
    public void onBackPressed()
    {
        if(resetFlag)
        {
            inputPassword.setVisibility(View.VISIBLE);
            forgotPassword.setVisibility(View.VISIBLE);
            loginButton.setText(this.getString(R.string.login_id));
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

        inputEmail = findViewById(R.id.login_email_input);
        inputPassword = findViewById(R.id.login_password_input);
        forgotPassword = findViewById(R.id.forget_password_link);
        loginButton = findViewById(R.id.login_btn);

        loginListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        };

        resetListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    // replace with set errors
                    Toast.makeText(LoginActivity.this,"Please enter your Email", Toast.LENGTH_SHORT).show();
                }
                else if(!isValidEmailID(email)){
                    Toast.makeText(LoginActivity.this,"Please enter a valid Email", Toast.LENGTH_SHORT).show();
                }
                else{
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
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        // ADD HORIZONTAL LAYOUTS

        boolean fieldsNotFilled = (TextUtils.isEmpty(email) || TextUtils.isEmpty(password));

        if(fieldsNotFilled)
        {
            Toast.makeText(this,"Please fill all Fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Pair<Boolean, String> inputValidation = validateUserLogin(email);

            if(!inputValidation.first)
            {
                Toast.makeText(this, inputValidation.second, Toast.LENGTH_SHORT).show();
            }

            else
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
        inputPassword.setVisibility(View.INVISIBLE);
        forgotPassword.setVisibility(View.INVISIBLE);
        loginButton.setText(this.getString(R.string.reset_pw_email));

        final String email = inputEmail.getText().toString();

        loginButton.setOnClickListener(resetListener);
        resetFlag = true;
    }
}
