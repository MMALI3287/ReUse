package com.example.reuse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupPage extends AppCompatActivity {

    EditText username,email,password;
    ImageView signUp,signupGoogle;
    TextView loginTextButton;

    GoogleSignInOptions options;
    GoogleSignInClient client;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        databaseRef = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupGoogle = findViewById(R.id.signupGoogle);
        loginTextButton = findViewById(R.id.loginTextButton);

        signUp = findViewById(R.id.signup);

        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(txt_username.isEmpty() || txt_email.isEmpty()  || txt_password.isEmpty()){
                    Toast.makeText(SignupPage.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                }
                else if(!isAlpha(txt_username)){
                    Toast.makeText(SignupPage.this, "Enter only letters in username", Toast.LENGTH_SHORT).show();
                }
                else if(txt_username.length()<5){
                    Toast.makeText(SignupPage.this, "Username too short", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length()<8) {
                    Toast.makeText(SignupPage.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(txt_username,txt_email,txt_password);
                }
            }
        });
        loginTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);

        signupGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleSignInIntent=client.getSignInIntent();
                startActivityForResult(googleSignInIntent, 1);
            }
        });
    }

    private void registerUser(String txt_username, String txt_email, String txt_password) {
        Users user = new Users(txt_username);

        auth.createUserWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignupPage.this, "Successful Registration\nPlease Login", Toast.LENGTH_SHORT).show();
                    databaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    Intent intent = new Intent(SignupPage.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                    FirebaseAuth.getInstance().signOut();
                }
                else{

                    Toast.makeText(SignupPage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(SignupPage.this, HomePage.class);
                                    startActivity(intent);
                                    finish();
                                    databaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(task.isSuccessful()){
                                                if(!task.getResult().exists()){
                                                    databaseRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new Users(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(SignupPage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c) && c!=' ') {
                return false;
            }
        }

        return true;
    }

}