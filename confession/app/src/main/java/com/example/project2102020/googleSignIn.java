package com.example.project2102020;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class googleSignIn extends AppCompatActivity {


    ProgressBar progress;
    GoogleSignInOptions gso;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=100;
    LinearLayout googleSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);
        googleSignIn=findViewById(R.id.googleSign);
        progress=findViewById(R.id.progress);
        mAuth=FirebaseAuth.getInstance();
        SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
        Boolean isLoggedIn=pre.getBoolean("isLoggedIn",false);

        // add your database url and json file to use the app
        DatabaseReference reference= FirebaseDatabase.getInstance().getReferenceFromUrl("your database url here");


        if (isLoggedIn){
            startActivity(new Intent(getApplicationContext(),frameMain.class));
            finish();
        }

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();


        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);


        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();


            }
        });






    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);


            try {

                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            }

            catch (Exception e){

                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account=completedTask.getResult(ApiException.class);
            firebaseAuth(account.getIdToken());


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void firebaseAuth(String idToken){

        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user=mAuth.getCurrentUser();
                    String uid=user.getUid();

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                    reference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(uid)){
                                progress.setVisibility(View.VISIBLE);
                                SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
                                SharedPreferences.Editor editor=pre.edit();
                                editor.putString("uid",uid);
                                editor.putBoolean("isLoggedIn",true);
                                editor.commit();
                                Intent intent=new Intent(getApplicationContext(),frameMain.class);
                                progress.setVisibility(View.GONE);
                                startActivity(intent);
                                finish();

                            }

                            else {

                                progress.setVisibility(View.VISIBLE);
                                SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
                                SharedPreferences.Editor editor=pre.edit();
                                editor.putString("uid",uid);
                                editor.commit();
                                Intent intent=new Intent(getApplicationContext(),register.class);
                                progress.setVisibility(View.GONE);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }




}