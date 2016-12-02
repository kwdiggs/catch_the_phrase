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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


public class GameplayActivity extends AppCompatActivity {
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

    // team scores
    private int teamOneScore;
    private int teamTwoScore;

    // request codes
    private final int PRACTICE_ROUND = 1;
    private final int NORMAL_ROUND = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gameplay);
        mContentView = (TextView)findViewById(R.id.fullscreen_content);

        Intent intent = getIntent();
        teamOneScore = intent.getIntExtra("team_one_score", 0);
        teamTwoScore = intent.getIntExtra("team_two_score", 0);

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

        // assign practiceRound value and set listener for TextView
        isPracticeRound = getIntent().getBooleanExtra("practice_round", false);
        setScreenListener();
    }

    // retrieve the next word from the list
    private String getNextWord() {
        currentWordIndex = (currentWordIndex >= wordList.size())? 0 : currentWordIndex;
        return wordList.get(currentWordIndex++);
    }

    // the number of the word in the list
    private int getCurrentIndex() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int currentIndex = preferences.getInt("CurrentIndex", 0);
        return currentIndex;
    }

    // create boopers and buzzer
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

    // play the boopers or buzzer in the appropriate sequence
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
            releaseMediaPlayer(buzzer);
            Intent intent = new Intent(this, ScoreboardActivity.class);
            if (isPracticeRound) {
                Log.d("hello", "practice round");
                intent.putExtra("practice_round", true);
                startActivityForResult(intent, PRACTICE_ROUND);
            } else {
                Log.d("hello", "normal round");
                intent.putExtra("team_one_score", teamOneScore);
                intent.putExtra("team_two_score", teamTwoScore);
                startActivityForResult(intent, NORMAL_ROUND);
            }
        }
    }

    // helper method: abstract away null check for start
    private void startMediaPlayer(MediaPlayer player) {
        if (player != null) {
            player.start();
        }
        if (player == buzzer) {
            mContentView.setEnabled(false);
        }
    }

    // helper method: abstract null check and call to release
    private void releaseMediaPlayer(MediaPlayer player) {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PRACTICE_ROUND && resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.practice_over, Toast.LENGTH_SHORT).show();
            finish();
        } else if (requestCode == NORMAL_ROUND && resultCode == RESULT_OK) {
            teamOneScore = data.getIntExtra("team_one_score", 0);
            teamTwoScore = data.getIntExtra("team_two_score", 0);
            String scores = "Team one score: " + teamOneScore + "\n";
            scores += "Team two score: " + teamTwoScore;
            Toast.makeText(this, scores, Toast.LENGTH_LONG).show();
        } else if (requestCode == NORMAL_ROUND && resultCode == RESULT_FIRST_USER) {
            Intent intent = new Intent();
            intent.putExtra("winner", data.getStringExtra("winner"));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Log.d("hello", "QUIT WITH ERROR");
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("CurrentIndex", currentWordIndex).apply();

        booperHandler.removeCallbacks(runny);
        releaseMediaPlayer(slowBooper);
        releaseMediaPlayer(medBooper);
        releaseMediaPlayer(fastBooper);
        releaseMediaPlayer(buzzer);
    }


    @Override
    protected void onResume() {
        super.onResume();
        FullScreenHelper.goFullscreen(this);

        // renable touches and prep default clue
        mContentView.setEnabled(true);
        if (isPracticeRound) {
            mContentView.setText(R.string.practice_clue);
        } else if (teamOneScore == teamTwoScore && teamTwoScore == 0) {
            mContentView.setText(R.string.default_clue);
        } else {
            mContentView.setText(R.string.continue_clue);
        }

        // reset play() variables
        boopCounter = 0;
        isPlaying = false;
        createMediaPlayers();

        // remember position in word list
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        currentWordIndex = preferences.getInt("CurrentIndex", 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
