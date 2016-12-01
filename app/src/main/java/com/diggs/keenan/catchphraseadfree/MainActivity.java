package com.diggs.keenan.catchphraseadfree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // visibility flags
    public static final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goFullscreen();
        setContentView(R.layout.activity_main);
    }

    public void startPracticeRound(View view) {
        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("practice_round", true);
        startActivity(intent);
    }

    public void startNewGame(View view) {
        Intent intent = new Intent(this, GameplayActivity.class);
        startActivity(intent);
    }

    public void showInstructions(View view) {
        Intent intent = new Intent(this, InstructionActivity.class);
        startActivity(intent);
    }

    public void testScoreboard (View view) {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);
    }

    // helper method: apply fullscreen flags to window
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
}
