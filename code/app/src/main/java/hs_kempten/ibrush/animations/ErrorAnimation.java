package hs_kempten.ibrush.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import hs_kempten.ibrush.R;

/**
 * Created by Antoine Schmidt
 */
public class ErrorAnimation {

    /**
     * Animates the given view with a blink-animation.
     *
     * @param view the view to animate
     */
    public static void animate(View view) {
        // be sure to not have multiple animations running at the same time
        view.clearAnimation();

        // load the animation
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.blink);

        // play the animation
        view.startAnimation(animation);
    }

}
