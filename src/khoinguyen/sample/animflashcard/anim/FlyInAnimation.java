package khoinguyen.sample.animflashcard.anim;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class FlyInAnimation extends CustomAnimation {
	private ObjectAnimator mFlyInXAnim;
	private ObjectAnimator mFlyInYAnim;

	public FlyInAnimation(View view, long duration) {
		super();

		mFlyInXAnim = ObjectAnimator.ofFloat(view, "x", 0);
		mFlyInYAnim = ObjectAnimator.ofFloat(view, "y", 0);
		ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.25f, 1f);
		ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.25f, 1f);

		mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		mAnimatorSet.setDuration(duration);
		mAnimatorSet.play(mFlyInXAnim).with(mFlyInYAnim).with(scaleUpX).with(scaleUpY);// .with(mRotateToBotAnim);
	}

	public void setValues(float fromX, float toX, float fromY, float toY) {
		mFlyInXAnim.setFloatValues(fromX, toX);
		mFlyInYAnim.setFloatValues(fromY, toY);
	}
}
