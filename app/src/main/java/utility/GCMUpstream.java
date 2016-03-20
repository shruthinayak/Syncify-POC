package utility;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by shruthi on 19/3/16.
 */
public class GCMUpstream extends AsyncTask<Bundle, Void, String> {
    Context ctx;
    GoogleCloudMessaging gcm;
    final static String senderId = "373521329424";
    final static String msgId = "100";
    final static long ttl = 100;
    private String tag = "Filter";


    public GCMUpstream(Context ctx) {
        this.ctx = ctx;
        gcm = GoogleCloudMessaging.getInstance(this.ctx);
    }

    @Override
    protected String doInBackground(Bundle... params) {

        try {
            gcm.send(senderId + "@gcm.googleapis.com", msgId,ttl, params[0]);
        } catch (NumberFormatException ex) {

            Log.e(tag, "Error sending upstream message: could not parse ttl " + ex.getLocalizedMessage());
            return "Error sending upstream message: could not parse ttl";
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(tag, "Successfully sent upstream message " + params[0].getInt("time"));
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(ctx,
                    "send message failed: " + result,
                    Toast.LENGTH_LONG).show();
        }
    }
}
