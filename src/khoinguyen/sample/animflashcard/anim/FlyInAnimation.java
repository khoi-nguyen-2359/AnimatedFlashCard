package khoinguyen.sample.animflashcard.anim;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Fly-in animation for StackView item when it is thrown out of StackView's bound.
 * 
 * @author khoi2359
 *
 */
public class FlyInAnimation extends CustomAnimation {
	
	// Animator on x axis.
	private ObjectAnimator mFlyInXAnim;
	
	// Animator on y axis.
	private ObjectAnimator mFlyInYAnim;
	
	private ObjectAnimator mRotationAnim;

	public FlyInAnimation(View view, long duration) {
		super();

		mFlyInXAnim = ObjectAnimator.ofFloat(view, "x", 0);
		mFlyInYAnim = ObjectAnimator.ofFloat(view, "y", 0);
		mRotationAnim = ObjectAnimator.ofFloat(view, "rotation", 0);
		ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.25f, 1f);
		ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.25f, 1f);

		mAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		mAnimatorSet.setDuration(duration);
		mAnimatorSet.play(mFlyInXAnim).with(mFlyInYAnim).with(scaleUpX).with(scaleUpY);//.with(mRotationAnim);
	}

	public void setValues(float fromX, float toX, float fromY, float toY, float degFrom, float degTo) {
		mFlyInXAnim.setFloatValues(fromX, toX);
		mFlyInYAnim.setFloatValues(fromY, toY);
		mRotationAnim.setFloatValues(degFrom, degTo);
	}
}
