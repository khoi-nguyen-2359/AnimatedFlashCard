package khoinguyen.sample.animflashcard;

import android.animation.AnimatorSet;

public abstract class AnimationWrapper {
	protected AnimatorSet mAnimatorSet;
	public AnimationWrapper() {
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
