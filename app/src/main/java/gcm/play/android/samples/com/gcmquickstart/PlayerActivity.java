package gcm.play.android.samples.com.gcmquickstart;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import utility.Constants;
import utility.GCMUpstream;
import utility.GcmSender;


public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlaybackEventListener {


    private YouTubePlayerView playerView;
    private YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        playerView.initialize(Constants.APIKey.YOUTUBE_API_KEY, this);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean restored) {
        if (!restored) {
            player = youTubePlayer;
            player.loadVideo(getIntent().getStringExtra(Constants.IntentKey.MESSAGE_VIDEO_URL));
            player.setPlaybackEventListener(this);

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, getString(R.string.youtube_init_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPlaying() {

    }

    @Override
    public void onPaused() {
        int id = player.getCurrentTimeMillis()+60000;
        new AsyncTask<String, Void, String>(){
            InputStream is = null;
            @Override
            protected String doInBackground(String...params){
                try {
                    Log.i("Filter", params[0]);
                    URL url = new URL("http://f600a094.ngrok.io/pause/"+params[0]+"/<groupId>");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    int response = conn.getResponseCode();
                    Log.d("Filter", "The response is: " + response);
                    is = conn.getInputStream();

                    // Convert the InputStream into a string
                    String contentAsString = readIt(is, 500);
                    Log.d("Filter", contentAsString);
                    return contentAsString;

                    // Makes sure that the InputStream is closed after the app is
                    // finished using it.
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return "";
            }
        }.execute(id+"");
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
    @Override
    public void onStopped() {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {
    }
}
