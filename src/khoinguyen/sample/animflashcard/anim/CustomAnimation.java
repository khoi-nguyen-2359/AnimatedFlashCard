package khoinguyen.sample.animflashcard.anim;

import android.animation.AnimatorSet;

/**
 * Wrapper class to combine some animation into one to make a custom animation.
 * @author khoi2359
 *
 */
public abstract class CustomAnimation {
	protected AnimatorSet mAnimatorSet;
	public CustomAnimation() {
		mAnimatorSet = new AnimatorSet();
	}
	public AnimatorSet getAnimatorSet() {
		return mAnimatorSet;
	}
	public void start() {
		mAnimatorSet.start();
	}
	public void cancel() {
		mAnimatorSet.cancel();
	}
}
