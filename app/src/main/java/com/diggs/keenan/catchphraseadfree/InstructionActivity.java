package com.diggs.keenan.catchphraseadfree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreenHelper.goFullscreen(this);
        setContentView(R.layout.activity_instruction);
    }
}
