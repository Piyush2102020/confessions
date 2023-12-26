package com.example.project2102020;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reportService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkReports();
        return START_STICKY;
    }

    public void checkReports(){

        SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
        String uid=pre.getString("uid","");
        String randomName=pre.getString("randomName","");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("available").child(randomName).removeValue();

        reference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1:snapshot.child("posted").getChildren()){
                    String postId=snapshot1.getKey();

                    checkPost(postId,uid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkPost(String postId,String uid){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        reference.child("posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long reports=snapshot.child("report").getChildrenCount();


                if (reports==10){
                    reference.child("posts").child(postId).removeValue();
                    reference.child("users").child(uid).child("posted").child(postId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
