package com.example.teamfive;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.managerViewHolder> {

    ArrayList<String> namelist;
    Context context;

    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference db=database.getReference();

    String user_id;

    private double nowlatitude;
    private double nowlongitude;

    public ManagerAdapter(ArrayList<String> namelist, Context context,String user_id) {
        this.namelist = namelist;
        this.context = context;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public managerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mangerxml,parent,false);
        ManagerAdapter.managerViewHolder holder = new ManagerAdapter.managerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull managerViewHolder holder, int position) {

        db.child("Name").child(namelist.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.name.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.child("Location").child(namelist.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userLocation user = snapshot.getValue(userLocation.class);
                holder.address.setText(getCurrentAddress(user.getLatitude(),user.getLongitude()));

                holder.eye.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ManagerActivity)context).moveCamera(user.getLatitude(),user.getLongitude());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7
            );
        }catch (IOException ioException){
            return "지오코더 서비스 사용불가";
        }catch (IllegalArgumentException illegalArgumentException){
            return "잘못된 GPS 좌표";
        }
        if(addresses==null || addresses.size()==0){
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();
    }


    @Override
    public int getItemCount() {
        return (namelist !=null ? namelist.size() : 0);
    }

    public class managerViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        ImageView eye;
        public managerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name=itemView.findViewById(R.id.mng_name);
            this.address=itemView.findViewById(R.id.mng_address);
            this.eye=itemView.findViewById(R.id.mng_eye);

        }
    }
}