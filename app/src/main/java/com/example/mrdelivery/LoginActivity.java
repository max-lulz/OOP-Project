package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.login_email_input);
        inputPassword = findViewById(R.id.login_password_input);
        Button login = findViewById(R.id.login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
    }

    private void authenticateUser(){
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        // ADD REGEX CHECKS FOR EMAIL AND HORIZONTAL LAYOUTS

        final String userEmail = email.split("\\.")[0] + "," + email.split("\\.")[1];

        boolean fieldsNotFilled = (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(password));

        if(fieldsNotFilled)
        {
            Toast.makeText(this,"Please fill all Fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("Users").child(userEmail).exists())
                    {
                        if(Objects.requireNonNull(dataSnapshot.child("Users").child(userEmail).child("Password").getValue()).toString().equals(password))
                        {
                            Toast.makeText(LoginActivity.this,"Login Successful!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Incorrect Password",Toast.LENGTH_SHORT).show();
                        }
                        Log.d("pass", Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("Users").child(userEmail).child("Password").getValue()).toString()));
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"This Email ID is not registered with our App, please create a new account.",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}
