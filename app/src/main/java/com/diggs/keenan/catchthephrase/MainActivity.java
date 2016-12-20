package com.diggs.keenan.catchthephrase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // request code for new game
    private final int START_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isFirstTime()) {
            setPrefs();
        }
    }

    // launch SettingsActivity
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // launch InstructionActivity
    public void showInstructions(View view) {
        Intent intent = new Intent(this, InstructionActivity.class);
        startActivity(intent);
    }

    // launch GameplayActivity as a full game
    public void startNewGame(View view) {
        Intent intent = new Intent(this, GameplayActivity.class);
        startActivityForResult(intent, START_REQUEST);
    }

    // determine winner of full game and launch FanfareActivity
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

    @Override
    protected void onResume() {
        super.onResume();
        FullScreenHelper.goFullscreen(this);
    }

    // check if first time user is playing this app
    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean firstTime = preferences.getBoolean("FirstTime", false);

        if (!firstTime) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("FirstTime", true).apply();
        }
        return !firstTime;
    }

    // activate all categories
    private void setPrefs() {
        SharedPreferences preferences = getSharedPreferences("categories", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("sublist_people", true);
        editor.putBoolean("sublist_food", true);
        editor.putBoolean("sublist_animals", true);
        editor.putBoolean("sublist_household", true);
        editor.putBoolean("sublist_games", true);
        editor.putBoolean("sublist_tv", true);
        editor.putBoolean("sublist_words", true);
        editor.apply();
    }
}
