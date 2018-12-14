package sdu.edu.kz.trackingandroiddevice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 07.02.2015.
 */
public class Registration extends Activity {
    EditText loginReg, passwordReg;
    Button registr;

    Context context;
    ProgressDialog pDialog;
    SharedPreferences pref;

    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "694270800752";
    final String LOGIN_URL = "http://android-tracking-dv.site88.net/register.php";
    JSONParser jsonParser = new JSONParser();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        context = this;
        loginReg = (EditText) findViewById(R.id.loginReg);
        passwordReg = (EditText) findViewById(R.id.passwordReg);

    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void registration(View v) {
        if (isOnline()) {
            new AttemptRegister().execute();
        }
    }


    class AttemptRegister extends AsyncTask<Void, Void, Void> {
        boolean failure = false;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Загрузка ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... params) {
            String regid = null;
            int success;
            String login = loginReg.getText().toString();
            String password = passwordReg.getText().toString();
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
            try {
                 regid = gcm.register(PROJECT_NUMBER);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                // Building Parameters
                List params1 = new ArrayList();
                params1.add(new BasicNameValuePair("login", login));
                params1.add(new BasicNameValuePair("password", md5(password)));
                params1.add(new BasicNameValuePair("reg_id", regid));
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params1);
                success = json.getInt("status");
                if (success == 0) {

                    Intent i = new Intent(context, SecondActivity.class);
                    finish();
                    startActivity(i);
                } else {
                }
            } catch (
                    JSONException e
                    )

            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // dismiss the dialog once product deleted
            super.onPostExecute(result);
            pDialog.dismiss();

        }

    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(context, "Нет соединения с интернетом !", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
