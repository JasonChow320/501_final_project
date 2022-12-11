package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cs501.project.Model.FireBaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
    This activity controls the login flow of the user
 */
public class SignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button sign_up_button, sign_in_button, forget_password_button;
    private ProgressBar progressBar_sign_in;

    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        email = (EditText) findViewById(R.id.editTextEmailField);
        password = (EditText) findViewById(R.id.editTextPasswordField);
        sign_in_button = (Button) findViewById(R.id.log_signin_button);
        sign_up_button = (Button) findViewById(R.id.login_signup_button);
        forget_password_button = (Button) findViewById(R.id.resetPassword_button);
        progressBar_sign_in = (ProgressBar) findViewById(R.id.progressBar_signin);

        // Hide Progress Bar
        progressBar_sign_in.setVisibility(View.GONE);

        // Set Up Button OnClicks
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calls signup
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);
            }
        });

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_in(email.getText().toString(), password.getText().toString());
            }
        });

        forget_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call ResetPassword Activity
                Intent i = new Intent(SignIn.this, ResetPassword.class);
                startActivity(i);
            }
        });
    }

    // Private method for signing in
    private void sign_in(String email, String password){

        // Check Inputs
        if(email == null || password == null || email.length() <= 0 || password.length() <= 0){
            Toast.makeText(SignIn.this, getResources().getString(R.string.unable_parse_text),
                    Toast.LENGTH_SHORT).show();
            this.resetFields();
            return;
        }

        // Login for users (Might want to hide the password in plaintext when logging xD )
        progressBar_sign_in.setVisibility(View.VISIBLE);
        Log.d(TAG, "logging in with: " + email + ", " + password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            Toast.makeText(SignIn.this, getResources().getString(R.string.sign_in_success),
                                    Toast.LENGTH_SHORT).show();

                            resetFields();
                            progressBar_sign_in.setVisibility(View.GONE);

                            // call FireBaseManager to sync up
                            FireBaseManager.getInstance();

                            // call login
                            Intent i = new Intent(SignIn.this, Login.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user
                            Log.d(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, getResources().getString(R.string.auth_failed_simple),
                                    Toast.LENGTH_SHORT).show();
                            resetFields();
                            progressBar_sign_in.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();

            // TODO could be a bug since we're auto-logging in
            Log.d(TAG, "Already have a user, log in!");

            // calls login
            Intent i = new Intent(SignIn.this, Login.class);
            startActivity(i);
        }
    }

    private void resetFields(){
        this.email.setText("");
        this.password.setText("");
    }
}
