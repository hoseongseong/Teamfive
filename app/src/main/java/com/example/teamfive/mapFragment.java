package com.example.teamfive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;
//wntjr
public class mapFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private Context context;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private static NaverMap naverMap;
    private MapView mapView;

    LatLng curruent_location;

    private FirebaseAuth mFirebaseAuth;
    String user_id;

    LocationManager locationManager;
    LocationListener locationListener;

    ArrayList<PlaceItem> placelist;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference db = firebaseDatabase.getReference();

    TextView test;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.mapfragment_layout, container, false);

        checkPermissions();


        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        { @Override public void run() {
            test.setText("delay");
        }
        },1000);


        init();

        return view;
    }


    public void init() {

        test = (TextView)view.findViewById(R.id.test);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        placelist = new ArrayList();

        db.child("Itemlist").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                placelist.clear();
                for(DataSnapshot sn : snapshot.getChildren()) {
                    PlaceItem item = sn.getValue(PlaceItem.class);
                    placelist.add(item);
                    test.setText(sn.getKey());

                    LatLng latLng = new LatLng(item.getLatitude(),item.getLongitude());
                    Marker marker=new Marker();
                    marker.setPosition(latLng);
                    marker.setMap(naverMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        { @Override public void run() { }
            },1000);
        test.setText(""+placelist.size());
    }

    public void marking() {
        for(int i=0;i<placelist.size();i++) {
            PlaceItem item = placelist.get(i);
            LatLng latLng = new LatLng(item.getLatitude(),item.getLongitude());
            Marker marker=new Marker();
            marker.setPosition(latLng);
            marker.setMap(naverMap);

        }
    }

    private void checkPermissions() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean checkResult = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false;
                    break;
                }
            }
            if (checkResult) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(context, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(context, "위치 권한이 거부되었습니다. 설정(앱 정보)에서 위치 권한을 허용해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(context, "근처의 게시물을 보기 위해서는 위치 접근 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("근처의 게시물을 보기 위해서는 위치 서비스가 필요합니다.\n위치 권한을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.cancel();
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    checkRunTimePermission();
                    return;
                }
                break;
        }
    }

    private boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updatelocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        String locationProvider;
        locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationProvider, 1, 1, locationListener);
        locationProvider=LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider,1,1,locationListener);


        db.child("Itemlist").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                placelist.clear();
                for(DataSnapshot sn : snapshot.getChildren()) {
                    PlaceItem item = sn.getValue(PlaceItem.class);

                    Double latitude = (Double)sn.child("latitude").getValue();
                    Double longitude = (Double)sn.child("longitude").getValue();
                    placelist.add(item);
                    test.setText(sn.getKey());

                    LatLng latLng = new LatLng(latitude,longitude);
                    Marker marker=new Marker();
                    marker.setPosition(latLng);
                    marker.setMap(naverMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void updatelocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        curruent_location = new LatLng(latitude,longitude);

        CameraPosition cameraPosition=new CameraPosition(
                new LatLng(latitude,longitude),15
        );
        naverMap.setCameraPosition(cameraPosition);

        CameraUpdate cameraUpdate = CameraUpdate.zoomTo(15);
        naverMap.moveCamera(cameraUpdate);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(curruent_location);
    }
    //구현 중인 부분
    public void checkItem(double latitude,double longitude) {
        String user_id="";  //유저 아이디로 이용할 부분

        db.child("Users").child(user_id).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot sn : snapshot.getChildren()) {
                    PlaceItem item = sn.getValue(PlaceItem.class);
                    String key = sn.getKey();
                    if(item.isNear()) {
                        if(50<ruler(latitude,longitude,item.getLatitude(), item.getLongitude())) {
                            db.child("Users").child(user_id).child("Items").child(key).child("near").setValue(false);
                        }
                    }
                    else {
                        if(50>ruler(latitude,longitude,item.getLatitude(), item.getLongitude())) {
                            //alaram;
                            db.child("Users").child(user_id).child("Items").child(key).child("near").setValue(true);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}
