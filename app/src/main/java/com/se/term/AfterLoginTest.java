package com.se.term;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class AfterLoginTest extends AppCompatActivity {

    TextView test;
    public FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogintest);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        test = (TextView) findViewById(R.id.test1);

        test.setText(user.getEmail());
    }
}