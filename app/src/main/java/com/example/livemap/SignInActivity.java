package com.example.livemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {
    private EditText mPhoneNumber;
    private EditText mCode;
    private Button mSend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_in);
        userIsLoggedIn();
        mPhoneNumber = findViewById(R.id.phoneNumber);
        mCode = findViewById(R.id.code);
        mSend = findViewById(R.id.sendVerificationButton);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPhoneNumberVerification();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.i("firebase-error","Phone Verification Failed !");
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        Log.i("firebase","Phone Verification started");
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userIsLoggedIn();
                }
            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mPhoneNumber.getText().toString(), 60, TimeUnit.SECONDS, this, mCallbacks);
    }
}