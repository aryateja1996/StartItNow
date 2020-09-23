package com.indiworks.stritnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
        TextView textView;
        Button buttonlogin;
        EditText useremail;
        EditText userpassword;
        FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        useremail = (EditText)findViewById(R.id.email) ;
        userpassword = (EditText)findViewById(R.id.password) ;

        textView=(TextView)findViewById(R.id.signup);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                finish();
                startActivity(intent);
            }
        });
        buttonlogin = (Button)findViewById(R.id.login);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlogin();
            }


            private void userlogin() {

                String password = userpassword.getText().toString().trim();
                String email = useremail.getText().toString().trim();

                if(email.isEmpty()){
                    useremail.setError("Email is required");
                    useremail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    useremail.setError("Enter a valid Email");
                    useremail.requestFocus();
                    return;

                }

                if(password.isEmpty()){
                   userpassword.setError("password Required");
                    userpassword.requestFocus();
                    return;
                }

                if(password.length() < 8){
                    userpassword.setError("Minimum length of password should be 8");
                    userpassword.requestFocus();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            Intent intent = new Intent(MainActivity.this,MainActivity3.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(MainActivity.this,MainActivity3.class));
        }
    }
}