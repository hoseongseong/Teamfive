package com.example.teamfive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
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
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//wntjr
public class mapFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private Context context;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference submitProfile;

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

    InfoWindow infoWindow;

    LinearLayout ll;
    ImageView ll_cancer;

    ImageView map_img;
    TextView map_name;
    TextView map_location;
    TextView map_date;
    TextView map_address;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.mapfragment_layout, container, false);

        checkPermissions();

        init();

        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        return view;
    }


    public void init() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        ll=(LinearLayout)view.findViewById(R.id.map_ll);
        ll_cancer=(ImageView)view.findViewById(R.id.cancel_ll);

        map_img=(ImageView)view.findViewById(R.id.map_img);
        map_name=(TextView)view.findViewById(R.id.map_name);
        map_location=(TextView)view.findViewById(R.id.map_location);
        map_date=(TextView)view.findViewById(R.id.map_date);
        map_address=(TextView)view.findViewById(R.id.map_address);

        ll_cancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams params
                        = (LinearLayout.LayoutParams) ll.getLayoutParams();
                params.weight = 1000;
                ll.setLayoutParams(params);

            }
        });


        placelist = new ArrayList();

        infoWindow = new InfoWindow();

        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) {

            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                Marker marker = infoWindow.getMarker();
                PlaceItem item = (PlaceItem)marker.getTag();
                return item.getName();
            }
        });


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
                    placelist.add(item);

                    db.child("Itemlist").child(user_id).child(sn.getKey()).child("visit").setValue(0);

                    String key = sn.getKey();

                    LatLng latLng = new LatLng(item.getLatitude(),item.getLongitude());
                    Marker marker=new Marker();
                    marker.setTag(item);
                    marker.setIcon(OverlayImage.fromResource(R.drawable.bingkamarker));
                    marker.setWidth(110);
                    marker.setHeight(140);
                    marker.setHideCollidedMarkers(true);
                    marker.setPosition(latLng);
                    marker.setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay overlay) {

                            LinearLayout.LayoutParams params
                                    = (LinearLayout.LayoutParams) ll.getLayoutParams();
                            params.weight = 2;
                            ll.setLayoutParams(params);

                            GpsTracker gps = new GpsTracker(context);

                            double nowlatitude = gps.latitude;
                            double nowlongitude = gps.longitude;

                            db.child("Itemlist").child(user_id).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    PlaceItem item=snapshot.getValue(PlaceItem.class);
                                    String plc_name=item.getName();
                                    map_name.setText(plc_name);

                                    Calendar posttime = Calendar.getInstance();

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        posttime.setTime(format.parse(item.getTime()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    Calendar current = Calendar.getInstance();

                                    long diff = (current.getTimeInMillis() - posttime.getTimeInMillis())/1000;

                                    if(diff>31536000) {
                                        map_date.setText(""+diff/31536000+"년 전");
                                    }

                                    else if(diff>2678400) {
                                        map_date.setText(""+diff/2678400+"달 전");
                                    }

                                    else if(diff>604800) {
                                        map_date.setText(""+diff/604800+"주 전");
                                    }

                                    else if(diff>86400) {
                                        map_date.setText(""+diff/86400+"일 전");
                                    }

                                    else if(diff>3600) {
                                        map_date.setText(""+diff/3600+"시간 전");
                                    }

                                    else if(diff>60) {
                                        map_date.setText(""+diff/60+"분 전");
                                    }

                                    else {
                                        map_date.setText(""+diff+"초 전");
                                    }

                                    double distance1 = ruler(item.getLatitude(),item.getLongitude(),nowlatitude,nowlongitude);
                                    int distance = (int)distance1;
                                    map_location.setText(""+distance+"m");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            map_address.setText(getCurrentAddress(item.getLatitude(),item.getLongitude()));

                            submitProfile = storageReference.child(user_id+"/"+key);
                            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(map_img)
                                            .load(uri)
                                            .into(map_img);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });


                            Marker marker = (Marker) overlay;

                            infoWindow.open(marker);

                            return false;
                        }
                    });
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


        db.child("Setting").child(user_id).child("zoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int zoom = snapshot.getValue(Integer.class);

                CameraPosition cameraPosition=new CameraPosition(
                        new LatLng(latitude,longitude),zoom
                );

                naverMap.setCameraPosition(cameraPosition);

                CameraUpdate cameraUpdate = CameraUpdate.zoomTo(zoom);
                naverMap.moveCamera(cameraUpdate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(curruent_location);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Itemlist").child(mFirebaseAuth.getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    PlaceItem item = snapshot1.getValue(PlaceItem.class);
                    db.child("Setting").child(user_id).child("meter").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            int meter = snapshot.getValue(Integer.class);

                            if(ruler(latitude,longitude,item.getLatitude(),item.getLongitude())<=meter) {

                                if (item.getVisit() == 0) {

                                    db.child("Itemlist").child(user_id).child(snapshot1.getKey()).child("visit").setValue(1);

                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                    String timeline = dateFormat.format(date);

                                    db.child("Itemlist").child(user_id).child(snapshot1.getKey()).child("time").setValue(timeline);

                                    final Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("UserTokenList").child(mFirebaseAuth.getCurrentUser().getUid());
                                            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Token item2 = snapshot.getValue(Token.class);
                                                    APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                                                    apiService.sendNotification(new NotificationData(new SendData(item.getInfo(), item.getName()), item2.getToken()))
                                                            .enqueue(new Callback<MyResponse>() {
                                                                @Override
                                                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                                    if (response.code() == 200) {
                                                                        if (response.body().success == 1) {
                                                                            Log.e("Notification", "success");
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    };
                                    Thread tr = new Thread(runnable);
                                    tr.start();
                                }
                            }
                            else db.child("Itemlist").child(user_id).child(snapshot1.getKey()).child("visit").setValue(0);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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
