package khoinguyen.sample.animflashcard;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CardView extends FrameLayout {
	public static final int DISTANCE_FLING = 2000;
	public static final int[] COLOR_ID_ARRAY = { R.color.card_colors_01, R.color.card_colors_02,
			R.color.card_colors_03, R.color.card_colors_04, R.color.card_colors_05, R.color.card_colors_06,
			R.color.card_colors_07, R.color.card_colors_08 };
	private static final long DUR_FLY_IN = 700;

	private static int COLOR_INDEX = 0;

	public static int STATE_FLY_OUT_UNINIT = -1;
	public static int STATE_FLY_OUT_START = 0;
	public static int STATE_FLY_OUT_END = 1;
	public static int STATE_FLY_OUT_CANCEL = 2;

	public interface CardFlyOutListener {
		void onCardFlyOutEnd(CardView flyCard);

		void onCardFlyOutStart(CardView flyCard);
	}

	class FlyOutAnimListener implements AnimatorListener {
		@Override
		public void onAnimationCancel(Animator arg0) {
			mFlyOutAnimState = STATE_FLY_OUT_CANCEL;
		}

		/**
		 * Use this callback to handle when card flied out of parent
		 */
		@Override
		public void onAnimationEnd(Animator animation) {
			if (mFlyOutAnimState == STATE_FLY_OUT_END // 2 animations (fly out X
														// and Y) can go here,
														// just handle once
					|| mFlyOutAnimState == STATE_FLY_OUT_CANCEL // do not handle
																// for canceled
																// intentionally
			)
				return;
			mFlyOutAnimState = STATE_FLY_OUT_END;

			mFlyOutAnim.cancel();

			if (mOnFlyOutListener != null)
				mOnFlyOutListener.onCardFlyOutEnd(CardView.this);

			flyIn();
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationStart(Animator animation) {
			mFlyOutAnimState = STATE_FLY_OUT_START;
		}
	};

	class CardViewGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.d("2359", "onFling");
			Log.d("2359", String.format("vX=%f, vY=%f", velocityX, velocityY));

			mFlyOutAnim.cancel();

			float[] velocity = { velocityX, velocityY };
			transformVelocityByRotation(velocity);
			velocityX = velocity[0];
			velocityY = velocity[1];

			flyOut(velocityX, velocityY);

			if (mOnFlyOutListener != null)
				mOnFlyOutListener.onCardFlyOutStart(CardView.this);

			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	};

	private ImageView mCardBackground;
	private TextView mCardLabel;

	private GestureDetector mGestureDetector;

	private ViewGroup mParentView;
	private CardFlyOutListener mOnFlyOutListener;

	private int mFlyOutAnimState = STATE_FLY_OUT_UNINIT;

	private FlyOutAnimation mFlyOutAnim;
	private FlyInAnimation mFlyInAnim;

	public CardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initContentView(null);
	}

	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initContentView(null);
	}

	public CardView(Context context, ViewGroup parentView, CardFlyOutListener flyOutListener) {
		super(context);

		initContentView(parentView);
		initFlyOutAnim();
		initFlyInAnim();
		initGestureDetector(flyOutListener);
	}

	private void initGestureDetector(CardFlyOutListener flyOutListener) {
		mOnFlyOutListener = flyOutListener;
		mGestureDetector = new GestureDetector(getContext(), new CardViewGestureListener());
	}

	private void initFlyInAnim() {
		mFlyInAnim = new FlyInAnimation(this, DUR_FLY_IN);
	}

	private void initFlyOutAnim() {
		FlyOutAnimListener flyOutAnimListener = new FlyOutAnimListener();
		mFlyOutAnim = new FlyOutAnimation(this, flyOutAnimListener);
	}

	/**
	 * Because velocity got in onFling is on view's coordinate, so when card is
	 * rotated, transform the velocity to its parent's coordinate system
	 * 
	 * @param velocity
	 */
	private void transformVelocityByRotation(float[] velocity) {
		Matrix matrix = new Matrix();
		matrix.setRotate(getRotation());
		matrix.mapPoints(velocity);
	}
	
	private void flyIn() {
		mFlyInAnim.setValues(getX(), (mParentView.getWidth() - getWidth()) / 2, getY(),
				(mParentView.getHeight() - getHeight()) / 2);
		mFlyInAnim.start();
	}

	private void flyOut(float vX, float vY) {
		float parentW = mParentView.getWidth();
		float parentH = mParentView.getHeight();

		float desX, desY;
		float longestDim = getDiagonal();
		if (vX > 0)
			desX = parentW + longestDim / 2;
		else
			desX = -longestDim;
		if (vY > 0)
			desY = parentH + longestDim / 2;
		else
			desY = -longestDim;
		Log.d("2359", String.format("sX=%f, sY=%f", desX, desY));

		long durX = (long) (Math.abs((desX - getX()) / vX) * 1000);
		long durY = (long) (Math.abs((desY - getY()) / vY) * 1000);
		
		mFlyOutAnim.setValues(getX(), desX, durX, getY(), desY, durY);
		mFlyOutAnim.start();
	}

	/**
	 * Get view bound's diagonal length
	 * 
	 * @return
	 */
	private float getDiagonal() {
		return (float) Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight());
	}

	private void initContentView(ViewGroup parentView) {
		mParentView = parentView;

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_card, this);

		mCardBackground = (ImageView) findViewById(R.id.iv_card_background);
		mCardLabel = (TextView) findViewById(R.id.tv_card_label);

		mCardBackground.setBackgroundResource(COLOR_ID_ARRAY[COLOR_INDEX = ++COLOR_INDEX % COLOR_ID_ARRAY.length]);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	public void setCardLabel(String label) {
		mCardLabel.setText(label);
	}
}
