package com.example.project2102020;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class home extends Fragment {

    RecyclerView recyclePost;
    ArrayList<model>list;
    postAdapter adapter;
    String uid;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences pre = getContext().getSharedPreferences("project2102020", Context.MODE_PRIVATE);
        uid = pre.getString("uid", "");

        Boolean dialog1=pre.getBoolean("dialog",false);
        recyclePost = view.findViewById(R.id.recyclerPost);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclePost.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new postAdapter(getContext(), list);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog, null);
        builder.setView(dialogView);




      getActivity().startService(new Intent(getContext(),reportService.class));

        // Initialize the dialog's views
        Button dialogCloseButton = dialogView.findViewById(R.id.dialogCloseButton);

        AlertDialog dialog = builder.create();

        if (!dialog1){
            dialog.show();
        }



        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPreferences.Editor editor= pre.edit();
                editor.putBoolean("dialog",true);
                editor.commit();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.child("posts").getChildren()) {
                    String id = snapshot1.getKey();
                    String message = snapshot1.child("content").getValue(String.class);
                    String time = String.valueOf(snapshot1.child("time").getValue(long.class));
                    long like = snapshot1.child("like").getChildrenCount();
                    String image=snapshot1.child("imageUrl").getValue(String.class);

                    long comment = snapshot1.child("comment").getChildrenCount();
                    Boolean isLiked = snapshot1.child("like").hasChild(uid);model model = new model(message, like, comment, time, isLiked, id,image);
                    list.add(0, model);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPost();
            }
        });
        recyclePost.setAdapter(adapter);
        return view;
    }

    public void fetchPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String id = snapshot.getKey();
                String message = snapshot.child("content").getValue(String.class);
                String time = String.valueOf(snapshot.child("time").getValue(long.class));
                long like = snapshot.child("like").getChildrenCount();
                String image=snapshot.child("imageUrl").getValue(String.class);
                long comment = snapshot.child("comment").getChildrenCount();
                Boolean isLiked = snapshot.child("like").hasChild(uid);
                model model = new model(message, like, comment, time, isLiked, id,image);

                // Add the new post at the beginning of the list
                list.add(0, model);
                adapter.notifyDataSetChanged();

                // Stop refreshing animation
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted

            } else {
                // Permission denied

            }

        }}

    }


