package com.indiworks.stritnow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity3 extends AppCompatActivity {
    private static final int CHOOSE_IMAGE =10;
    ImageView imageView;
        // declaring user name object
        TextView name;

        //declaring user button object
        //Button save;
    private FirebaseAuth mAuth;

        // Declared storage reference
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

            // Initialized Storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Initializing  text view by id

        name  = findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();
        // Initializing button by id

        //save =  findViewById(R.id.save);

        //getting image view by id
        imageView = findViewById(R.id.profilepic);

        loadUserInformation();


    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(MainActivity3.this,MainActivity.class));
        }
    }
    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
            if (user.getPhotoUrl() != null) {
                Glide.with(MainActivity3.this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
            if(user.getDisplayName() != null) {
                name.setText(user.getDisplayName());
            }
    }


}