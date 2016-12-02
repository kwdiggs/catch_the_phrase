package com.diggs.keenan.catchphraseadfree;

import android.media.MediaPlayer;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class FanfareActivity extends AppCompatActivity {
    private TextView gameResultView;
    private MediaPlayer fanfare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanfare);

        // display winning team on screen
        gameResultView = (TextView)findViewById(R.id.game_result);
        String resultString = getIntent().getStringExtra("winner");
        if (resultString.equals("team_one")) {
            gameResultView.setText(R.string.team_one_wins);
        } else if (resultString.equals("team_two")) {
            gameResultView.setText(R.string.team_two_wins);
        }

        // play fanfare sound effect
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

    @Override
    protected void onPause() {
        super.onPause();

        // release media player
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
