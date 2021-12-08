package com.example.teamfive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.ArrayList;

public class plusmapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static NaverMap naverMap;
    private MapView mapView;
    LatLng final_latLng;

    Context context=this;

    GpsTracker gpsTracker;

    private double nowlatitude,nowlongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plusmap);
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

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap=naverMap;
        gpsTracker = new GpsTracker(this);
        nowlatitude= gpsTracker.latitude;
        nowlongitude=gpsTracker.longitude;
        CameraPosition cameraPosition=new CameraPosition(
                new LatLng(nowlatitude,nowlongitude),14
        );
        naverMap.setCameraPosition(cameraPosition);
        final ArrayList<Marker> LIST= new ArrayList<Marker>();
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                for(int i=0;i<LIST.size();i++)LIST.get(i).setMap(null);
                LIST.clear();
                Marker marker=new Marker();
                LIST.add(marker);
                marker.setHideCollidedMarkers(true);

                marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
                marker.setWidth(110);
                marker.setHeight(140);
                //맵에 띄울때는 latLng 밑에 두줄 쓰면됩니다.
                marker.setPosition(latLng);
                marker.setMap(naverMap);
                final_latLng=latLng;
            }
        });
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