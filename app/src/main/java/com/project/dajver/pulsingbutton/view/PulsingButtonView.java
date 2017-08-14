package com.project.dajver.pulsingbutton.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.project.dajver.pulsingbutton.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by gleb on 8/15/17.
 */

public class PulsingButtonView extends FrameLayout {

    public static final long HOLD_TIME_MS = 1800L;

    @BindView(R.id.pulsing_background)
    PulsingButtonBackground mBackground;

    @BindView(R.id.pulsing_text)
    PulsingButtonTextView mPulsingText;

    private Animator mAnimator;
    private Animator mLongPressAnimator;
    private Rect mBoundsRectangle;
    private View mClickedView;

    private OnClickListener mOnClickListener;
    private Handler mHandler;

    private boolean mAnimationEnabled = true;
    private boolean mAlreadyAnimating;

    public PulsingButtonView(Context context) {
        super(context);
        initialize();
    }

    public PulsingButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PulsingButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_pulsing_button, this);
        ButterKnife.bind(this);

        mBoundsRectangle = new Rect();
        mHandler = new Handler();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == VISIBLE) {
            startAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimation();
        super.onDetachedFromWindow();
    }

    @Override
    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            super.setVisibility(visibility);
            updateAnimation();
        }
    }

    private void updateAnimation() {
        if (getVisibility() == VISIBLE && mAnimationEnabled) {
            startAnimation();
        } else {
            cancelAnimation();
        }
    }

    private void cancelAnimation() {
        if (mAnimator != null) {
            mAlreadyAnimating = false;
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private void startAnimation() {
        if (mAlreadyAnimating) {
            return;
        }
        mAlreadyAnimating = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(mBackground.getAnimator(), mPulsingText.getAnimator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAlreadyAnimating) {
                    mAlreadyAnimating = false;
                    updateAnimation();
                }
            }
        });
        animatorSet.start();
        mAnimator = animatorSet;
    }

    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        if (!hasOnClickListeners()) {
            super.setOnClickListener(clickListener);
        } else {
            mOnClickListener = clickListener;
        }
    }

    @OnClick(R.id.hold_to_end_workout_button)
    void onClickThis() {
        // noop
    }

    private Runnable mOnLongPressed = new Runnable() {
        public void run() {
            if (mLongPressAnimator != null) {
                mLongPressAnimator.cancel();
            }
            if (mOnClickListener != null) {
                mOnClickListener.onClick(mClickedView);
            }
        }
    };

    @OnTouch(R.id.hold_to_end_workout_button)
    boolean onTouchPulsingButton(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mClickedView = view;
                mBoundsRectangle.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                mLongPressAnimator = mBackground.getWipeAnimator(HOLD_TIME_MS);
                mLongPressAnimator.start();
                mHandler.postDelayed(mOnLongPressed, HOLD_TIME_MS);

                break;
            case MotionEvent.ACTION_MOVE:
                if (mBoundsRectangle.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                    break;
                }
                // FALL THROUGH!
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                mHandler.removeCallbacks(mOnLongPressed);
                mLongPressAnimator.cancel();
                break;
        }
        return view.onTouchEvent(event);
    }

    public void setAnimated(boolean pulsing) {
        mAnimationEnabled = pulsing;
        updateAnimation();
    }
}