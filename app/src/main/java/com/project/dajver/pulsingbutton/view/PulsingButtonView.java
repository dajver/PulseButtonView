package com.project.dajver.pulsingbutton.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.project.dajver.pulsingbutton.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gleb on 8/15/17.
 */

public class PulsingButtonView extends FrameLayout {

    @BindView(R.id.pulsing_background)
    PulsingButtonBackground pulsingButtonBackground;
    @BindView(R.id.pulsing_text)
    PulsingButtonTextView pulsingButtonTextView;

    private Animator animator;
    private OnPulseButtonClickListener onPulseButtonClick;

    private boolean animationEnabled = true;
    private boolean alreadyAnimating;

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
        if (getVisibility() == VISIBLE && animationEnabled) {
            startAnimation();
        } else {
            cancelAnimation();
        }
    }

    private void cancelAnimation() {
        if (animator != null) {
            alreadyAnimating = false;
            animator.cancel();
            animator = null;
        }
    }

    private void startAnimation() {
        if (alreadyAnimating) {
            return;
        }
        alreadyAnimating = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(pulsingButtonBackground.getAnimator(), pulsingButtonTextView.getAnimator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (alreadyAnimating) {
                    alreadyAnimating = false;
                    updateAnimation();
                }
            }
        });
        animatorSet.start();
        animator = animatorSet;
    }

    @OnClick(R.id.pulsing_button)
     void onClickThis() {
        onPulseButtonClick.onPulseButtonClick();
    }

    public void setOnPulseButtonClick(OnPulseButtonClickListener onPulseButtonClick) {
        this.onPulseButtonClick = onPulseButtonClick;
    }

    public interface OnPulseButtonClickListener {
        void onPulseButtonClick();
    }
}