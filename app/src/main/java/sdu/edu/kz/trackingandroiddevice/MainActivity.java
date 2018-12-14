package sdu.edu.kz.trackingandroiddevice;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.io.IOException;

/**
 * Created by Aibol Ongarov on 27.01.2015.
**/
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


@SuppressLint("NewApi")
public class MainActivity extends Activity {
    Context context;
    ProgressDialog pDialog;

    final String LOGIN_URL = "http://android-tracking-dv.site88.net/login.php";

    private static final String TAG_MESSAGE = "message";

    EditText loginET;
    EditText passwordET;
    Button enterButton ;

    SharedPreferences pref;

    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);


        String token =  pref.getString("authorized",null);
        if (token != null){
            Intent i = new Intent(context, SecondActivity.class);
            i.putExtra("token",token);
            finish();
            startActivity(i);
        }


        loginET = (EditText)findViewById(R.id.login);
        passwordET = (EditText)findViewById(R.id.password);
        enterButton = (Button)findViewById(R.id.enter);
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
    public void login(View v){
        if (isOnline()){
            new AttemptLogin().execute();
        }
    }

    public void registration(View v){
        Intent i = new Intent(context, Registration.class);
        startActivity(i);
    }
    class AttemptLogin extends AsyncTask<Void, Void, Void> {
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

            int success;
            String login = loginET.getText().toString();
            String password = passwordET.getText().toString();
            try {
                // Building Parameters
                List params1 = new ArrayList();
                params1.add(new BasicNameValuePair("login", login));
                params1.add(new BasicNameValuePair("password", md5(password)));
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params1);
                success = json.getInt("status");
                if (success == 0) {

                    String token = json.getString("token");

                    Editor editor = pref.edit();
                    editor.putString("authorized", token);
                    editor.commit();

                    Intent i = new Intent(context, SecondActivity.class);
                    i.putExtra("token",token);
                    finish();
                    startActivity(i);
                }else{
                }
            } catch (JSONException e) {
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

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(context, "Нет соединения с интернетом !", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
