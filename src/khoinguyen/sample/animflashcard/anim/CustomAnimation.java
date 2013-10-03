package khoinguyen.sample.animflashcard.anim;

import android.animation.AnimatorSet;

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
