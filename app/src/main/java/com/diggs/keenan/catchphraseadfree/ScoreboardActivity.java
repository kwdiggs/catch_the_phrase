package com.diggs.keenan.catchphraseadfree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScoreboardActivity extends AppCompatActivity {

    // visibility flags
    private final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

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

    // shared preferences
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        isPracticeRound = getIntent().getBooleanExtra("practice_round", false);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        teamOneScore = preferences.getInt("team_one_score", 0);
        teamTwoScore = preferences.getInt("team_two_score", 0);
        editor = preferences.edit();

        team1 = (Button)findViewById(R.id.team_one);
        team2 = (Button)findViewById(R.id.team_two);

        setButtonListener(team1);
        setButtonListener(team2);

        setContentView(R.layout.activity_scoreboard);
    }

    private void setButtonListener(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPracticeRound) {
                    Toast.makeText(ScoreboardActivity.this, getString(R.string.practice_over),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ScoreboardActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                if (button.getId() == R.id.team_one) {
                    if (teamOneScore+1 == GOAL) {
                        editor.putInt("team_one_score", 0);
                        editor.commit();
                        Intent intent = new Intent(ScoreboardActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        editor.putInt("team_one_score", ++teamOneScore);
                        editor.commit();
                        Toast.makeText(ScoreboardActivity.this, "Team 2 has " + teamTwoScore +
                                "points.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ScoreboardActivity.this, FullscreenActivity.class);
                        startActivity(intent);
                    }
                } else if (button.getId() == R.id.team_two) {
                    if (teamTwoScore+1 == GOAL) {
                        editor.putInt("team_two_score", 0);
                        editor.commit();
                        Intent intent = new Intent(ScoreboardActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        editor.putInt("team_two_score", ++teamTwoScore);
                        editor.commit();
                        Toast.makeText(ScoreboardActivity.this, "Team 2 has " + teamTwoScore +
                                "points.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ScoreboardActivity.this, FullscreenActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

    }

}
