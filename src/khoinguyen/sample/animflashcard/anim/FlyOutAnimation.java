package khoinguyen.sample.animflashcard.anim;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class FlyOutAnimation extends CustomAnimation {

	public static int STATE_FLY_OUT_UNINIT = -1;
	public static int STATE_FLY_OUT_START = 0;
	public static int STATE_FLY_OUT_END = 1;
	public static int STATE_FLY_OUT_CANCEL = 2;

	public interface OnFlyOutListener {
		void onFlyOutEnd(View flyView);
	}

	private ObjectAnimator mFlyOutXAnim;
	private ObjectAnimator mFlyOutYAnim;
	private View mTarget;
	private OnFlyOutListener mOnFlyOutListener;
	private int mAnimState = STATE_FLY_OUT_UNINIT;

	public FlyOutAnimation(View view, OnFlyOutListener flyOutListener) {
		super();

		mTarget = view;
		mOnFlyOutListener = flyOutListener;

		AnimatorListener animListener = new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				mAnimState = STATE_FLY_OUT_START;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (mAnimState == STATE_FLY_OUT_END)
					return;
				
				mAnimatorSet.cancel();

				if (mAnimState != STATE_FLY_OUT_CANCEL) {
					if (mOnFlyOutListener != null)
						mOnFlyOutListener.onFlyOutEnd(mTarget);
				}
				
				mAnimState = STATE_FLY_OUT_END;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mAnimState = STATE_FLY_OUT_CANCEL;
			}
		};

		mFlyOutXAnim = ObjectAnimator.ofFloat(view, "x", 0);
		mFlyOutXAnim.addListener(animListener);

		mFlyOutYAnim = ObjectAnimator.ofFloat(view, "y", 0);
		mFlyOutYAnim.addListener(animListener);

		mAnimatorSet.setInterpolator(new LinearInterpolator());
		mAnimatorSet.play(mFlyOutXAnim).with(mFlyOutYAnim);
	}

	public void setValues(float fromX, float toX, long durX, float fromY, float toY, long durY) {
		mFlyOutXAnim.setFloatValues(fromX, toX);
		mFlyOutXAnim.setDuration(durX);

		mFlyOutYAnim.setFloatValues(fromY, toY);
		mFlyOutYAnim.setDuration(durY);
	}
}
