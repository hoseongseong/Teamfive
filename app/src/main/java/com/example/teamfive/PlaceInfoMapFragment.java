package com.example.teamfive;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.teamfive.Notification.APIService;
import com.example.teamfive.Notification.Client;
import com.example.teamfive.Notification.MyResponse;
import com.example.teamfive.Notification.NotificationData;
import com.example.teamfive.Notification.SendData;
import com.example.teamfive.Notification.Token;
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
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceInfoMapFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private Context context;

    LatLng curruent_location;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference submitProfile;

    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference db=database.getReference();

    private FirebaseAuth mFirebaseAuth;
    String user_id;

    TextView place_name;
    TextView place_info;
    TextView place_time;

    LinearLayout place_with;

    LocationManager locationManager;
    LocationListener locationListener;

    LinearLayout place_change;

    ArrayList<String> tag = new ArrayList<>();

    String place_id;

    private static NaverMap naverMap;
    private MapView mapView;

    ImageView del;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.placeinfomapfragment, container, false);

        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        init();

        upload();

        return view;
    }


    public void init() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        place_id=getArguments().getString("place_id");
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        place_name = (view).findViewById(R.id.place_name);
        place_time = (view).findViewById(R.id.place_time);
        place_info = (view).findViewById(R.id.place_info);
        place_change = (view).findViewById(R.id.change_frame);
        del=(view).findViewById(R.id.del);

        place_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceInfoFragment placeInfoFragment = new PlaceInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("place_id",place_id);
                placeInfoFragment.setArguments(bundle);
                ((MainActivity)context).changeFragment(placeInfoFragment);
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).del(place_id);
            }
        });


    }

    public void upload() {
        db.child("Itemlist").child(user_id).child(place_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PlaceItem item=snapshot.getValue(PlaceItem.class);

                place_name.setText(item.getName());

                place_info.setText(getCurrentAddress(item.getLatitude(),item.getLongitude()));

                GpsTracker gpsTrcker = new GpsTracker(context);

                double nowlatitude = gpsTrcker.latitude;
                double nowlongitude = gpsTrcker.longitude;

                double distance1 = ruler(item.getLatitude(),item.getLongitude(),nowlatitude,nowlongitude);
                int distance = (int)distance1;
                if(distance<1000) {
                    place_time.setText(""+distance+"m");
                }
                else {
                    distance=distance/1000;
                    place_time.setText(""+distance+"km");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

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

        db.child("Itemlist").child(user_id).child(place_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PlaceItem item = snapshot.getValue(PlaceItem.class);

                LatLng latLng = new LatLng(item.getLatitude(),item.getLongitude());
                Marker marker=new Marker();
                marker.setTag(item);
                marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
                marker.setWidth(110);
                marker.setHeight(140);
                marker.setHideCollidedMarkers(true);
                marker.setPosition(latLng);
                marker.setMap(naverMap);

                db.child("Setting").child(user_id).child("zoom").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int zoom = snapshot.getValue(Integer.class);

                        CameraPosition cameraPosition=new CameraPosition(
                                latLng,zoom
                        );
                        naverMap.setCameraPosition(cameraPosition);
                        CameraUpdate cameraUpdate = CameraUpdate.zoomTo(zoom);
                        naverMap.moveCamera(cameraUpdate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


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


        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(curruent_location);


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
}
