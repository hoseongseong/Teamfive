package com.example.teamfive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class plusmapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static NaverMap naverMap;
    private MapView mapView;
    LatLng final_latLng;

    Context context=this;

    GpsTracker gpsTracker;

    TextView address;

    EditText search;

    Marker marker;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference db = firebaseDatabase.getReference();

    private FirebaseAuth mFirebaseAuth;
    String user_id;


    private double nowlatitude,nowlongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plusmap);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        address = (TextView)findViewById(R.id.address);
        search = (EditText)findViewById(R.id.search);

        mapView = (MapView)findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    public void next(View view) {
        Intent intent = new Intent(this,PlusFragment.class);
        intent.putExtra("latitude",final_latLng.latitude);
        intent.putExtra("longitude",final_latLng.longitude);
        startActivity(intent);
    }

    public void do_search(View view) {
        String add = search.getText().toString();

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName
                    (add, // 지역 이름
                            10); // 읽을 개수
        } catch (IOException e) {
            search.setText("지오코더 서비스 사용불가");
        }

        if (addresses != null) {
            if (addresses.size() == 0) {
                search.setText("해당되는 주소 정보는 없습니다");
            } else {
                Address addr = addresses.get(0);
                double lat = addr.getLatitude();
                double lon = addr.getLongitude();

                db.child("Setting").child(user_id).child("zoom").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int zoom = snapshot.getValue(Integer.class);

                        CameraPosition cameraPosition=new CameraPosition(
                                new LatLng(lat,lon),zoom
                        );
                        naverMap.setCameraPosition(cameraPosition);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                marker.setHideCollidedMarkers(true);

                LatLng latLng = new LatLng(lat,lon);

                marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
                marker.setWidth(110);
                marker.setHeight(140);
                marker.setPosition(latLng);
                marker.setMap(naverMap);
                final_latLng=latLng;
                address.setText(getCurrentAddress(final_latLng.latitude,final_latLng.longitude));
            }
        }


    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap=naverMap;
        gpsTracker = new GpsTracker(this);
        nowlatitude= gpsTracker.latitude;
        nowlongitude=gpsTracker.longitude;

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


        marker=new Marker();
        marker.setHideCollidedMarkers(true);

        marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
        marker.setWidth(110);
        marker.setHeight(140);
        LatLng latLng = new LatLng(nowlatitude,nowlongitude);
        marker.setPosition(latLng);
        marker.setMap(naverMap);
        final_latLng=latLng;
        address.setText(getCurrentAddress(final_latLng.latitude,final_latLng.longitude));


        final ArrayList<Marker> LIST= new ArrayList<Marker>();
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

                marker.setHideCollidedMarkers(true);

                marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
                marker.setWidth(110);
                marker.setHeight(140);
                marker.setPosition(latLng);
                marker.setMap(naverMap);
                final_latLng=latLng;
                address.setText(getCurrentAddress(final_latLng.latitude,final_latLng.longitude));
            }
        });


    }

    private String getCurrentAddress(double latitude, double longitude) {
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
    public void onStart(){
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();;
    }
    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}