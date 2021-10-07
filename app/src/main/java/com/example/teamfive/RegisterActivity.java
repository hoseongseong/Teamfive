package com.example.teamfive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);

        mBtnRegister = findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 처리 시작
                final String strEmail = mEtEmail.getText().toString().trim();
                final String strPwd = mEtPwd.getText().toString();
                if(TextUtils.isEmpty(strEmail)){
                    Toast.makeText(RegisterActivity.this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(strPwd)){
                    Toast.makeText(RegisterActivity.this, "암호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        UserAccount account = new UserAccount();
                                        account.setIdToken(firebaseUser.getUid());
                                        account.setEmail(firebaseUser.getEmail());
                                        account.setPassword(strPwd);

                                        //  setValue : database에 insert (삽입) 행위
                                        mDatabaseRef.child(firebaseUser.getUid()).setValue(account);

                                        Toast.makeText(RegisterActivity.this, "인증 이메일이 전송되었습니다.\n확인 후 로그인 해주세요!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else{
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {

                                case "ERROR_INVALID_EMAIL":
                                    mEtEmail.setError("이메일 형식에 맞게 다시 입력해주세요!");
                                    mEtEmail.requestFocus();
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    mEtEmail.setError("이미 사용중인 이메일입니다!");
                                    mEtEmail.requestFocus();
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                    mEtPwd.setError("비밀번호를 6자리 이상 설정해주세요!");
                                    mEtPwd.requestFocus();
                                    break;

                                default:
                                    Toast.makeText(RegisterActivity.this, "실패하였습니다. 다시 입력해주세요!\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }
                });
            }
        });
    }
}