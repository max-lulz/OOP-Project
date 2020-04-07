package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mrdelivery.regexcheck.InputHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "DEBUGBOI";

    private EditText inputName, inputEmail, inputPassword, inputMobileNumber, inputConfirmPassword;
    private CheckBox deliveryPerson;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Redirect to main intent and pass user obj
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        Button createAccount = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_name_input);
        inputEmail = findViewById(R.id.register_email_input);
        inputMobileNumber = findViewById(R.id.register_mobilenumber_input);
        inputConfirmPassword = findViewById(R.id.register_confirmpassword_input);
        inputPassword = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);
        deliveryPerson = findViewById(R.id.delivery_rad);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createAccount();
            }
        });
    }
    private void createAccount()
    {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();
        String mobileNumber = inputMobileNumber.getText().toString();
        boolean deliveryCheck = deliveryPerson.isChecked();

        boolean fieldsNotFilled = (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(mobileNumber) ||
                TextUtils.isEmpty(confirmPassword));

        if(fieldsNotFilled)
        {
            Toast.makeText(this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this,"Passwords don't match", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Pair<Boolean, String> inputValidation = InputHandler.validateUserReg(name, email, mobileNumber, password);

            if(!inputValidation.first)
            {
                Toast.makeText(this, inputValidation.second, Toast.LENGTH_SHORT).show();
            }

            else
            {
                loadingBar.setTitle("Create Account");
                loadingBar.setMessage("Please Wait while we create your account...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                HashMap<String,Object> userDataMap =new HashMap<>();
                userDataMap.put("Name", name);
                userDataMap.put("Email", email);
                userDataMap.put("Mobile Number", mobileNumber);
                userDataMap.put("DeliverPerson", deliveryCheck);

                createAccount(email, password, userDataMap);
            }
        }
    }

    private void createAccount(String email, String password, final HashMap<String, Object> userData)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "userCreation:Success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateDatabase(Objects.requireNonNull(user).getUid(), userData);

                            // Redirect to main intent and pass user obj
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        loadingBar.dismiss();

                        if(e instanceof FirebaseAuthInvalidUserException)
                        {
                            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                            if(errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE"))
                            {
                                Toast.makeText(RegisterActivity.this,"An account with this Email-ID already exists!", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
        });
    }

    private void updateDatabase(final String UID, final HashMap<String, Object> userData)
    {
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!(dataSnapshot.child("Users").child(UID).exists()))
                {
                    rootRef.child("Users").child(UID).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this,"Congratulations!, your Account has been created",Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);

                                finish();
                                startActivity(intent);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                else
                {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"An account with this Email-ID already exists!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(RegisterActivity.this,"Account creation cancelled",Toast.LENGTH_LONG).show();
            }
        });
    }
}
