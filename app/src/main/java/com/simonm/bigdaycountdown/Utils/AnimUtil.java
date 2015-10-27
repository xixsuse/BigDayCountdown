package com.simonm.bigdaycountdown.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;


public class AnimUtil {

    // Creates the cross fade from the get started view to the main view
    public static void crossfade(final View view_to_disappear, View view_to_show, int time) {


        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        view_to_show.setAlpha(0f);
        view_to_show.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        view_to_show.animate()
                .alpha(1f)
                .setDuration(time)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        view_to_disappear.animate()
                .alpha(0f)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view_to_disappear.setVisibility(View.GONE);
                    }
                });
    }

}
