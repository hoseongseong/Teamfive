package com.example.teamfive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ManagerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static NaverMap naverMap;
    private MapView mapView;


    GpsTracker gpsTracker;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference db = firebaseDatabase.getReference();

    private FirebaseAuth mFirebaseAuth;
    String user_id;

    ArrayList<Marker> LIST= new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }



    public void out(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap=naverMap;
        gpsTracker = new GpsTracker(this);
        double nowlatitude= gpsTracker.latitude;
        double nowlongitude=gpsTracker.longitude;

        db.child("Setting").child(user_id).child("zoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int zoom = snapshot.getValue(Integer.class);

                CameraPosition cameraPosition=new CameraPosition(
                        new LatLng(nowlatitude,nowlongitude),zoom
                );
                naverMap.setCameraPosition(cameraPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for(int i=0;i<LIST.size();i++)LIST.get(i).setMap(null);
        LIST.clear();

        db.child("Manager").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot sn : snapshot.getChildren()) {
                    String person = sn.getValue(String.class);
                    try {
                        db.child("Location").child(person).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                                userLocation user_location = snapshot2.getValue(userLocation.class);

                                LatLng latLng = new LatLng(user_location.getLatitude(),user_location.getLongitude());

                                Marker marker = new Marker();
                                LIST.add(marker);
                                marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
                                marker.setWidth(110);
                                marker.setHeight(140);
                                marker.setHideCollidedMarkers(true);
                                marker.setPosition(latLng);
                                marker.setMap(naverMap);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    } catch(NullPointerException e ) {}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void refresh(View view) {

        for(int i=0;i<LIST.size();i++)LIST.get(i).setMap(null);
        LIST.clear();

        db.child("Manager").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot sn : snapshot.getChildren()) {
                    String person = sn.getValue(String.class);
                    try {
                        db.child("Location").child(person).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                                userLocation user_location = snapshot2.getValue(userLocation.class);

                                LatLng latLng = new LatLng(user_location.getLatitude(),user_location.getLongitude());

                                Marker marker = new Marker();
                                LIST.add(marker);
                                marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
                                marker.setWidth(110);
                                marker.setHeight(140);
                                marker.setHideCollidedMarkers(true);
                                marker.setPosition(latLng);
                                marker.setMap(naverMap);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    } catch(NullPointerException e ) {}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}