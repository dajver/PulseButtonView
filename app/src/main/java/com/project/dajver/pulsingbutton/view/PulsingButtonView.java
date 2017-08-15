package com.project.dajver.pulsingbutton.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.project.dajver.pulsingbutton.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gleb on 8/15/17.
 */

public class PulsingButtonView extends FrameLayout {

    @BindView(R.id.pulsing_background)
    PulsingButtonBackground mBackground;
    @BindView(R.id.pulsing_text)
    PulsingButtonTextView mPulsingText;

    private Animator mAnimator;

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
        }
    }

    @OnClick(R.id.pulsing_button)
    void onClickThis() {
        Toast.makeText(getContext(), "Button clicked", Toast.LENGTH_LONG).show();
    }
}