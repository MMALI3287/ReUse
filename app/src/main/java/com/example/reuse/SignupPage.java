package com.example.reuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupPage extends AppCompatActivity {

    EditText username, email, phone, password;
    ImageView signUp;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        databaseRef = FirebaseDatabase
                .getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);

        signUp = findViewById(R.id.signup);

        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_phone = phone.getText().toString();
                String txt_password = password.getText().toString();

                if (txt_username.isEmpty() || txt_email.isEmpty() || txt_phone.isEmpty() || txt_password.isEmpty()) {
                    Toast.makeText(SignupPage.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                } else if (txt_username.length() < 5) {
                    Toast.makeText(SignupPage.this, "Username too short", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 8) {
                    Toast.makeText(SignupPage.this, "Password too short", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txt_username, txt_email, txt_phone, txt_password);
                }
            }
        });
    }

    private void registerUser(String txt_username, String txt_email, String txt_phone, String txt_password) {
        Users user = new Users(txt_username);

        auth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupPage.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                            auth.signInWithEmailAndPassword(txt_email, txt_password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignupPage.this, "Succesful Login", Toast.LENGTH_SHORT)
                                                        .show();
                                                databaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .setValue(user);
                                                Intent intent = new Intent(SignupPage.this, HomePage.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignupPage.this, "Something went wrong D",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupPage.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}