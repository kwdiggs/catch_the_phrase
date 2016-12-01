package com.diggs.keenan.catchphraseadfree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    private final int PRACTICE_SCORE = 1;
    private final int SCORE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        isPracticeRound = getIntent().getBooleanExtra("practice_round", false);

        team1 = (Button)findViewById(R.id.team_one);
        team2 = (Button)findViewById(R.id.team_two);

        if (!isPracticeRound) {
            teamOneScore = 0;
            teamTwoScore = 0;

            setButtonListener(team1);
            setButtonListener(team2);
        } else {
            setPracticeButtonListener(team1);
            setPracticeButtonListener(team2);
        }
    }

    // send control flow back to main menu after button touch
    private void setPracticeButtonListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScoreboardActivity.this, R.string.practice_over,Toast.LENGTH_SHORT)
                        .show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    // if there is no victor, send control flow back to GamePlay for the next round
    // otherwise, send flow to victory fanfare
    private void setButtonListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scores = "Team 1 score: " + teamOneScore + "\n";
                scores += "Team 2 score: " + teamTwoScore;

                if (button.getId() == R.id.team_one && teamOneScore + 1 == GOAL) {
                    // go to fanfare, celebrate team 1 win

                } else if (button.getId() == R.id.team_one) {
                    Toast.makeText(ScoreboardActivity.this, scores, Toast.LENGTH_LONG).show();
                } else if (button.getId() == R.id.team_two && teamTwoScore +1 == GOAL) {
                    // go to fanfare, celebrate team 2 win

                } else if (button.getId() == R.id.team_two) {
                    Toast.makeText(ScoreboardActivity.this, scores, Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreenHelper.goFullscreen(this);
    }
}
