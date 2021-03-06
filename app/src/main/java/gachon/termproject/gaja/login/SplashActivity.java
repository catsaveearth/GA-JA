package gachon.termproject.gaja.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;

import gachon.termproject.gaja.MainActivity;
import gachon.termproject.gaja.R;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Intent loginIntent;
    AutoLoginProvider autoLoginProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_autologin);
        loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        autoLoginProvider = new AutoLoginProvider();
        mAuth = FirebaseAuth.getInstance();

        if (autoLoginProvider.AutoLoginChecker()) {
            String[] emailPW = autoLoginProvider.AutoLoginReader();
            loginUser(emailPW[0], emailPW[1]);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(loginIntent);
                    finish();
                }
            }, 500); //500밀리초
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent afterLoginIntent = new Intent(getApplicationContext(), LoginBridge.class);
                            startActivity(afterLoginIntent); //AfterLoginTest로 이동, 나중에 다른 액티비티로 바꿔주세요
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                            startActivity(loginIntent);
                            finish();
                        }
                    }
                });
    }


}
