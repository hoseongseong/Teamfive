package com.example.teamfive;

import android.content.Context;
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

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    ArrayList<String> placelist;
    Context context;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference submitProfile;

    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference db=database.getReference();

    String user_id;

    private double nowlatitude;
    private double nowlongitude;

    public PlaceAdapter(ArrayList<String> placelist, Context context,String user_id) {
        this.placelist = placelist;
        this.context = context;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.placexml,parent,false);
        PlaceAdapter.PlaceViewHolder holder = new PlaceAdapter.PlaceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {

        GpsTracker gps = new GpsTracker(context);
        nowlatitude=gps.latitude;
        nowlongitude=gps.longitude;


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        db.child("Itemlist").child(user_id).child(placelist.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PlaceItem item=snapshot.getValue(PlaceItem.class);
                String plc_name=item.getName();
                holder.name_place.setText(plc_name);
                holder.date_place.setText(item.getTime());
                double distance1 = ruler(item.getLatitude(),item.getLongitude(),nowlatitude,nowlongitude);
                int distance = (int)distance1;
                if(distance<1000) {
                    holder.location_place.setText(""+distance+"m");
                }
                else {
                    distance=distance/1000;
                    holder.location_place.setText(""+distance+"km");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        submitProfile = storageReference.child(user_id+"/"+placelist.get(position));
        submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.img_place)
                        .load(uri)
                        .into(holder.img_place);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        holder.ll_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceInfoFragment placeInfoFragment = new PlaceInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("place_id",placelist.get(position));
                placeInfoFragment.setArguments(bundle);
                ((MainActivity)context).changeFragment(placeInfoFragment);
            }
        });



    }

    private double ruler(double first_latitude,double first_longitude,double second_latitude,double second_longitude)
    {
        double distance=0.0;

        double R = 6372.8;

        double dLat = Math.toRadians(first_latitude-second_latitude);
        double dLon = Math.toRadians(first_longitude-second_longitude);
        double fr_latitude = Math.toRadians(first_latitude);
        double sr_latitude = Math.toRadians(second_latitude);

        double tempt = Math.pow(Math.sin(dLat/2),2)+Math.pow(Math.sin(dLon/2),2)*Math.cos(fr_latitude)*Math.cos(sr_latitude);
        double c = 2*Math.asin(Math.sqrt(tempt));

        distance = R*c*1000;

        return distance;
    }

    @Override
    public int getItemCount() {
        return (placelist !=null ? placelist.size() : 0);
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_place;
        LinearLayout ll_box;
        TextView name_place;
        TextView location_place;
        TextView date_place;
        ImageView img_place;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ll_place=itemView.findViewById(R.id.place_ll);
            this.name_place=itemView.findViewById(R.id.place_name);
            this.location_place=itemView.findViewById(R.id.place_location);
            this.date_place=itemView.findViewById(R.id.place_date);
            this.img_place=itemView.findViewById(R.id.place_img);
            this.ll_box=itemView.findViewById(R.id.textbox);
        }
    }
}