package edu.stlawu.locationgps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import  android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class MainActivity
        extends AppCompatActivity
        implements Observer {

    private TextView tv_lat;
    private TextView tv_lon;
    private TextView tv_velo;
    private TextView d_box;
    private TextView location_text;
    private Button new_point;
    private ScrollView scroll_View;
    private TableLayout table;
    private int index;

    private Double curr_lat;
    private Double curr_lon;
    private long curr_time;

    private Double old_lat;
    private Double old_lon;
    private long old_time;

    private Observable location;
    private LocationHandler handler = null;
    private final static int PERMISSION_REQUEST_CODE = 999;
    private ArrayList<point> points;

    private boolean permissions_granted;
    private final static String LOGTAG =
            MainActivity.class.getSimpleName();

    private float theDistance(){
        Location location1 = new Location("point A");
        location1.setLatitude(old_lat);
        location1.setLongitude(old_lon);

        Location location2 = new Location("point B");
        location2.setLatitude(curr_lat);
        location2.setLongitude(curr_lon);

        float distance = location1.distanceTo(location2);
        return distance;
    }



    private  Float getVelocity(){

        Float d = theDistance();
        System.out.println( "the Distance is: "+ d);
        System.out.println("the change in time is: "+ (curr_time-old_time));
        float vel = d/curr_time-old_time;
        return vel;
    }


    private Double getDistance(){
        double x1 = Math.toRadians(old_lat);
        double y1 = Math.toRadians(old_lon);

        double x2 = Math.toRadians(curr_lat);
        double y2 = Math.toRadians(curr_lon);

        double r =6371000;

        double a = 1- Math.cos(x2-x1)/2;
        double b = Math.cos(x2)*Math.cos(x1);
        double c = 1- Math.cos(y2-y1)/2;
        double h = a+b*c;
        double d = 2*r * Math.asin(Math.sqrt(h));
        return d;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tv_lat = findViewById(R.id.tv_lat);
        this.tv_lon = findViewById(R.id.tv_lon);
        this.tv_velo =findViewById(R.id.tv_velocity);
        this.location_text = findViewById(R.id.buttonData);
        this.d_box = findViewById(R.id.distance_box);
        this.scroll_View = findViewById(R.id.scroll_view);
        this.table = findViewById(R.id.tableLayout);
        this.points = new ArrayList<point>();
        this.index = 1;
        this.curr_lat= 0.0;
        this.curr_lon = 0.0;

        //add point button click listener
        this.new_point = findViewById(R.id.new_point);
        new_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Button aButton = new Button(MainActivity.this);
                aButton.setText("Location "+ index);
                final point aPoint = new point(old_lat,old_lon,old_time);
                points.add(aPoint);
                index++;
                table.addView(aButton);
                aButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index1 = table.indexOfChild(aButton);
                        System.out.println(index1);
                         point apoint = points.get(index1-1);
                         location_text.setText(Double.toString(aPoint.getLatitude())+ " "+Double.toString(aPoint.getLongitude())+ " "+ Long.toString(aPoint.getTime()) );
                    }
                });

            }
        });



        if (handler == null) {
            this.handler = new LocationHandler(this);
            this.handler.addObserver(this);
        }


        // check permissions
        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    public boolean isPermissions_granted() {
        return permissions_granted;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // we have only asked for FINE LOCATION
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.permissions_granted = true;
                Log.i(LOGTAG, "Fine location permission granted.");
            } else {
                this.permissions_granted = false;
                Log.i(LOGTAG, "Fine location permission not granted.");
            }
        }

    }

    @Override
    public void update(Observable observable,
                       Object o) {
        if (observable instanceof LocationHandler) {
            Location l = (Location) o;
            final double lat = l.getLatitude();
            final double lon = l.getLongitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_lat.setText(Double.toString(lat));
                    tv_lon.setText(Double.toString(lon));
                    //save values before update
                    if(old_lat != null && old_lat != 0) {
                        old_lat = curr_lat;
                        old_lon = curr_lon;
                        old_time = curr_time;

                        curr_lon = lon;
                        curr_lat = lat;
                        curr_time = Calendar.getInstance().getTimeInMillis()/1000;

                        tv_velo.setText(Float.toString(getVelocity()));
                    }else{
                        old_lon = lon;
                        old_lat = lat;
                        old_time = Calendar.getInstance().getTimeInMillis()/1000;
                    }
                }
            });

        }
    }
}