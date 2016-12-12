package com.diggs.keenan.catchthephrase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
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
import java.util.Collections;
import java.util.Random;

public class GameplayActivity extends AppCompatActivity {
    // file name
    private final String WORD_BANK = "word_bank.txt";

    // preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // holds the words
    private ArrayList<String> wordList;
    private int currentWordIndex;

    // displays the words
    private TextView wordView;

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

    // handles the MediaPlayers
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

    // team scores
    private int teamOneScore;
    private int teamTwoScore;

    // how long (ms) to vibrate device when a round concludes
    private final int VIBRATE_DURATION = 1200;

    // request code
    private final int REQUEST_SCOREBOARD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // get the TextView
        wordView = (TextView)findViewById(R.id.fullscreen_content);

        // get team scores
        Intent intent = getIntent();
        teamOneScore = intent.getIntExtra("team_one_score", 0);
        teamTwoScore = intent.getIntExtra("team_two_score", 0);

        // set listener for TextView
        setScreenListener();

        // put words in ArrayList of Strings
        wordList = new ArrayList<>();
        currentWordIndex = getCurrentIndex();

        // parameterize preferences and word list
        preferences = getSharedPreferences("categories", MODE_PRIVATE);
        populateList();
    }

    // create word file for gameplay from the word bank, based on set categories
    private void populateList() {
        BufferedReader reader;
        try {
            final InputStream file = getAssets().open(WORD_BANK);
            reader = new BufferedReader(new InputStreamReader(file));
            String line = "";

            // parse the word bank into sublists
            while (line != null) {
                if (line.equals("*")) {
                    String sublistLabel = reader.readLine();
                    boolean userSet = preferences.getBoolean(sublistLabel, true);

                    // if user set this sublist (category) for use, add its words
                    // otherwise skip this sublist entirely
                    line = reader.readLine();
                    if (userSet) {
                        while (line != null && !line.equals("*")) { // add
                            wordList.add(line);
                            line = reader.readLine();
                        }
                    } else {
                        while (line != null && !line.equals("*")) { // skip
                            line = reader.readLine();
                        }
                    }
                } else {
                    line = reader.readLine();
                }
            }

            // shuffle the list
            Collections.shuffle(wordList);
            currentWordIndex = 0;

        } catch (IOException ioe) {
            ioe.printStackTrace();
            onPause();
            finish();
        }
    }

    // start or quicken the timer, or start the buzzer
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
            intent.putExtra("team_one_score", teamOneScore);
            intent.putExtra("team_two_score", teamTwoScore);
            startActivityForResult(intent, REQUEST_SCOREBOARD);
        }
    }

    // retrieve game state from ScoreboardActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // vary behavior by resultCode
        if (resultCode == RESULT_OK) {
            teamOneScore = data.getIntExtra("team_one_score", 0);
            teamTwoScore = data.getIntExtra("team_two_score", 0);
            String scores = "Team one score: " + teamOneScore + "\n";
            scores += "Team two score: " + teamTwoScore;
            Toast.makeText(this, scores, Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_FIRST_USER) {
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
        wordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordView.setText(getNextWord());

                // set random timer durations on first touch
                if (!isFirstTap) {
                    isFirstTap = true;
                    Random r = new Random();
                    durations[SLOW] = (r.nextInt((35 - 25) + 1) + 25) * 1000;
                    durations[MEDIUM] = (r.nextInt((30 - 20) + 1) + 20) * 1000;
                    durations[FAST] = (r.nextInt((25 - 15) + 1) + 15) * 1000;
                    durations[SLOW] = 1000;
                    durations[MEDIUM] = 1000;
                    durations[FAST] = 1000;
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
            wordView.setEnabled(false);
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
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();
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
        wordView.setEnabled(true);
        if (teamOneScore == teamTwoScore && teamTwoScore == 0) {
            wordView.setText(R.string.default_clue);
        } else {
            wordView.setText(R.string.continue_clue);
        }

        // reset play() variables
        timerCount = 0;
        isFirstTap = false;
        createMediaPlayers();

        // remember position in word list
        preferences = getPreferences(MODE_PRIVATE);
        currentWordIndex = preferences.getInt("CurrentIndex", 0);
    }
}
