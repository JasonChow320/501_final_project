package com.cs501.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Hash;
import com.cs501.project.Model.User;
import com.cs501.project.Model.User_settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    // Views for the Activity
    private EditText username, password_text, password_verify_text;
    private Button edit_account, back_button, delete_password;
    private CheckBox checkbox, checkbox_pw_enable;

    // To interact with FireBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private User user;

    FireBaseManager fb_manager;

    private final static String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // get firebase manager
        fb_manager = FireBaseManager.getInstance();

        this.user = fb_manager.getUser();

        // Initialize Views
        username = (EditText) findViewById(R.id.editTextUsernameEditProfile);
        password_text = (EditText) findViewById(R.id.editTextPasswordEditProfile);
        password_verify_text = (EditText) findViewById(R.id.editTextPasswordVerifyEditProfile);
        edit_account = (Button) findViewById(R.id.edit_confirm_button);
        back_button = (Button) findViewById(R.id.edit_back_button);
        delete_password = (Button) findViewById(R.id.edit_delete_password_button);
        checkbox = (CheckBox) findViewById(R.id.edit_profile_checkBox);
        checkbox_pw_enable = (CheckBox) findViewById(R.id.enable_password_edit_checkbox);

        password_text.setEnabled(false);
        password_text.setInputType(InputType.TYPE_NULL);
        password_verify_text.setEnabled(false);
        password_verify_text.setInputType(InputType.TYPE_NULL);

        this.resetFields();

        // Set Up Button OnClicks
        edit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // currently we're only adding username
                // TODO allow password
                edit_account(username.getText().toString());
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

        delete_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getPasswordProtected()){

                    // define confirm delete dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                    builder.setCancelable(true);
                    builder.setTitle(getResources().getString(R.string.confirm_delete_pass));
                    builder.setMessage(getResources().getString(R.string.confirm_delete_pass_q));
                    builder.setPositiveButton(getResources().getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // delete password
                                    user.setPasswordProtected(false);
                                    Toast.makeText(EditProfile.this, getResources().getString(R.string.password_del),
                                            Toast.LENGTH_SHORT).show();
                                    fb_manager.update();
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog confirm_dialog = builder.create();
                    confirm_dialog.show();
                } else {
                    Toast.makeText(EditProfile.this, getResources().getString(R.string.no_pass),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        User_settings uSettings = fb_manager.getUser().getUserSettings();
        ConstraintLayout con = findViewById(R.id.background);
        String backgroundColor = getResources().getStringArray(R.array.themesValues)[uSettings.getTheme()];
        System.out.println(backgroundColor);
        con.setBackgroundColor(android.graphics.Color.parseColor(backgroundColor));
    }

    private void resetFields(){

        this.username.setText(this.user.getUsername());
        this.password_text.setText("");
        this.password_verify_text.setText("");
    }

    // private method to make account
    private void edit_account(String username){

        if(username == null || username.length() <= 0){

            // ERROR
            Toast.makeText(EditProfile.this, getResources().getString(R.string.fail_username),
                    Toast.LENGTH_SHORT).show();
            this.resetFields();
            return;
        }

        User user = fb_manager.getUser();
        user.setUsername(username);

        if(checkbox_pw_enable.isChecked()){

            if(!verifyPassword()){
                Toast.makeText(EditProfile.this, getResources().getString(R.string.fail_edit_profile),
                        Toast.LENGTH_SHORT).show();
                this.resetFields();
                return;
            }

            user.setPasswordProtected(true);
            user.setPassword(user.getUserId() + Hash.sha256(password_text.getText().toString()));
        }

        fb_manager.update();

        Toast.makeText(EditProfile.this, getResources().getString(R.string.edit_profile_success),
                Toast.LENGTH_SHORT).show();
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
