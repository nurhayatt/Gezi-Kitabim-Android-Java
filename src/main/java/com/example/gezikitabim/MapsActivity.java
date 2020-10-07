package com.example.gezikitabim;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    static SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ZoomControls zoom=(ZoomControls)findViewById( R.id.zoom);
        //uzaklaştırma tuşuna basıldığında etkin hala getirir
        zoom.setOnZoomOutClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera( CameraUpdateFactory.zoomOut() );
            }
        } );
        //yakınlaştırma tuşuna basıldığında etkin hale getirir
        zoom.setOnZoomInClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera( CameraUpdateFactory.zoomIn() );
            }
        } );
        final Button btn_Htip=(Button)findViewById( R.id.btn_uydu );
        btn_Htip.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL)
                {
                    mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID);
                    btn_Htip.setText("NORMAL");
                }
                else
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL  );
                    btn_Htip.setText( "UYDU" );
                }
            }
        } );
        Button btnGo=(Button) findViewById(R.id.btn_git);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etLocation=(EditText)findViewById(R.id.git_lokasyon);
                String location=etLocation.getText().toString();
                if(location!=null && !location.equals("")){
                    List<Address> adressList=null;
                    Geocoder geocoder=new Geocoder(MapsActivity.this);
                    try {
                        adressList=geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address=adressList.get(0);
                    LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Burası "+location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        });

}



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent ıntent = getIntent();
        String info = ıntent.getStringExtra("info");
        if (info.matches("new")) {
            locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("com.example.gezikitabim", MODE_PRIVATE);
                    boolean firstTimeCheck = sharedPreferences.getBoolean("notFirstTime", false);
                    if (!firstTimeCheck) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                        sharedPreferences.edit().putBoolean("notFirsTime", true).apply();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if (Build.VERSION.SDK_INT >= 23) {
                //eger izin yok ise
                if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //izin iste
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    //izn varsa kullanıcnın konumunu al
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, locationListener);
                    mMap.clear();
                    Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastlocation != null) //cökme olmamması için yazmasaydık boş olunca çökme oluyor
                    {
                        LatLng lastUserLocation = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                    }
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                }

            }

        }
        else
        {
            mMap.clear();
            int  position=ıntent.getIntExtra("position",0);
            LatLng location;
            location = new LatLng( Konum_Ekle.locations.get( position).latitude, Konum_Ekle.locations.get( position ).longitude );
            String yeradi=Konum_Ekle.names.get(position);
            mMap.addMarker(new MarkerOptions().title(yeradi).position(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        }
    }

    @Override //kullanıcın izni yoksa
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0)
        {
            if (requestCode==1)
            {
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Intent ıntent= getIntent();
                    String info = ıntent.getStringExtra("info");
                    if (info.matches("new")) {
                        Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastlocation != null) //cökme olmamması için yazmasaydık boş olunca çökme oluyor
                        {
                            LatLng lastUserLocation = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }

                    } else {
                        mMap.clear();
                        int position = ıntent.getIntExtra("position", 0);
                        LatLng location = new LatLng( Konum_Ekle.locations.get( position ).latitude, Konum_Ekle.locations.get( position ).longitude );
                        String yeradi = String.valueOf( Konum_Ekle.names.get(position) );

                        mMap.addMarker(new MarkerOptions().icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET) ).title(yeradi).position(location));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                    }
                }}}}


    @Override
    public void onMapLongClick(LatLng latLng)//kullanıcı uzun süre tıklayınca adreslerı alması için bu metod kullanıldı
    {
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="";
        try
        {
            List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList!=null && addressList.size()>0)
            {
                if (addressList.get(0).getThoroughfare()!=null)
                {
                    address +=addressList.get(0).getThoroughfare();
                    if (addressList.get(0).getSubThoroughfare()!=null)
                    {
                        address +=addressList.get(0).getSubThoroughfare();
                    }
                }
            }
            else
            {
                address="Yeni Yer";
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().title( address).position( latLng ) );
        Toast.makeText( getApplicationContext(),"Yeni Yer Eklendi!", Toast.LENGTH_SHORT).show();
        Konum_Ekle.names.add(address);
        Konum_Ekle.locations.add( latLng );
        Konum_Ekle.arrayAdapter.notifyDataSetChanged();
        try //sqllite işlemleri burada
        {
            Double l1=latLng.latitude;
            Double l2=latLng.longitude;
            String coord1=l1.toString();
            String coord2=l2.toString();
            database=this.openOrCreateDatabase("Yerler",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS yerler(name VARCHAR,latitude VARCHAR,longitude VARCHAR)");
            String toCompile="INSERT INTO yerler(name,latitude,longitude) VALUES(?,?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(toCompile);
            sqLiteStatement.bindString(1,address);
            sqLiteStatement.bindString(2,coord1);
            sqLiteStatement.bindString(3,coord2);
            sqLiteStatement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}

