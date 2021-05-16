package com.se.term;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {

    EditText email;
    EditText pw;
    TextView login;
    Button register;
    private FirebaseAuth mAuth;
    public FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        ActivityCompat.requestPermissions(RegisterActivity.this,
                new String[]{"android.permission.INTERNET"}, 0);
        ActivityCompat.requestPermissions(RegisterActivity.this,
                new String[]{"Manifest.permission.READ_EXTERNAL_STORAGE"}, MODE_PRIVATE);
        ActivityCompat.requestPermissions(RegisterActivity.this,
                new String[]{"Manifest.permission.WRITE_EXTERNAL_STORAGE"}, MODE_PRIVATE);

        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.pw);
        login = (TextView) findViewById(R.id.gotologin);
        register = (Button) findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("") && !pw.getText().toString().equals("")) {
                    registerUser(email.getText().toString(), pw.getText().toString());
                    finish(); //로그인창으로 돌아가기
                } else {
                    Toast.makeText(getApplicationContext(), "이메일 혹은 비밀번호가 공백입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //로그인창으로 돌아가기
            }
        });
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            user.updateEmail(email);
                            Toast.makeText(getApplicationContext(), "회원가입 성공, 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                            //액티비티 이동
                        } else {
                            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //reload();
        }
    }
}