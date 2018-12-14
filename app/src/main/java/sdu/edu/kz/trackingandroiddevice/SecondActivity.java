package sdu.edu.kz.trackingandroiddevice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.SslCertificate;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Date;


/**
 * Created by Aibol Ongarov on 22.01.2015.
 */
public class SecondActivity extends Activity{

    SharedPreferences pref;
    TextView TV;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
    }


    public void exit(View v){

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
    public void onClickCamera(View v){

        Intent i = new Intent(getApplicationContext(),CameraUp.class);
        startActivity(i);

    }
}
