package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "DEBUGBOI";

    private EditText inputName, inputEmail, inputPassword, inputMobileNumber, inputConfirmPassword;
    private RadioButton deliveryPerson;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                CreateAccount();
            }
        });
    }
    private void CreateAccount()
    {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();
        String mobileNumber = inputMobileNumber.getText().toString();
        boolean deliveryCheck = deliveryPerson.isChecked();

        // ADD REGEX CHECKS FOR NAME, EMAIL AND MOBILE NUMBER

        boolean fieldsNotFilled = (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(mobileNumber) ||
                TextUtils.isEmpty(confirmPassword));

        if(fieldsNotFilled)
        {
            Toast.makeText(this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this,"Passwords don't match",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please Wait while we create your account...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidateUser(name, email, password, mobileNumber, deliveryCheck);
        }
    }

    private void ValidateUser(final String name, final String email, final String password, final String mobileNumber, final boolean deliveryCheck)
    {
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String userKey = email.split("\\.")[0] + "," + email.split("\\.")[1];
                if(!(dataSnapshot.child("Users").child(userKey).exists()))
                {
                    HashMap<String,Object> userDataMap =new HashMap<>();
                    userDataMap.put("Name", name);
                    userDataMap.put("Email", email);
                    userDataMap.put("Mobile Number", mobileNumber);
                    userDataMap.put("Password", password);
                    userDataMap.put("DeliverPerson", deliveryCheck);

                    rootRef.child("Users").child(userKey).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this,"Congratulations Your Account Has Been Created",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this,"An Error Occurred Please Retry",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                        }
                    });
                }
                else
                {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"An Account Already Exists With This Username! Try with a different username",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
