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

    private EditText inputUserName, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUserName = findViewById(R.id.login_email_input);
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
        final String userName = inputUserName.getText().toString();
        final String password = inputPassword.getText().toString();

        boolean fieldsNotFilled = (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password));

        if(fieldsNotFilled)
        {
            Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("Users").child(userName).exists())
                    {
                        if(Objects.requireNonNull(dataSnapshot.child("Users").child(userName).child("Password").getValue()).toString().equals(password))
                        {
                            Toast.makeText(LoginActivity.this,"Login Successful!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Incorrect Password",Toast.LENGTH_SHORT).show();
                        }
                        Log.d("pass", Objects.requireNonNull(dataSnapshot.child("Users").child(userName).child("Password").getValue().toString()));
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"No user with given User Name found",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}
