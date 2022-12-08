package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cs501.project.Model.Profile;
import com.cs501.project.Model.RandomString;
import com.cs501.project.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    // Views for the Activity
    private EditText email_text, password_text, password_verify_text;
    private Button sign_up, back_button;
    private CheckBox checkbox;
    private ProgressBar progressBar_signup;

    // To interact with FireBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private final static String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        email_text = (EditText) findViewById(R.id.editTextEmailSignUp);
        password_text = (EditText) findViewById(R.id.editTextPasswordSignUp);
        password_verify_text = (EditText) findViewById(R.id.editTextPasswordVerifySignUp);
        sign_up = (Button) findViewById(R.id.sign_up_button);
        back_button = (Button) findViewById(R.id.signup_back_button);
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        progressBar_signup = (ProgressBar) findViewById(R.id.progressBar_signup);

        // Hide Progress Bar
        progressBar_signup.setVisibility(View.GONE);

        // Set Up Button OnClicks
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup(email_text.getText().toString(), password_text.getText().toString()
                        ,password_verify_text.getText().toString());
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

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFields();
                finish();
            }
        });
    }

    // Private method for signing out an account with Firebase
    private void signup(String email, String password, String password_verify){

        // Check inputs
        if(email == null || password == null){
            Toast.makeText(SignUp.this, getResources().getString(R.string.parse_text_fields),
                    Toast.LENGTH_SHORT).show();
            this.resetFields();
            return;
        }

        if(password.length() <= 5){
            Toast.makeText(SignUp.this, getResources().getString(R.string.pass_length),
                    Toast.LENGTH_SHORT).show();
            this.password_text.setText("");
            this.password_verify_text.setText("");
            return;
        }

        if(!password.equals(password_verify)){
            Toast.makeText(SignUp.this, getResources().getString(R.string.pass_dont_match),
                    Toast.LENGTH_SHORT).show();
            this.password_text.setText("");
            this.password_verify_text.setText("");
            return;
        }

        if(email.length() <= 0){
            Toast.makeText(SignUp.this, getResources().getString(R.string.valid_email),
                    Toast.LENGTH_SHORT).show();
            this.email_text.setText("");
            return;
        }

        // sign up with Firebase
        Log.d(TAG, "Creating authentication account");
        progressBar_signup.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUp.this, getResources().getString(R.string.sign_up_success),
                                    Toast.LENGTH_SHORT).show();
                            resetFields();

                            FirebaseUser user = mAuth.getCurrentUser();
                            addAccount(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressBar_signup.setVisibility(View.GONE);
                            Toast.makeText(SignUp.this, getResources().getString(R.string.auth_failed),
                                    Toast.LENGTH_LONG).show();
                            resetFields();
                        }
                    }
                });
    }

    private void resetFields(){
        this.email_text.setText("");
        this.password_text.setText("");
        this.password_verify_text.setText("");
    }

    // add account to our database for storage
    private void addAccount(FirebaseUser user) {

        Log.d(TAG, "Calling addAccount");
        if(user == null){

            // error, this is bad!?
            return;
        }

        // get database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("accounts");

        Profile profile = new Profile();
        profile.setAccountEmail(user.getEmail());
        profile.setUserId(user.getUid());

        // TODO allow users to create User class themselves
        User new_user = new User();
        new_user.setUserId(RandomString.getAlphaNumericString(16));
        profile.addUser(new_user);

        /* It's fine if we don't have any clothes to start - I think...
        Clothes_Factory factory = new Clothes_Factory();
        Wardrobe wardrobe = new_user.getWardrobe();
        wardrobe.insertClothes(factory.get_tshirt()); */

        myRef.child(profile.getUserId()).setValue(profile);

        // finish adding the user
        progressBar_signup.setVisibility(View.GONE);
        finish();
    }
}
