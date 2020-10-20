package com.example.push_notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {
    private EditText emailET,passET;
    private Button loginBT;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    //private TextView textview;
    //1.notification channel
    //2.notification builder
    //3.notification manager
    private static final String CHANNEL_ID="diponkar_saha";
    private static final String CHANNEL_Name="diponkar saha";
    private static final String CHANNEL_DESC="diponkar_saha_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textview=findViewById(R.id.textView);
        emailET=findViewById(R.id.editTextEmail);
        passET=findViewById(R.id.editTextPass);
        loginBT=findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();

            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_Name,NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager=getSystemService(NotificationManager.class);
        }


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token=task.getResult().getToken();
                           // textview.setText("Token"+token);

                        } else{
                            //textview.setText("token not generate");
                            //textview.setText(task.getException().getMessage());

                        }
                    }
                });


        /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNotification();
            }
        });*/

    }
    private void createUser(){
        final String email=emailET.getText().toString().trim();
        final String password=passET.getText().toString().trim();
        if(email.isEmpty()){
            emailET.setError("Email Required");
            emailET.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passET.setError("Password Required");
            passET.requestFocus();
            return;
        }
        if(password.length()<6){
            passET.setError("Password Should be atleast six character");
            passET.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //FirebaseUser user = mAuth.getCurrentUser();
                            startProfileActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            if(task.getException()instanceof FirebaseAuthUserCollisionException){
                                signin(email,password);
                            }else{
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                
                            }
                               

                        }

                        // ...
                    }
                });

    }

    private void signin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                           // FirebaseUser user = mAuth.getCurrentUser();
                            startProfileActivity();

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    private void startProfileActivity(){
        Intent intent =new Intent(this,ProfileActivity.class);
        startActivity(intent);

    }
    private void displayNotification(){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setContentTitle("Hurry its working")
                .setContentText("wellcome")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat=
                NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1,builder.build());
    }
}