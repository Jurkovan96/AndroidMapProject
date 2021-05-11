package com.example.rent.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.rent.ConstantsUtils;
import com.example.rent.FireHelper;
import com.example.rent.R;
import com.example.rent.databinding.ActivityLoginBinding;
import com.example.rent.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    static boolean sIsNewUser = false;

    private FirebaseAuth mFirebaseAuth;

    ActivityLoginBinding mActivityLoginBinding;

    public static final String FAILED_SIGN_IN = "Email or password are incorrect!";

    public static final String SUCCESSFULLY_SIGN_IN = "Signed in successfully!";

    public static final String EMAIL_SENT = "Email was sent";

    public static final String EMAIL_NOT_SENT = "Email was not sent";

    public static final String EMAIL_IS_EMPTY_WARNING = "Email field cannot be empty!";

    public static final String PASSWORD_IS_EMPTY_WARNING = "Password field cannot be empty!";

    private UserViewModel usersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mFirebaseAuth = FireHelper.getInstanceOfAuth();
        usersViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        checkUserIsLogged();
        ClickHandler handler = new ClickHandler();
        mActivityLoginBinding.setClickHandler(handler);
        mActivityLoginBinding.textForgotPassword.setOnClickListener(this);
    }

    public void checkUserIsLogged() {
        FirebaseUser user = FireHelper.getInstanceOfAuth().getCurrentUser();
        if (user != null && !sIsNewUser) {
            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
        }
    }


    public class ClickHandler {

        public ClickHandler() {
        }

        public void loginClickButton(View view) {
            final String email = mActivityLoginBinding.emailEditTextLoginPage.getText().toString().trim();
            String password = mActivityLoginBinding.passwordEditTextLoginPage.getText().toString();
            if (email.equals("") || email.length() == 0) {
                Toast.makeText(LoginActivity.this, EMAIL_IS_EMPTY_WARNING, Toast.LENGTH_SHORT).show();
            } else {
                if (password.equals("") || password.length() == 0) {
                    Toast.makeText(LoginActivity.this, PASSWORD_IS_EMPTY_WARNING, Toast.LENGTH_SHORT).show();
                } else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                        Toast.makeText(LoginActivity.this, R.string.wrong_email_address,
                                                Toast.LENGTH_SHORT).show();
                                    } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                        Toast.makeText(LoginActivity.this, R.string.wrong_password,
                                                Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(LoginActivity.this, FAILED_SIGN_IN,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, SUCCESSFULLY_SIGN_IN,
                                            Toast.LENGTH_SHORT).show();
                                    usersViewModel = new ViewModelProvider(LoginActivity.this)
                                            .get(UserViewModel.class);
                                    usersViewModel.loadUserAccountInfo(Objects.requireNonNull(
                                            mFirebaseAuth.getCurrentUser()).getUid())
                                            .observe(LoginActivity.this, user ->
                                            {
                                                if (!user.getPassword().equals(password)) {
                                                    user.setPassword(password);
                                                    usersViewModel.updateUserInfo(user);
                                                }
                                            });
                                    if (mFirebaseAuth != null && mFirebaseAuth.getCurrentUser() != null) {
                                        usersViewModel.loadCurrentUserAccountInfo(mFirebaseAuth
                                                .getCurrentUser().getUid()).observe(LoginActivity.this,
                                                user -> ConstantsUtils.sUserGlobalModel = user);
                                    }

                                    startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                                }
                            });

                }
            }
        }

        public void registerClickButton(View view) {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }

    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {

        final EditText typeEmailEditText = new EditText(LoginActivity.this);
        typeEmailEditText.setHint(R.string.enter_email);
        typeEmailEditText.setBackground(getResources().getDrawable(R.drawable.round_border));
        typeEmailEditText.setGravity(Gravity.CENTER);
        AlertDialog.Builder forgotPasswordDialog = new AlertDialog.Builder(this);
        forgotPasswordDialog.setTitle(R.string.password_recovery)
                .setMessage(R.string.insert_email_address)
                .setView(typeEmailEditText)
                .setCancelable(false);
        forgotPasswordDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        forgotPasswordDialog.setPositiveButton(R.string.send_email, (dialogInterface, i) -> {
        });
        AlertDialog updatePasswordAlertDialog = forgotPasswordDialog.create();
        updatePasswordAlertDialog.show();
        updatePasswordAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String editTextValue = typeEmailEditText.getText().toString().trim();
            if (!editTextValue.equals("")) {
                //the email is sent and the dialog is dismissed only when the field is not empty
                mFirebaseAuth.sendPasswordResetEmail(editTextValue)
                        .addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, EMAIL_SENT,
                                Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, EMAIL_NOT_SENT,
                                Toast.LENGTH_SHORT).show());
                updatePasswordAlertDialog.dismiss();
            } else {
                Toast.makeText(this, R.string.insert_email_address, Toast.LENGTH_SHORT).show();
            }
        });


    }
}