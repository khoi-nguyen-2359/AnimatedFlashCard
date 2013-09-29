package khoinguyen.sample.animflashcard;

import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class FlyOutAnimation extends AnimationWrapper {
	private ObjectAnimator mFlyOutXAnim;
	private ObjectAnimator mFlyOutYAnim;

	public FlyOutAnimation(View view, AnimatorListener listener) {
		super();
		
		mFlyOutXAnim = ObjectAnimator.ofFloat(view, "x", 0);
		mFlyOutXAnim.addListener(listener);
		
		mFlyOutYAnim = ObjectAnimator.ofFloat(view, "y", 0);
		mFlyOutYAnim.addListener(listener);
		
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
