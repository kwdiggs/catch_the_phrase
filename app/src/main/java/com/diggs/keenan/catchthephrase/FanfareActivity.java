package com.diggs.keenan.catchthephrase;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FanfareActivity extends AppCompatActivity {
    // victory message "Team X Wins!"
    private TextView gameResultView;

    // MediaPlayer to play crowd cheering (fanfare) sound
    private MediaPlayer fanfare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanfare);

        // display message for winning team
        gameResultView = (TextView)findViewById(R.id.game_result);
        String resultString = getIntent().getStringExtra("winner");
        if (resultString.equals("team_one")) {
            gameResultView.setText(R.string.team_one_wins);
        } else if (resultString.equals("team_two")) {
            gameResultView.setText(R.string.team_two_wins);
        }

        // play fanfare sound
        fanfare = MediaPlayer.create(this, R.raw.victory);
        if (fanfare != null) {
            fanfare.start();
        }

        // press anywhere to destroy activity
        gameResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // release media player
    @Override
    protected void onPause() {
        super.onPause();
        if (fanfare != null) {
            fanfare.release();
            fanfare = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreenHelper.goFullscreen(this);
    }
}