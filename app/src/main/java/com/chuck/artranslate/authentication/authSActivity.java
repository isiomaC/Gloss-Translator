package com.chuck.artranslate.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuck.artranslate.R;
import com.chuck.artranslate.activities.MainActivity;
import com.chuck.artranslate.utils.ViewsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class authSActivity extends AppCompatActivity {
    public  FirebaseAuth mAuth;

    private EditText inputEmail, inputPassword;
    private ProgressBar progressbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_signin);

        final AlertDialog mProgressDialog = ViewsUtil.progressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        Button btnLogIn = findViewById(R.id.btn_login);
        Button btnSignUp = findViewById(R.id.sign_up_button);
        TextView resetPassword = findViewById(R.id.btn_reset_password);

        TextView textView = findViewById(R.id.header);
        textView.setText(R.string.account);

        progressbar = findViewById(R.id.progressBar);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);

        textView = findViewById(R.id.info);
        textView.setText(R.string.account_msg);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(authSActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.show();

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(authSActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                mProgressDialog.dismiss();

                                if (!task.isSuccessful()) {

                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(authSActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    MainActivity.start(authSActivity.this);
                                    finish();
                                }
                            }
                        });
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.show();

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mProgressDialog.dismiss();
                    ViewsUtil.makeGone(progressbar, getApplicationContext(), R.anim.fade_out);
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mProgressDialog.dismiss();
                    ViewsUtil.makeGone(progressbar, getApplicationContext(), R.anim.fade_out);
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(authSActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                mProgressDialog.dismiss();
//                                ViewsUtil.makeGone(progressbar, getApplicationContext(), R.anim.fade_out);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(authSActivity.this, "Please register with a valid Email" ,
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    MainActivity.start(authSActivity.this);
                                    finish();
                                }
                            }
                        });

            }
        });

    }


    public static void start(Context context){
        Intent intent = new Intent(context, authSActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

}
