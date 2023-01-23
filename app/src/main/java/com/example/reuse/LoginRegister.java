package com.example.reuse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginRegister extends AppCompatActivity {

    ImageView login;
    ImageView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        login = findViewById(R.id.imageView6);
        register=findViewById(R.id.imageView7);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if(account != null){
//            Intent intent = new Intent(LoginRegister.this, HomePage.class);
//            startActivity(intent);
//        }
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginRegister.this, LoginPage.class);
                startActivity(intent);
            }

        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginRegister.this,SignupPage.class);
                startActivity(intent);
            }
        });
    }


    /*
    Button signOut;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView name, email;
    name = getView().findViewById(R.id.userName);
    email = getView().findViewById(R.id.UserEmail);
    gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
                .build();
    gsc = GoogleSignIn.getClient(getActivity(), gso);
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account != null) {
        name.setText("Hello " + account.getDisplayName());
        email.setText("mail: " + account.getEmail());
    }*/
}