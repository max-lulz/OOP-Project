package com.example.mrdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createaccount;
    private EditText InputName,InputEmail,InputPassword,InputMobileNumber,InputConfirmPassword;
    private Switch deliveryperson;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createaccount=(Button) findViewById(R.id.register_btn);
        InputName=(EditText) findViewById(R.id.register_name_input);
        InputEmail=(EditText)findViewById(R.id.register_email_input);
        InputMobileNumber=(EditText)findViewById(R.id.register_mobilenumber_input);
        InputConfirmPassword=(EditText)findViewById(R.id.register_confirmpassword_input);
        InputPassword=(EditText)findViewById(R.id.register_password_input);
        loadingbar=new ProgressDialog(this);
        deliveryperson=(Switch) findViewById(R.id.delivery_rad);




        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateAccount();
            }
        });
    }
        private void CreateAccount()
        {
            int count=0;
            String name = InputName.getText().toString();
            String email = InputEmail.getText().toString();
            String password = InputPassword.getText().toString();
            String confirmPassword = InputConfirmPassword.getText().toString();
            String mobileNumber = InputMobileNumber.getText().toString();
            boolean deliveryCheck = deliveryperson.isChecked();

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
                loadingbar.setTitle("Create Account");
                loadingbar.setMessage("Please Wait while we create your account...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                ValidateUser(name, email, password, mobileNumber, deliveryCheck);
            }
        }

    private void ValidateUser(final String name, final String email, final String password, final String mobileNumber, final boolean deliveryCheck)
    {
        final DatabaseReference rootref;
        rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!(dataSnapshot.child("Users").child(email).exists()))
                {
                    HashMap<String,Object> userdatamap =new HashMap<>();
                    userdatamap.put("Name",name);
                    userdatamap.put("Username",email);
                    userdatamap.put("Mobile Number",mobileNumber);
                    userdatamap.put("Password",password);
                    userdatamap.put("DeliverPerson", deliveryCheck);
                    rootref.child("Users").child(email).updateChildren(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this,"Congratulations Your Account Has Been Created",Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                                {
                                    Toast.makeText(RegisterActivity.this,"An Error Occurred Please Retry",Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();

                                }
                        }
                    });
                }
                else
                {

                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this,"An Account Already Exists With This Username! Try with a different username",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
