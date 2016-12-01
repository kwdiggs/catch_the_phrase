package com.diggs.keenan.catchphraseadfree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


public class FullscreenActivity extends AppCompatActivity {
    // visibility flags
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    // holds the words
    private ArrayList<String> wordList;
    private int currentWordIndex;

    // displays the words
    private TextView mContentView;

    // determine if user decided for a practice round or a full game
    private boolean isPracticeRound;

    // makes the sounds
    private MediaPlayer slowBooper, medBooper, fastBooper, buzzer;

    // used to destroy the sound makers
    private final int SLOW = 0;
    private final int MEDIUM = 1;
    private final int FAST = 2;
    private final int BUZZER = 3;

    // handles the sound makers
    final Handler booperHandler = new Handler();
    final Runnable runny = new Runnable() {
        @Override
        public void run() {
            play();
        }
    };
    final int[] durations = new int[4];
    private boolean isPlaying = false;
    int boopCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goFullscreen();

        setContentView(R.layout.activity_fullscreen);
        mContentView = (TextView)findViewById(R.id.fullscreen_content);

        wordList = new ArrayList<>();
        currentWordIndex = getCurrentIndex();

        // put word list in ArrayList of Strings
        // do this once on creation
        BufferedReader reader;
        try{
            final InputStream file = getAssets().open("Words.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while(line != null){
                wordList.add(line);
                line = reader.readLine();
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }

        isPracticeRound = getIntent().getBooleanExtra("practice_round", false);
        setScreenListener();
    }

    private void createMediaPlayers() {
        slowBooper = MediaPlayer.create(this, R.raw.slow_boop);
        slowBooper.setLooping(true);

        medBooper = MediaPlayer.create(this, R.raw.med_boop);
        medBooper.setLooping(true);

        fastBooper = MediaPlayer.create(this, R.raw.fast_boop);
        fastBooper.setLooping(true);

        buzzer = MediaPlayer.create(this, R.raw.timeup);
    }

    // get new word when screen is tapped
    private void setScreenListener() {
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContentView.setText(getNextWord());

                if (!isPlaying) {
                    isPlaying = true;

                    Random r = new Random();
//                    durations[SLOW] = (r.nextInt((35 - 25) + 1) + 25) * 1000;
//                    durations[MEDIUM] = (r.nextInt((30 - 20) + 1) + 20) * 1000;
//                    durations[FAST] = (r.nextInt((25 - 15) + 1) + 15) * 1000;
                    durations[BUZZER] = 4500;

                    durations[SLOW] = 2000;
                    durations[MEDIUM] = 2000;
                    durations[FAST] = 2000;

                    play();
                }
            }
        });
    }

    private void play() {
        int duration;
        if (boopCounter < 4) {
            if (boopCounter == SLOW) {
                startMediaPlayer(slowBooper);
            } else if (boopCounter == MEDIUM)  {
                releaseMediaPlayer(slowBooper);
                startMediaPlayer(medBooper);
            } else if (boopCounter == FAST) {
                releaseMediaPlayer(medBooper);
                startMediaPlayer(fastBooper);
            } else {
                releaseMediaPlayer(fastBooper);
                startMediaPlayer(buzzer);
            }

            duration = durations[boopCounter++];
            booperHandler.postDelayed(runny, duration);

        } else {
            Intent intent = new Intent(this, ScoreboardActivity.class);
            if (isPracticeRound) {
                intent.putExtra("practice_round", true);
            }
            startActivity(intent);
        }
    }


    // abstract null check and call to release
    private void releaseMediaPlayer(MediaPlayer player) {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // abstract away null check for start
    private void startMediaPlayer(MediaPlayer player) {
        if (player != null) {
            player.start();
        }
        if (player == buzzer) {
            mContentView.setEnabled(false);
        }
    }

    private String getNextWord(){
        currentWordIndex = (currentWordIndex >= wordList.size())? 0 : currentWordIndex;
        return wordList.get(currentWordIndex++);
    }

    private int getCurrentIndex() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int currentIndex = preferences.getInt("CurrentIndex", 0);
        return currentIndex;
    }

    private void goFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Continue hiding Nav bar even if a volume change triggers the system UI
        final View decorView = getWindow().getDecorView();
        decorView
            .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
            {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        booperHandler.removeCallbacks(runny);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("CurrentIndex", currentWordIndex);
        editor.apply();

        releaseMediaPlayer(slowBooper);
        releaseMediaPlayer(medBooper);
        releaseMediaPlayer(fastBooper);
        releaseMediaPlayer(buzzer);
    }


    @Override
    protected void onResume() {
        super.onResume();
        goFullscreen();

        boopCounter = 0;
        isPlaying = false;

        // remember position in word list
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        currentWordIndex = preferences.getInt("CurrentIndex", 0);

        // reset all boopers
        createMediaPlayers();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
