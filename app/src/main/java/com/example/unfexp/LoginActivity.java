package com.example.unfexp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unfexp.General.General;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView SignUpLink;
    private ProgressDialog LoadingBar;

    private FirebaseAuth mAuth;

    private CheckBox UserCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        SignUpLink = (TextView) findViewById(R.id.register_account_link);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_button);
        LoadingBar = new ProgressDialog(this);

        UserCheckBox = findViewById(R.id.remember_me_checkbox);
        Paper.init(this);

        SignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermitUserToLogin();
            }
        });

        String UserEmailKey = Paper.book().read(General.UserEmailKey);
        String UserPasswordKey = Paper.book().read(General.UserPasswordKey);
        if (UserEmailKey != "" && UserPasswordKey != "") {
            if (!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                UserEmail.setText(UserEmailKey);
                UserPassword.setText(UserPasswordKey);
                UserCheckBox.setChecked(true);
            }
        }
    }

    private void PermitUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (UserCheckBox.isChecked()) {
            Paper.book().write(General.UserEmailKey, email);
            Paper.book().write(General.UserPasswordKey, password);
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();
        } else {
            LoadingBar.setTitle("Login");
            LoadingBar.setMessage("Please wait, Login is in process...");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "You are now logged in successfully.", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Credentials are wrong. Please try again.", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }


}
