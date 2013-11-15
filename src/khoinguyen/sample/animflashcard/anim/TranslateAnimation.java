package khoinguyen.sample.animflashcard.anim;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class TranslateAnimation extends CustomAnimation {
    private ObjectAnimator mTranslateX;
    private ObjectAnimator mTranslateY;
    
    public TranslateAnimation(View view, float dX, float dY) {
        mTranslateX = ObjectAnimator.ofFloat(view, "x", view.getX() + dX);
        mTranslateY = ObjectAnimator.ofFloat(view, "y", view.getY() + dY);
        
        mAnimatorSet.setDuration(0);
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.play(mTranslateX).with(mTranslateY);
    }
}
