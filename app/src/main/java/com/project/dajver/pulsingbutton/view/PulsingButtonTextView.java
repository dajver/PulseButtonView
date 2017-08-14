package com.project.dajver.pulsingbutton.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by gleb on 8/15/17.
 */

public class PulsingButtonTextView extends android.support.v7.widget.AppCompatTextView {

    public PulsingButtonTextView(Context context) {
        super(context);
    }

    public PulsingButtonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PulsingButtonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Animator getAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(getTextAnimator(), getRefreshAnimator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setScaleX(1.0f);
                setScaleY(1.0f);
            }
        });
        return animatorSet;
    }

    private Animator getTextAnimator() {
        ValueAnimator solidShrink = ValueAnimator.ofFloat(1.0f, 0.96f).setDuration(250);
        solidShrink.addUpdateListener(mTextAnimatorUpdateListener);
        ValueAnimator solidGrow = ValueAnimator.ofFloat(0.96f, 1.0f).setDuration(450);
        solidGrow.addUpdateListener(mTextAnimatorUpdateListener);
        AnimatorSet solidAnimation = new AnimatorSet();
        solidAnimation.playSequentially(solidShrink, solidGrow);
        return solidAnimation;
    }

    private ValueAnimator.AnimatorUpdateListener mTextAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            setScaleX((float) valueAnimator.getAnimatedValue());
            setScaleY((float) valueAnimator.getAnimatedValue());
        }
    };

    private Animator getRefreshAnimator() {
        ValueAnimator refreshAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(1200);
        refreshAnimator.addUpdateListener(mRefreshUpdateListener);
        return refreshAnimator;
    }

    private ValueAnimator.AnimatorUpdateListener mRefreshUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            invalidate();
        }
    };
}