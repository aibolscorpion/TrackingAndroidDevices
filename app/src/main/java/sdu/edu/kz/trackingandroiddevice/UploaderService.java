package sdu.edu.kz.trackingandroiddevice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

/**
 * Created by Aibol Ongarov on 05.04.2015.
 */
public class UploaderService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void sendPicture(String absFileName) {
        Log.w("sendPicture()", "started");
        String ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.w("sendPicture()", ANDROID_ID);
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(getString(R.string.C2DM_PHOTO_URI));
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("photo", new FileBody(new File(absFileName)));
            entity.addPart("deviceId", new StringBody(ANDROID_ID));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            // if result is OK
            if(response.getStatusLine().getStatusCode() == 200) {
                // delete images
                MyUtils.deleteFiles(FileType.IMAGE);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Log.w("sendPicture()", "ended");
        }
    }

}
