package com.project.dajver.pulsingbutton.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.project.dajver.pulsingbutton.R;

/**
 * Created by gleb on 8/15/17.
 */

public class PulsingButtonBackground extends View {

    private Paint mSolidPaint;
    private RectF mSolidRect;
    private float mSolidMultiplier;
    private float mClipMultiplier;

    private Paint mStrokePaint;
    private RectF mStrokeRect;
    private float mStrokeMultiplier;

    private Paint mWipePaint;

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
        mSolidPaint = new Paint();
        mSolidPaint.setColor(getResources().getColor(R.color.a_4));
        mSolidPaint.setAntiAlias(true);

        mStrokePaint = new Paint();
        mStrokePaint.setColor(getResources().getColor(R.color.a_4));
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(getResources().getDimension(R.dimen.ring_width));
        mStrokePaint.setAntiAlias(true);

        mWipePaint = new Paint();
        mWipePaint.setColor(Color.WHITE);
        mWipePaint.setAlpha(51);
        mWipePaint.setAntiAlias(true);

        mSolidRect = new RectF();
        mStrokeRect = new RectF();

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
        mSolidMultiplier = 1.0f;
        mStrokeMultiplier = 1.0f;
        mStrokePaint.setAlpha(0);
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
            mSolidMultiplier = (float) valueAnimator.getAnimatedValue();
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
            mStrokeMultiplier = (float) valueAnimator.getAnimatedValue();
        }
    };

    private Animator getStrokeAlphaAnimator() {
        ValueAnimator alphaAnimator = ValueAnimator.ofInt(255, 0).setDuration(650);
        alphaAnimator.addUpdateListener(mStrokeAlphaUpdateListener);
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mStrokePaint.setAlpha(255);
            }
        });
        alphaAnimator.setStartDelay(500);
        return alphaAnimator;
    }

    private ValueAnimator.AnimatorUpdateListener mStrokeAlphaUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mStrokePaint.setAlpha((int) valueAnimator.getAnimatedValue());
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
        float adjustedHalfWidth = halfWidth * 0.78260869565217f;
        float solidHalfWidth = adjustedHalfWidth * mSolidMultiplier;
        float strokeHalfWidth = adjustedHalfWidth * mStrokeMultiplier;
        float strokeDifference = strokeHalfWidth - solidHalfWidth;

        int height = canvas.getHeight();
        float halfHeight = height / 2.0f;
        float solidHalfHeight = halfHeight * mSolidMultiplier * 0.46808510638298f;
        float strokeHeightDifference = solidHalfHeight + strokeDifference;

        canvas.drawARGB(0, 0, 0, 0);

        mStrokeRect.set(halfWidth - strokeHalfWidth, halfHeight - strokeHeightDifference, halfWidth + strokeHalfWidth, halfHeight + strokeHeightDifference);
        canvas.drawRoundRect(mStrokeRect, strokeHeightDifference, strokeHeightDifference, mStrokePaint);

        mSolidRect.set(halfWidth - solidHalfWidth, halfHeight - solidHalfHeight, halfWidth + solidHalfWidth, halfHeight + solidHalfHeight);
        canvas.drawRoundRect(mSolidRect, solidHalfHeight, solidHalfHeight, mSolidPaint);

        float clipStart = halfWidth - solidHalfWidth;
        float clipEnd = clipStart + (solidHalfWidth * 2.0f) * mClipMultiplier;
        canvas.clipRect(clipStart, 0, clipEnd, height);
        canvas.drawRoundRect(mSolidRect, solidHalfHeight, solidHalfHeight, mWipePaint);
    }

    public Animator getWipeAnimator(long animationTimeMs) {
        ValueAnimator wipeAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(animationTimeMs);
        wipeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mClipMultiplier = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });
        wipeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mClipMultiplier = 0f;
                invalidate();
            }
        });
        return wipeAnimator;
    }
}