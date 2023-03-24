package com.example.googlemapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //INITIALIZE VARIABLE
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    DatabaseReference DRef;
    Details detail = new Details();
    int count;
    Circle circle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //assign variable

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        // initialize fused location

        client = LocationServices.getFusedLocationProviderClient(this);

        //when permission grant
        //call method
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);


        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {

                    if (location != null) {
                        //sync map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                //initialize lat long
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                // create marker optionL
                                MarkerOptions options = new MarkerOptions().position(latLng).title("HELLO");
                                //ZOOM MAP


                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                // add marker in app
                                googleMap.addMarker(options);

                                circle = googleMap.addCircle(new CircleOptions()
                                        //Center position;
                                        .center(new LatLng(location.getLatitude(), location.getLongitude()))    //27.697791, 84.424048   //27.695005,84.431811
                                        .radius(900)
                                        .strokeColor(Color.RED)
                                );
                                // retrive from database


                                DRef = FirebaseDatabase.getInstance().getReference().child("Detail");
                                DRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                            String Lat = String.valueOf(dataSnapshot1.child("Lat").getValue());
                                            String Lon = String.valueOf(dataSnapshot1.child("Lon").getValue());
                                            Log.i("value", Lat);
                                            detail.setLatitude(Float.valueOf(Lat));
                                            detail.setLongitude(Float.valueOf(Lon));


                                            //Coordinate position

                                            final float[] distance = new float[2];

                                           /* Log.i("value", String.valueOf(location.getLatitude()));
                                            Log.i("value", String.valueOf(location.getLongitude()));
                                            Log.i("value", String.valueOf(circle.getCenter().latitude));
                                            Log.i("value", String.valueOf(circle.getCenter().longitude));
                                            Log.i("value", String.valueOf(distance));*/
                                            Location.distanceBetween(detail.getLatitude(), detail.getLongitude(),
                                                    circle.getCenter().latitude, circle.getCenter().longitude, distance);
                                            /*    Log.i("value", String.valueOf(distance[0]));*/


                                            if (distance[0] < circle.getRadius()) {
                                                count++;
                                                /*  Log.i("distance", "Outside Cirlce");*/

                                            } /*else {
                                                Log.i("distance", "Inside Cirlce");
                                                count++;
                                            }*/

                                        }

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });


                    }


                }
            });

        }


    }

    public void btn_click(View v) {
        if (count < 1) {
            Toast.makeText(MainActivity.this, "No Active Case", Toast.LENGTH_SHORT).show();

        } else if (count > 0) {
            Toast.makeText(MainActivity.this, "active Cases Inside The Circle" + "  " + String.valueOf(count), Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
}