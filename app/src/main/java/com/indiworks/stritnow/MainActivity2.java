package com.indiworks.stritnow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity2 extends AppCompatActivity  {
    private static final int CHOOSE_IMAGE = 80;
    EditText name;
    EditText password;
    EditText email;
    EditText phone;
    TextView textView;
    Button signup;
    ImageView imageView;
    // variable that store url of the image
    String profileImageUrl;

    Uri uriProfileImage;

    ProgressBar progressBar;

    // authentication reference
    private FirebaseAuth mAuth;
    // Declared storage reference
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        // Initialized Firebase Authentication reference
        mAuth = FirebaseAuth.getInstance();
        //NAME BOX
        name = (EditText)findViewById(R.id.name);
        //PASSWORD
        password = (EditText)findViewById(R.id.password);
        //EMAIL BOX
        email = (EditText)findViewById(R.id.email);
        //PHONE NUMBER
        phone = (EditText)findViewById(R.id.phone);
        //TEXTVIEW
        textView = (TextView)findViewById(R.id.login);
        // Initialized Storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //Progress Bar
        progressBar = (ProgressBar)findViewById(R.id.progressbar3);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MainActivity2.this,MainActivity.class);
                startActivity(intent);
            }
        });
        signup = (Button)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                saveUserInformation();
            }
        });


        //getting image view by id
        imageView = findViewById(R.id.uploadpic);
        //Initializing onclick listener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
    }

    private void saveUserInformation() {
        String displayName = name.getText().toString().trim();
        String userphone = phone.getText().toString().trim();

        if(displayName.isEmpty()){
            name.setError("Name is Required");
            name.requestFocus();
            return;
        }
        if(userphone.isEmpty()){
            phone.setError("phone Required");
            phone.requestFocus();
            return;
        }
        if (userphone.length() < 10){
            phone.setError("Enter a valid number");
            phone.requestFocus();
        }
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null){
            UserProfileChangeRequest profile = new  UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity2.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void registerUser() {
        String userpassword = password.getText().toString().trim();
        String useremail = email.getText().toString().trim();


        if(useremail.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
            email.setError("Enter a valid Email");
            email.requestFocus();
            return;

        }

        if(userpassword.isEmpty()){
            password.setError("password Required");
            password.requestFocus();
            return;
        }


        if(userpassword.length() < 8){
            password.setError("Minimum length of password should be 8");
            password.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(useremail, userpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"User Registered Successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity2.this,MainActivity3.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(),"Email already registered",Toast.LENGTH_SHORT).show();
                                email.requestFocus();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                 }
                });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
           uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
        private void  uploadImageToFirebaseStorage(){
                        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/"+ System.currentTimeMillis() + ".jpg");
                        if(uriProfileImage != null){
                            progressBar.setVisibility(View.VISIBLE);
                            profileImageRef.putFile(uriProfileImage)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressBar.setVisibility(View.GONE);
                                    //profileImageUrl = taskSnapshot.getDownloadUrl().toString();

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(MainActivity2.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }

                }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select a Image"),CHOOSE_IMAGE);
    }

}