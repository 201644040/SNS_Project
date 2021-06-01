package com.inhatc.sns_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inhatc.sns_project.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    login();
                    break;
                case R.id.gotoPasswordResetButton:
                    myStartActivity(PasswordResetActivity.class);
                    break;
            }
        }
    };
    private void login() {
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();

        if(email.length() > 0 && password.length() > 0){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인에 성공하였습니다.");
                                myStartActivity(MainActivity.class);
                            } else {
                                if(task.getException().toString() != null){
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });
        } else{
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }

    }

    private void startToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
