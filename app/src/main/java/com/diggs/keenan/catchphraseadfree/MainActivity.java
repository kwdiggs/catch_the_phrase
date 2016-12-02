package com.diggs.keenan.catchphraseadfree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private final int START_REQUEST = 1; // request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreenHelper.goFullscreen(this);
        setContentView(R.layout.activity_main);
    }

    public void startPracticeRound(View view) {
        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("practice_round", true);
        startActivity(intent);
    }

    public void showInstructions(View view) {
        Intent intent = new Intent(this, InstructionActivity.class);
        startActivity(intent);
    }

    public void startNewGame(View view) {
        Intent intent = new Intent(this, GameplayActivity.class);
        startActivityForResult(intent, START_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // launch fanfare activity if the game is over
        if (requestCode == START_REQUEST && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, FanfareActivity.class);
            intent.putExtra("winner", data.getStringExtra("winner"));
            startActivity(intent);
        }
    }
}
