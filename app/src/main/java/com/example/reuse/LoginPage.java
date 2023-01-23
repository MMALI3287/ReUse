package com.example.reuse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class LoginPage extends AppCompatActivity {

    EditText email,password;
    ImageView login;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

         auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(txt_email.isEmpty() || txt_password.isEmpty()){
                    Toast.makeText(LoginPage.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                }
                else{
                    loginUser(txt_email,txt_password);
                }
            }
        });
    }
    private void loginUser(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginPage.this, "Succesful Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginPage.this, HomePage.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginPage.this, "Failed Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}




















//    ImageView btnGoogle;
//    ImageView btnFacebook;
//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;

//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if(account != null){
//            Intent intent = new Intent(LoginPage.this, HomePage.class);
//            startActivity(intent);
//        }
//        btnGoogle = findViewById(R.id.loginGoogle);
//
//        btnGoogle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent googleSignInIntent=gsc.getSignInIntent();
//                startActivityForResult(googleSignInIntent, 1);
//            }
//        });
//
//        btnFacebook = findViewById(R.id.loginFB);
//
//        btnFacebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent facebookSignInIntent = new Intent(LoginPage.this, FBLogin.class);
//                startActivity(facebookSignInIntent);
//                finish();
//            }
//        });



//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==1){
//            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                task.getResult(ApiException.class);
//                finish();
//                Intent intent = new Intent(LoginPage.this, HomePage.class);
//                startActivity(intent);
//            } catch (ApiException e) {
//                Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
