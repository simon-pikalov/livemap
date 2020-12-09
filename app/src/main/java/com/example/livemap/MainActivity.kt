package com.example.livemap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


public lateinit var username: String
public lateinit var signInClient: GoogleSignInClient;

public lateinit var firebaseAuth: FirebaseAuth
private lateinit var firebaseUser: FirebaseUser

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }



}