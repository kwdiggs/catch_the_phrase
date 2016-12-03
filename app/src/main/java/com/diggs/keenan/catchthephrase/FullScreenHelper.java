package com.diggs.keenan.catchthephrase;

import android.app.Activity;
import android.view.View;

public final class FullScreenHelper {
    // define visibility flags
    public static final int FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    // apply visibility flags to activity
    public static void goFullscreen(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(FullScreenHelper.FLAGS);

        // Continue hiding Nav bar even if a volume change triggers the system UI
        final View decorView = activity.getWindow().getDecorView();
        decorView
            .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(FullScreenHelper.FLAGS);
                }
            }
        });
    }
}
