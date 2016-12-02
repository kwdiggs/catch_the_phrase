package com.diggs.keenan.catchphraseadfree;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class FanfareActivity extends AppCompatActivity {
    private TextView gameResult;
    private MediaPlayer fanfare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanfare);


        gameResult = (TextView)findViewById(R.id.game_result);
        String result = getIntent().getStringExtra("winner");
        Log.d("s", "MADE IT TO FANFARE ACTIVITY, result is " + result);
        if (result.equals("team_one")) {
            gameResult.setText(R.string.team_one_wins);
        } else if (result.equals("team_two")) {
            gameResult.setText(R.string.team_two_wins);
        }

        gameResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        if (fanfare != null) {
//
//        }
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
