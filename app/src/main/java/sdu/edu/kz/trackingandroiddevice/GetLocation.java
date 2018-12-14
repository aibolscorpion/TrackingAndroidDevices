package sdu.edu.kz.trackingandroiddevice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: NMB
 * Date: 10.05.12
 * Time: 1:34
 * To change this template use File | Settings | File Templates.
 */
public class GetLocation extends Service implements LocationListener {

    private LocationListener listener = this;

    private static Location location;
    private static LocationManager locationManager;

    private static boolean gpsEnabled = false;
    private static boolean networkEnabled = false;

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;


    public Location getLocation() {
        //locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return this.location;
    }

    /*public void startListening() {
        //if(gpsEnabled) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //}
        //if(networkEnabled) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        //}
    }*/

    // should be called on location change
    public void sendLocation(Location location) {
        Log.w("sendLocation()", "started");
        Log.w("sendLocation()", "ended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w("onLocationChanged()", "started");
        if(location != null) {
            this.location = location;
            sendLocation(location);
            locationManager.removeUpdates(this);
            stopSelf();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        }
    }

    @Override
    public void onCreate() {
        Log.w("onCreate()", "started");
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        Log.w("onCreate()", "ended");
    }

    @Override
    public void onDestroy() {
        Log.w("onDestroy()", "started");
        locationManager.removeUpdates(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
