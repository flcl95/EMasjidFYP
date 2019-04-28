package com.fyp.emasjid;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class QuranActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener{

    private ImageButton btn_play_pause;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private int mediaFileLength;
    private int realTimeLength;
    final Handler handler = new Handler();

    String url = "https://server8.mp3quran.net/afs/001.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Al-Quran Stream");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setMax(99);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mediaPlayer.isPlaying())
                {
                    SeekBar seekBar = (SeekBar)v;
                    int playPosition = (mediaFileLength/100)*seekBar.getProgress();
                    mediaPlayer.seekTo(playPosition);
                }
                return false;
            }
        });

        btn_play_pause = (ImageButton) findViewById(R.id.btn_play_pause);

        final String[] links = getResources().getStringArray(R.array.links);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.surah));
        ListView listView = (ListView) findViewById(R.id.window_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ProgressDialog mDialog = new ProgressDialog(QuranActivity.this);



                url=links[position];
                mediaPlayer.reset();
                mediaPlayer.stop();
                try{
                    mDialog.setMessage("Please Wait");
                    mDialog.show();
                    mediaPlayer.setDataSource(links[position]);
                    mediaPlayer.prepare();
                }
                catch (Exception ex){

                }
                mDialog.dismiss();
                mediaFileLength = mediaPlayer.getDuration();
                realTimeLength = mediaFileLength;
                updateSeekBar();
                mediaPlayer.start();
                //btn_play_pause.setImageResource(R.drawable.ic_pause_black_24dp);

            }
        });

        btn_play_pause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v){

                AsyncTask<String,String,String> mp3Play = new AsyncTask<String, String, String>() {

                    @Override
                    protected void onPreExecute(){

                    }

                    @Override
                    protected String doInBackground(String... params) {
                        /*try{
                            mediaPlayer.setDataSource(params[0]);
                            mediaPlayer.prepare();
                        }
                        catch (Exception ex){
                        }*/
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s){
                        mediaFileLength = mediaPlayer.getDuration();
                        realTimeLength = mediaFileLength;
                        if(!mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                            btn_play_pause.setImageResource(R.drawable.ic_pause_black_24dp);
                        }
                        else{
                            mediaPlayer.pause();
                            btn_play_pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        }

                        updateSeekBar();

                    }
                };

                mp3Play.execute(url);

            }
        });



    }

    private void updateSeekBar() {
        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / mediaFileLength)*100));
        if(mediaPlayer.isPlaying())
        {
            Runnable updater = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                    realTimeLength-=1000; // declare 1 second

                }

            };
            handler.postDelayed(updater,1000); // 1 second
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btn_play_pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);

    }

    @Override
    public void onBackPressed(){
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();
        finish();
    }
}
