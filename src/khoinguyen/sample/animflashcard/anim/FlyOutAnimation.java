package khoinguyen.sample.animflashcard.anim;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Fly-out animation for StackView item.
 * @author khoi2359
 *
 */
public class FlyOutAnimation extends CustomAnimation {

	/**
	 * One animator listener is used for 2 animator (x and y) so flagging the state of whole animation
	 */
	public static int STATE_FLY_OUT_UNINIT = -1;
	public static int STATE_FLY_OUT_START = 0;
	public static int STATE_FLY_OUT_END = 1;
	public static int STATE_FLY_OUT_CANCEL = 2;

	public interface OnFlyOutListener {
		/**
		 * Should be called when the fly-out animation ends.
		 * @param flyView the View which just ended the animation
		 */
		void onFlyOutEnd(View flyView);
	}

	private ObjectAnimator mFlyOutXAnim;
	private ObjectAnimator mFlyOutYAnim;
	
	// state of whole animation
	private int mAnimState = STATE_FLY_OUT_UNINIT;
	
	public FlyOutAnimation(final View view, final OnFlyOutListener flyOutListener) {
		super();

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
				if (mAnimState == STATE_FLY_OUT_END
						|| mAnimState == STATE_FLY_OUT_CANCEL)
					return;
				mAnimState = STATE_FLY_OUT_END;

				if (flyOutListener != null)
					flyOutListener.onFlyOutEnd(view);

				mAnimatorSet.cancel();
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
