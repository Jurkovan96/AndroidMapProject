package com.example.rent.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.rent.ConstantsUtils;
import com.example.rent.FireHelper;
import com.example.rent.R;
import com.example.rent.databinding.ActivityRegisterBinding;
import com.example.rent.model.User;
import com.example.rent.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.rent.ConstantsUtils.INVALID_PASSWORD;
import static com.example.rent.ConstantsUtils.IS_NEW_USER;
import static com.example.rent.ConstantsUtils.SPECIAL_CHAR_EXCEPTION;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RegisterActivity.class.getName();

    private List<String> mUserNameList;

    private ActivityRegisterBinding mRegisterBinding;

    private UserViewModel userViewModel;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mUserNameList = new ArrayList<>();
        mFirebaseAuth = FireHelper.getInstanceOfAuth();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, users -> {
            for (User user : users) {
                mUserNameList.add(user.getName());
            }
        });
        mRegisterBinding.userRegister.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        String userName = mRegisterBinding.userName.getText().toString().trim();
        String userEmail = mRegisterBinding.userEmailAddress.getText().toString().trim();
        String password = mRegisterBinding.userPassword.getText().toString().trim();
        String repeatPassword = mRegisterBinding.userRepeatPassword.getText().toString().trim();
        if (userEmail.equals("") || userName.equals("")) {
            Toast.makeText(this, LoginActivity.EMAIL_IS_EMPTY_WARNING, Toast.LENGTH_SHORT).show();
        } else if (userName.contains("+") || userName.contains("$") || userName.contains("@")
                || userName.contains("&") || userName.contains("%")) {
            Toast.makeText(this, SPECIAL_CHAR_EXCEPTION, Toast.LENGTH_SHORT).show();
        } else if (ConstantsUtils.checkForDuplicateUserName(userName, mUserNameList)) {
            Toast.makeText(this, R.string.duplicated_value, Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty() || !password.equals(repeatPassword)) {
            Toast.makeText(this, INVALID_PASSWORD, Toast.LENGTH_SHORT).show();
        } else {
            createUser(userEmail, userName, password);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createUser(final String email, final String name, final String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
                        User user = new User(uid, name, email, password);
                        userViewModel.createUser(user);

                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Something went wrong" + e));
        Intent goToLoginPageIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        goToLoginPageIntent.putExtra(IS_NEW_USER, true);
        startActivity(goToLoginPageIntent);
    }
}