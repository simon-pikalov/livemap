package com.example.livemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.livemap.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {
    private EditText mPhoneNumber;
    private EditText mCode;
    private Button mSend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseDatabase rootNode;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_in);
        userIsLoggedIn();
        mPhoneNumber = findViewById(R.id.phoneNumber);
        mCode = findViewById(R.id.code);
        mSend = findViewById(R.id.sendVerificationButton);
        rootNode = FirebaseDatabase.getInstance();
        mRef = rootNode.getReference("/root/users/");
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVerificationId != null) { // case User has already send an sms  and pushed the button with code -> verify him
                    verifyPhoneNumberWithCode();
                }
                else{
                    startPhoneNumberVerification(); //  // case User has not already send an sms -> send it
                }

            }
        });



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.i("firebase-error", "Phone Verification Failed !");
            }

            @Override
            public void onCodeSent(@NonNull String verification, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verification, forceResendingToken);
                mVerificationId = verification;
                mSend.setText("Verify Code");

            }
        };

    }

    private void verifyPhoneNumberWithCode() {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mCode.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        Log.i("firebase", "Phone Verification started");
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userIsLoggedIn();
                }
            }
        });
    }

//    void addUserToFirebase() {
//        //@TODO this is hard coded , replace with hash and more generic method
//        boolean isAdmin = (true);
//        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (fUser!=null){
//            String sUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // the user hash of the current user
//            User cUser = new User(sUid);
//            Log.w("user",cUser.toString());
//            rootNode = FirebaseDatabase.getInstance();
//            mRef = rootNode.getReference("/users/" +sUid);
//            mRef.setValue(cUser);
//        }
//    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        addUserToFirebase();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mPhoneNumber.getText().toString(), 60, TimeUnit.SECONDS, this, mCallbacks);
    }
}