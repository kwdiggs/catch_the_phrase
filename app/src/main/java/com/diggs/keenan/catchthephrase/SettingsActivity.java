package com.diggs.keenan.catchthephrase;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    CheckBox people;
    CheckBox food;
    CheckBox animals;
    CheckBox household;
    CheckBox games;
    CheckBox tv;
    CheckBox words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        parameterizeCheckBoxes();
        setBoxPreferences();
    }

    public void onCheckboxClicked(View view) {
        preferences = getSharedPreferences("categories", MODE_PRIVATE);
        editor = preferences.edit();

        // is the box checked
        boolean checked = ((CheckBox) view).isChecked();

        // determine which box was selected
        switch (view.getId()) {
            case R.id.checkbox_people:
                if (checked)
                    editor.putBoolean("sublist_people", true).apply();
                else
                    editor.putBoolean("sublist_people", false).apply();
                break;
            case R.id.checkbox_food:
                if (checked)
                    editor.putBoolean("sublist_food", true).apply();
                else
                    editor.putBoolean("sublist_food", false).apply();
                break;
            case R.id.checkbox_animals:
                if (checked)
                    editor.putBoolean("sublist_animals", true).apply();
                else
                    editor.putBoolean("sublist_animals", false).apply();
                break;
            case R.id.checkbox_household:
                if (checked)
                    editor.putBoolean("sublist_household", true).apply();
                else
                    editor.putBoolean("sublist_household", false).apply();
                break;
            case R.id.checkbox_games:
                if (checked)
                    editor.putBoolean("sublist_games", true).apply();
                else
                    editor.putBoolean("sublist_games", false).apply();
                break;
            case R.id.tv:
                if (checked)
                    editor.putBoolean("sublist_tv", true).apply();
                else
                    editor.putBoolean("sublist_tv", false).apply();
                break;
            case R.id.checkbox_words:
                if (checked)
                    editor.putBoolean("sublist_words", true).apply();
                else
                    editor.putBoolean("sublist_words", false).apply();
                break;
        }
    }

    private void parameterizeCheckBoxes() {
        people = (CheckBox) findViewById(R.id.checkbox_people);
        food = (CheckBox) findViewById(R.id.checkbox_food);
        animals = (CheckBox) findViewById(R.id.checkbox_animals);
        household = (CheckBox) findViewById(R.id.checkbox_household);
        games = (CheckBox) findViewById(R.id.checkbox_games);
        tv = (CheckBox) findViewById(R.id.tv);
        words = (CheckBox) findViewById(R.id.checkbox_words);
    }

    private void setBoxPreferences() {
        preferences = getSharedPreferences("categories", MODE_PRIVATE);

        people.setChecked(preferences.getBoolean("sublist_people", false));
        food.setChecked(preferences.getBoolean("sublist_food", false));
        animals.setChecked(preferences.getBoolean("sublist_animals", false));
        household.setChecked(preferences.getBoolean("sublist_household", false));
        games.setChecked(preferences.getBoolean("sublist_games", false));
        tv.setChecked(preferences.getBoolean("sublist_tv", false));
        words.setChecked(preferences.getBoolean("sublist_words", false));
    }
}
