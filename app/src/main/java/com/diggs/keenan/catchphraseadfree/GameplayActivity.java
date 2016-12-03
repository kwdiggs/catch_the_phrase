package com.diggs.keenan.catchphraseadfree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    // determine if user selected a practice round or a full game
    private boolean isPracticeRound;

    // plays the sounds
    private MediaPlayer slowTimer;
    private MediaPlayer medTimer;
    private MediaPlayer fastTimer;
    private MediaPlayer buzzer;

    // used to destroy the MediaPlayers
    private final int SLOW = 0;
    private final int MEDIUM = 1;
    private final int FAST = 2;
    private final int BUZZER = 3;

    // handles the sound makers
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            play();
        }
    };
    private final int[] durations = new int[4];
    private boolean isFirstTap = false;
    private int timerCount = 0;

    // request codes
    private final int PRACTICE_ROUND = 1;
    private final int NORMAL_ROUND = 2;

    // team scores
    private int teamOneScore;
    private int teamTwoScore;

    // how long (ms) to vibrate device when a round concludes
    private final int VIBRATE_DURATION = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // get the TexView
        mContentView = (TextView)findViewById(R.id.fullscreen_content);

        // get team scores
        Intent intent = getIntent();
        teamOneScore = intent.getIntExtra("team_one_score", 0);
        teamTwoScore = intent.getIntExtra("team_two_score", 0);

        // assign practiceRound value and set listener for TextView
        isPracticeRound = intent.getBooleanExtra("practice_round", false);
        setScreenListener();

        // put word list in ArrayList of Strings
        // do this once on creation
        wordList = new ArrayList<>();
        currentWordIndex = getCurrentIndex();

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
    }

    // start/quicken the timer or start the buzzer
    private void play() {
        int duration;
        if (timerCount < 4) {
            if (timerCount == SLOW) {
                startMediaPlayer(slowTimer);
            } else if (timerCount == MEDIUM)  {
                releaseMediaPlayer(slowTimer);
                startMediaPlayer(medTimer);
            } else if (timerCount == FAST) {
                releaseMediaPlayer(medTimer);
                startMediaPlayer(fastTimer);
            } else {
                releaseMediaPlayer(fastTimer);
                startMediaPlayer(buzzer);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VIBRATE_DURATION);
            }
            duration = durations[timerCount++];
            timerHandler.postDelayed(timerRunnable, duration);
        } else {
            releaseMediaPlayer(buzzer);
            Intent intent = new Intent(this, ScoreboardActivity.class);
            if (isPracticeRound) {
                intent.putExtra("practice_round", true);
                startActivityForResult(intent, PRACTICE_ROUND);
            } else {
                intent.putExtra("team_one_score", teamOneScore);
                intent.putExtra("team_two_score", teamTwoScore);
                startActivityForResult(intent, NORMAL_ROUND);
            }
        }
    }

    // retrieve game state from ScoreboardActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // vary behavior by result
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
            finish();
        }
    }

    // retrieve the next word from the list
    private String getNextWord() {
        currentWordIndex = (currentWordIndex >= wordList.size())? 0 : currentWordIndex;
        return wordList.get(currentWordIndex++);
    }

    // get the word number
    private int getCurrentIndex() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int currentIndex = preferences.getInt("CurrentIndex", 0);
        return currentIndex;
    }

    // get new word when screen is tapped
    private void setScreenListener() {
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContentView.setText(getNextWord());

                // set random timer durations on first touch
                if (!isFirstTap) {
                    isFirstTap = true;

                    Random r = new Random();
                    durations[SLOW] = (r.nextInt((35 - 25) + 1) + 25) * 1000;
                    durations[MEDIUM] = (r.nextInt((30 - 20) + 1) + 20) * 1000;
                    durations[FAST] = (r.nextInt((25 - 15) + 1) + 15) * 1000;
                    durations[BUZZER] = 4500;
                    play();
                }
            }
        });
    }

    // create timers and buzzer
    private void createMediaPlayers() {
        slowTimer = MediaPlayer.create(this, R.raw.slow_boop);
        slowTimer.setLooping(true);
        medTimer = MediaPlayer.create(this, R.raw.med_boop);
        medTimer.setLooping(true);
        fastTimer = MediaPlayer.create(this, R.raw.fast_boop);
        fastTimer.setLooping(true);
        buzzer = MediaPlayer.create(this, R.raw.timeup);
    }

    // abstract away null check and MediaPlayer.start()
    private void startMediaPlayer(MediaPlayer player) {
        if (player != null) {
            player.start();
        }
        if (player == buzzer) {
            mContentView.setEnabled(false);
        }
    }

    // abstract away null check and MediaPlayer.release()
    private void releaseMediaPlayer(MediaPlayer player) {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // record the current word index
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("CurrentIndex", currentWordIndex).apply();

        // release resources
        timerHandler.removeCallbacks(timerRunnable);
        releaseMediaPlayer(slowTimer);
        releaseMediaPlayer(medTimer);
        releaseMediaPlayer(fastTimer);
        releaseMediaPlayer(buzzer);
    }


    @Override
    protected void onResume() {
        super.onResume();
        FullScreenHelper.goFullscreen(this);

        // enable touches and display default clue
        mContentView.setEnabled(true);
        if (isPracticeRound) {
            mContentView.setText(R.string.practice_clue);
        } else if (teamOneScore == teamTwoScore && teamTwoScore == 0) {
            mContentView.setText(R.string.default_clue);
        } else {
            mContentView.setText(R.string.continue_clue);
        }

        // reset play() variables
        timerCount = 0;
        isFirstTap = false;
        createMediaPlayers();

        // remember position in word list
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        currentWordIndex = preferences.getInt("CurrentIndex", 0);
    }
}
