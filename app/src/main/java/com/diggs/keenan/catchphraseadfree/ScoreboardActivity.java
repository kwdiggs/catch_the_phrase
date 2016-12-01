package com.diggs.keenan.catchphraseadfree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScoreboardActivity extends AppCompatActivity {
    // determine if user is playing a practice round
    private boolean isPracticeRound;

    // scoring button
    private Button team1;
    private Button team2;

    // current scores
    private int teamOneScore;
    private int teamTwoScore;

    // score goal
    private final int GOAL = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        team1 = (Button) findViewById(R.id.team_one);
        team2 = (Button) findViewById(R.id.team_two);

        if (!isPracticeRound) {
            teamOneScore = 0;
            teamTwoScore = 0;

            setButtonListener(team1);
            setButtonListener(team2);
        } else {
            setButtonListenerPractice(team1);
            setButtonListenerPractice(team1);
        }
    }

    // send control flow back to main menu after button touch
    private void setButtonListenerPractice(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScoreboardActivity.this, getString(R.string.practice_over),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ScoreboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // if there is no victor, send control flow back to GamePlay for the next round
    // otherwise, send flow to victory fanfare
    private void setButtonListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getId() == R.id.team_one && teamOneScore + 1 == GOAL) {
                    startActivity(new Intent(ScoreboardActivity.this, MainActivity.class));
                } else if (button.getId() == R.id.team_one) {
                    Toast.makeText(ScoreboardActivity.this, "Team 1 has " + teamOneScore +
                            " points.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ScoreboardActivity.this, GameplayActivity.class));
                } else if (button.getId() == R.id.team_two && teamTwoScore +1 == GOAL) {
                    startActivity(new Intent(ScoreboardActivity.this, MainActivity.class));
                } else if (button.getId() == R.id.team_two) {
                    Toast.makeText(ScoreboardActivity.this, "Team 2 has " + teamTwoScore +
                            " points.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ScoreboardActivity.this, GameplayActivity.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreenHelper.goFullscreen(this);
        isPracticeRound = getIntent().getBooleanExtra("practice_round", false);
    }
}
