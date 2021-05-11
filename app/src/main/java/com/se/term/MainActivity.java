package com.se.term;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText pw;
    TextView textview;
    Button login;
    Button register;
    Button writeLogin;
    Button readLogin;
    AutoLoginProvider autoLoginProvider = new AutoLoginProvider();
    private FirebaseAuth mAuth;
    public FirebaseUser user = null;
    File loginFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{"android.permission.INTERNET"},0);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{"Manifest.permission.READ_EXTERNAL_STORAGE"}, MODE_PRIVATE);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{"Manifest.permission.WRITE_EXTERNAL_STORAGE"},MODE_PRIVATE);

        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.pw);
        textview = (TextView) findViewById(R.id.userview);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        writeLogin = (Button) findViewById(R.id.write);
        readLogin = (Button) findViewById(R.id.read);
        mAuth = FirebaseAuth.getInstance();
        loginFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/logininfo", "login.dat");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().equals("")&&!pw.getText().toString().equals("")){
                    loginUser(email.getText().toString(), pw.getText().toString());
                    if(user!=null){
                        textview.setText(user.getEmail().toString() + "로 로그인 됨.");
                    }
                } else{
                    Toast.makeText(getApplicationContext(),"이메일 혹은 비밀번호가 공백입니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().equals("")&&!pw.getText().toString().equals("")){
                    registerUser(email.getText().toString(), pw.getText().toString());
                } else{
                    Toast.makeText(getApplicationContext(),"이메일 혹은 비밀번호가 공백입니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        writeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoLoginProvider.AutoLoginWriter(email.getText().toString(), pw.getText().toString());
                Toast.makeText(getApplicationContext(),loginFile.getPath(),Toast.LENGTH_SHORT).show();
            }
        });

        readLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp[] = autoLoginProvider.AutoLoginReader();
                email.setText(temp[0]);
                pw.setText(temp[1]);
            }
        });
    }

    private void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            user.updateEmail(email);
                            Toast.makeText(getApplicationContext(),"회원가입 성공, 다시 로그인 해주세요.",Toast.LENGTH_SHORT).show();
                            //액티비티 이동
                        } else {
                            Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            user.updateEmail(email);
                            Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                            //액티비티 이동
                        } else {
                            Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }
}