package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Hash;
import com.cs501.project.Model.RandomString;
import com.cs501.project.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MakeProfile extends AppCompatActivity {

    // Views for the Activity
    private EditText username, password_text, password_verify_text;
    private Button make_account, back_button;
    private CheckBox checkbox, checkbox_pw_enable;
    private ProgressBar progressBar_make_account;

    // To interact with FireBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    FireBaseManager fb_manager;

    private final static String TAG = "MakeProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // get firebase manager
        fb_manager = FireBaseManager.getInstance();

        // Initialize Views
        username = (EditText) findViewById(R.id.editTextUsernameMakeAccount);
        password_text = (EditText) findViewById(R.id.editTextPasswordMakeAccount);
        password_verify_text = (EditText) findViewById(R.id.editTextPasswordVerifyMakeAccount);
        make_account = (Button) findViewById(R.id.make_user_button);
        back_button = (Button) findViewById(R.id.make_account_back_button);
        checkbox = (CheckBox) findViewById(R.id.make_account_checkBox);
        checkbox_pw_enable = (CheckBox) findViewById(R.id.enable_password_checkbox);
        progressBar_make_account = (ProgressBar) findViewById(R.id.progressBar_make_account);

        // Hide Progress Bar
        progressBar_make_account.setVisibility(View.GONE);

        password_text.setEnabled(false);
        password_text.setInputType(InputType.TYPE_NULL);
        password_verify_text.setEnabled(false);
        password_verify_text.setInputType(InputType.TYPE_NULL);

        // Set Up Button OnClicks
        make_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // make account
                progressBar_make_account.setVisibility(View.VISIBLE);

                // currently we're only adding username
                // TODO allow password
                make_account(username.getText().toString());
            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkbox.isChecked()){
                    password_text.setTransformationMethod(null);
                    password_verify_text.setTransformationMethod(null);
                }else{
                    password_text.setTransformationMethod(new PasswordTransformationMethod());
                    password_verify_text.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        checkbox_pw_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkbox_pw_enable.isChecked()){
                    password_text.setEnabled(true);
                    password_text.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password_verify_text.setEnabled(true);
                    password_verify_text.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    if(checkbox.isChecked()){
                        password_text.setTransformationMethod(null);
                        password_verify_text.setTransformationMethod(null);
                    }else{
                        password_text.setTransformationMethod(new PasswordTransformationMethod());
                        password_verify_text.setTransformationMethod(new PasswordTransformationMethod());
                    }
                }else{
                    password_text.setEnabled(false);
                    password_text.setInputType(InputType.TYPE_NULL);
                    password_verify_text.setEnabled(false);
                    password_verify_text.setInputType(InputType.TYPE_NULL);
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFields();
                finish();
            }
        });
    }

    private void resetFields(){
        this.username.setText("");
        this.password_text.setText("");
        this.password_verify_text.setText("");
    }

    // private method to make account
    private void make_account(String username){

        if(username == null || username.length() <= 0){

            // ERROR
            Toast.makeText(MakeProfile.this, "Unable to parse username fields. Please try again",
                    Toast.LENGTH_SHORT).show();
            this.resetFields();
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setUserId(RandomString.getAlphaNumericString(16));

        if(checkbox_pw_enable.isChecked()){

            if(!verifyPassword()){
                Toast.makeText(MakeProfile.this, "Unable to make an user profile, password does not match!",
                        Toast.LENGTH_SHORT).show();
                this.resetFields();
                progressBar_make_account.setVisibility(View.GONE);
                return;
            }

            user.setPasswordProtected(true);
            user.setPassword(Hash.md5(password_text.getText().toString()));
        }

        fb_manager.addUser(user);
        this.resetFields();

        Toast.makeText(MakeProfile.this, "Successfully made an user profile!",
                Toast.LENGTH_SHORT).show();
        progressBar_make_account.setVisibility(View.GONE);
        finish();
    }

    private boolean verifyPassword(){
        String password = password_text.getText().toString(), password_verify = password_verify_text.getText().toString();

        if(password_text.length() <= 0 || !password.equals(password_verify)){
            return false;
        }

        return true;
    }
}
