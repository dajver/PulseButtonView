package com.project.dajver.pulsingbutton.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.project.dajver.pulsingbutton.R;

/**
 * Created by gleb on 8/15/17.
 */

public class PulsingButtonBackground extends View {

    private Paint solidPaint;
    private Paint strokePaint;
    private Paint ripplePaint;

    private float solidMultiplier;
    private float strokeMultiplier;

    private float duration = 150;
    private int frameRate = 15;

    private float speed = 1;
    private float rippleRadius = 0;
    private float endRippleRadius = 0;
    private float rippleX = 0;
    private float rippleY = 0;
    private int width = 0;
    private int height = 0;
    private int touchAction;

    private Handler handler = new Handler();

    public PulsingButtonBackground(Context context) {
        super(context);
        initialize();
    }

    public PulsingButtonBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PulsingButtonBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        solidPaint = new Paint();
        solidPaint.setColor(getResources().getColor(R.color.colorPrimary));
        solidPaint.setAntiAlias(true);

        strokePaint = new Paint();
        strokePaint.setColor(getResources().getColor(R.color.colorPrimary));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(getResources().getDimension(R.dimen.ring_width));
        strokePaint.setAntiAlias(true);

        ripplePaint = new Paint();
        ripplePaint.setColor(Color.WHITE);
        ripplePaint.setAlpha(51);
        ripplePaint.setAntiAlias(true);

        resetAnimatedValues();
    }

    public Animator getAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(getSolidAnimator(), getStrokeAnimator(), getStrokeAlphaAnimator(), getRefreshAnimator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                resetAnimatedValues();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                resetAnimatedValues();
                invalidate();
            }
        });
        return animatorSet;
    }

    private void resetAnimatedValues() {
        solidMultiplier = 1.0f;
        strokeMultiplier = 1.0f;
        strokePaint.setAlpha(0);
    }

    private Animator getSolidAnimator() {
        ValueAnimator solidShrink = ValueAnimator.ofFloat(1.0f, 0.96f).setDuration(250);
        solidShrink.addUpdateListener(mSolidUpdateListener);
        ValueAnimator solidGrow = ValueAnimator.ofFloat(0.96f, 1.0f).setDuration(450);
        solidGrow.addUpdateListener(mSolidUpdateListener);
        solidGrow.setInterpolator(new DecelerateInterpolator());
        AnimatorSet solidAnimation = new AnimatorSet();
        solidAnimation.playSequentially(solidShrink, solidGrow);
        return solidAnimation;
    }

    private ValueAnimator.AnimatorUpdateListener mSolidUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            solidMultiplier = (float) valueAnimator.getAnimatedValue();
        }
    };

    private Animator getStrokeAnimator() {
        ValueAnimator strokeGrow = ValueAnimator.ofFloat(0.80f, 1.1f).setDuration(800);
        strokeGrow.addUpdateListener(mStrokeUpdateListener);
        strokeGrow.setInterpolator(new DecelerateInterpolator());
        strokeGrow.setStartDelay(250);
        return strokeGrow;
    }

    private ValueAnimator.AnimatorUpdateListener mStrokeUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            strokeMultiplier = (float) valueAnimator.getAnimatedValue();
        }
    };

    private Animator getStrokeAlphaAnimator() {
        ValueAnimator alphaAnimator = ValueAnimator.ofInt(255, 0).setDuration(650);
        alphaAnimator.addUpdateListener(mStrokeAlphaUpdateListener);
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                strokePaint.setAlpha(255);
            }
        });
        alphaAnimator.setStartDelay(500);
        return alphaAnimator;
    }

    private ValueAnimator.AnimatorUpdateListener mStrokeAlphaUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            strokePaint.setAlpha((int) valueAnimator.getAnimatedValue());
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

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        float halfWidth = width / 2.0f;
        float solidHalfWidth = halfWidth * solidMultiplier;
        float strokeHalfWidth = halfWidth * strokeMultiplier;

        int height = canvas.getHeight();
        float halfHeight = height / 2.0f;
        float solidHalfHeight = halfHeight * solidMultiplier;
        float strokeHalfHeight = halfHeight * strokeMultiplier;

        canvas.drawARGB(0, 0, 0, 0);

        double strokeRadius =  0.515 * Math.sqrt(strokeHalfWidth * strokeHalfWidth + strokeHalfHeight * strokeHalfHeight);
        canvas.drawCircle(halfWidth, halfHeight, (float) strokeRadius, strokePaint);

        double solidRadius =  0.5 * Math.sqrt(solidHalfWidth * solidHalfWidth + solidHalfHeight * solidHalfHeight);
        canvas.drawCircle(halfWidth, halfHeight, (float) solidRadius, solidPaint);

        if(rippleRadius > 0 && rippleRadius < endRippleRadius) {
            canvas.drawCircle(rippleX, rippleY, rippleRadius, ripplePaint);
            if(touchAction == MotionEvent.ACTION_UP) {
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        rippleX = event.getX();
        rippleY = event.getY();

        touchAction = event.getAction();
        switch(event.getAction()) {
            case MotionEvent.ACTION_UP: {
                getParent().requestDisallowInterceptTouchEvent(false);

                rippleRadius = 1;
                endRippleRadius = Math.max(Math.max(Math.max(width - rippleX, rippleX), rippleY), height - rippleY);
                speed = endRippleRadius / duration * frameRate;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(rippleRadius < endRippleRadius) {
                            rippleRadius += speed;
                            ripplePaint.setAlpha(90 - (int) (rippleRadius / endRippleRadius * 90));
                            handler.postDelayed(this, frameRate);
                        }
                    }
                }, frameRate);
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                rippleRadius = 0;
                if(rippleX < 0 || rippleX > width || rippleY < 0 || rippleY > height) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    touchAction = MotionEvent.ACTION_CANCEL;
                    break;
                } else {
                    invalidate();
                    return true;
                }
            }
        }
        invalidate();
        return false;
    }
}