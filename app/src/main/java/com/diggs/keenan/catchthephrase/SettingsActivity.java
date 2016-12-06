package com.diggs.keenan.catchthephrase;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onCheckboxClicked(View view) {
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();

        // is the box checked
        boolean checked = ((CheckBox) view).isChecked();

        // determine which box was selected
        switch(view.getId()) {
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
            case R.id.checkbox_words:
                if (checked)
                    editor.putBoolean("sublist_words", true).apply();
                else
                    editor.putBoolean("sublist_words", false).apply();
                break;
        }
    }
}
