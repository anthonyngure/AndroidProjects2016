package com.mustmobile;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity  implements Animation.AnimationListener{

    private static int SPLASH_TIME_OUT = 2000;
    private ImageView mImageView;
    private TranslateAnimation animationDown, animationUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mImageView = (ImageView) findViewById(R.id.activity_splash_screen_imageview);

        animationDown = new TranslateAnimation(0.0f, 0.0f, 0.0f, 50.0f);
        animationDown.setDuration(1000);
        animationDown.setRepeatCount(0);
        animationDown.setRepeatMode(Animation.RESTART);
        animationDown.setFillAfter(true);
        animationDown.setAnimationListener(this);
        mImageView.startAnimation(animationDown);

        animationUp = new TranslateAnimation(0.0f, 0.0f, 50.0f, 0.0f);
        animationUp.setDuration(1000);
        animationUp.setRepeatCount(0);
        animationUp.setRepeatMode(Animation.RESTART);
        animationUp.setFillAfter(true);
        animationUp.setAnimationListener(this);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                }, SPLASH_TIME_OUT
        );

    }

    /**
     * <p>Notifies the start of the animation.</p>
     *
     * @param animation The started animation.
     */
    @Override
    public void onAnimationStart(Animation animation) {

    }

    /**
     * <p>Notifies the end of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     *
     * @param animation The animation which reached its end.
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        mImageView.startAnimation(animationUp);
    }

    /**
     * <p>Notifies the repetition of the animation.</p>
     *
     * @param animation The animation which was repeated.
     */
    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
