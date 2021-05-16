package com.se.term;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText pw;
    Button login;
    TextView register;
    CheckBox isAutoLogin;
    AutoLoginProvider autoLoginProvider = new AutoLoginProvider();
    private FirebaseAuth mAuth;
    public FirebaseUser user = null;
    File loginFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{"android.permission.INTERNET"}, 0);
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{"Manifest.permission.READ_EXTERNAL_STORAGE"}, MODE_PRIVATE);
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{"Manifest.permission.WRITE_EXTERNAL_STORAGE"}, MODE_PRIVATE);

        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.pw);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        isAutoLogin = (CheckBox) findViewById(R.id.isAutoLogin);
        loginFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/logininfo", "login.dat");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("") && !pw.getText().toString().equals("")) {
                    loginUser(email.getText().toString(), pw.getText().toString());
                    if (user != null) {

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "이메일 혹은 비밀번호가 공백입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));//액티비티 이동
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            user.updateEmail(email);
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                            if (isAutoLogin.isChecked()) {
                                autoLoginProvider.AutoLoginWriter(email, password); //자동로그인 설정
                            } else {
                                autoLoginProvider.AutoLoginRemover();
                            }

                            Intent afterLoginIntent = new Intent(getApplicationContext(), AfterLoginTest.class);
                            startActivity(afterLoginIntent); //AfterLoginTest로 이동, 나중에 다른 액티비티로 바꿔주세요
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
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