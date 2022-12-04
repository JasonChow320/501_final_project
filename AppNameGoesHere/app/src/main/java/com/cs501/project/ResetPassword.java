package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    // Views
    Button reset_button, back_button;
    EditText email_text;
    FirebaseAuth auth;

    private static final String TAG = "ResetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        reset_button = (Button) findViewById(R.id.reset_button);
        back_button = (Button) findViewById(R.id.reset_back_button);
        email_text = (EditText) findViewById(R.id.editTextResetEmailText);

        // set up onClick methods
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetEmail(email_text.getText().toString());
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // private method to reset email
    private void resetEmail(String email){

        // check inputs
        if(email == null || email.length() <= 0){
            Toast.makeText(ResetPassword.this, "Unable to parse email. Please try again",
                    Toast.LENGTH_SHORT).show();
            this.email_text.setText("");
            return;
        }


        // send reset email
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            email_text.setText("");
                            Toast.makeText(ResetPassword.this, "Email sent.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
}
