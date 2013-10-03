package khoinguyen.sample.animflashcard;

import java.util.ArrayList;
import java.util.List;

import khoinguyen.sample.animflashcard.anim.FlyInAnimation;
import khoinguyen.sample.animflashcard.anim.FlyOutAnimation;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;

public class StackAdapterView extends AdapterView<CardStackAdapter> implements FlyOutAnimation.OnFlyOutListener {
	public static final int SUM_DEGREE = 120;
	public static final long DUR_INTRO_ANIM = 1000;
	public static final int MAX_ROTATE_OFFSET = 10; // 10 degree rotation each
													// card
	public static final int DUR_NEXT_ANIM = 300;
	private static final long DUR_FLY_IN = 700;

	class OnItemGestureListener extends GestureDetector.SimpleOnGestureListener {
		private int mItemIndex;

		public OnItemGestureListener(int itemIndex) {
			this.mItemIndex = itemIndex;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.d("2359", "onFling");

			float[] velocity = { velocityX, velocityY };
			rotateVelocityVector(this.mItemIndex, velocity);
			velocityX = velocity[0];
			velocityY = velocity[1];

			flyOut(this.mItemIndex, velocityX, velocityY);

			shiftItemList(mItemIndex);

			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	};

	private CardStackAdapter mAdapter;
	private List<View> mItemList;
	private List<FlyOutAnimation> mFlyOutAnimList;
	private List<FlyInAnimation> mFlyInAnimList;
	private List<Integer> mIndexList;

	public StackAdapterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StackAdapterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StackAdapterView(Context context) {
		super(context);
	}

	@Override
	public CardStackAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(CardStackAdapter adapter) {
		mAdapter = adapter;
		mItemList = new ArrayList<View>();
		initIndexList();
		removeAllViewsInLayout();
		requestLayout();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mAdapter == null)
			return;

		if (getChildCount() == 0) {
			int position = 0;

			while (position < mAdapter.getCount()) {
				View newItem = mAdapter.getView(position, null, this);
				addAndMeasureItem(newItem);

				mItemList.add(newItem);

				position++;
			}

			initFlyInAnimList();
			initFlyOutAnimList();
			initItemGestureDetector();
		}

		layoutItems();
	}

	private void initIndexList() {
		mIndexList = new ArrayList<Integer>();
		for (int i = 0; i < mAdapter.getCount(); ++i)
			mIndexList.add(i);
	}

	private void initItemGestureDetector() {
		int nItem = mItemList.size();
		for (int i = 0; i < nItem; ++i) {
			final GestureDetector itemGestureDetector = new GestureDetector(getContext(), new OnItemGestureListener(i));
			mItemList.get(i).setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return itemGestureDetector.onTouchEvent(event);
				}
			});
		}
	}

	private void initFlyInAnimList() {
		mFlyInAnimList = new ArrayList<FlyInAnimation>();
		for (View item : mItemList) {
			FlyInAnimation flyInAnim = new FlyInAnimation(item, DUR_FLY_IN);
			mFlyInAnimList.add(flyInAnim);
		}
	}

	private void initFlyOutAnimList() {
		mFlyOutAnimList = new ArrayList<FlyOutAnimation>();
		for (View item : mItemList) {
			FlyOutAnimation flyOutAnim = new FlyOutAnimation(item, this);
			mFlyOutAnimList.add(flyOutAnim);
		}
	}

	/**
	 * Get view bound's diagonal length
	 * 
	 * @return
	 */
	private float getDiagonal(int itemIndex) {
		View item = mItemList.get(itemIndex);
		return (float) Math.sqrt(item.getWidth() * item.getWidth() + item.getHeight() * item.getHeight());
	}

	private void flyIn(int itemIndex) {
		FlyInAnimation flyIn = mFlyInAnimList.get(itemIndex);
		View item = mItemList.get(itemIndex);
		flyIn.setValues(item.getX(), (getWidth() - item.getWidth()) / 2, item.getY(),
				(getHeight() - item.getHeight()) / 2);
		flyIn.start();
	}

	private void flyOut(int itemIndex, float vX, float vY) {
		float desX, desY;
		float longestDim = getDiagonal(itemIndex);
		if (vX > 0)
			desX = getWidth() + longestDim / 2;
		else
			desX = -longestDim;
		if (vY > 0)
			desY = getHeight() + longestDim / 2;
		else
			desY = -longestDim;

		View item = mItemList.get(itemIndex);
		long durX = (long) (Math.abs((desX - item.getX()) / vX) * 1000);
		long durY = (long) (Math.abs((desY - item.getY()) / vY) * 1000);
		Log.d("2359", String.format("durX=%d, durY=%d", durX, durY));

		FlyOutAnimation flyOutAnim = mFlyOutAnimList.get(itemIndex);
		flyOutAnim.cancel();
		flyOutAnim.setValues(item.getX(), desX, durX, item.getY(), desY, durY);
		flyOutAnim.start();
	}

	private void layoutItems() {
		int nChild = mItemList.size();
		for (int index = 0; index < nChild; index++) {
			View item = mItemList.get(index);

			int width = item.getMeasuredWidth();
			int height = item.getMeasuredHeight();
			int left = (getWidth() - width) / 2;
			int top = (getHeight() - height) / 2;

			/* stack positions its children at center */
			item.layout(left, top, left + width, top + height);

			Log.d("2359", String.format("l=%d,t=%d,r=%d,b=%d", left, top, left + width, top + height));
		}
	}

	/**
	 * Because velocity got in onFling is on view's coordinate, so when card is
	 * rotated, transform the velocity to its parent's coordinate system
	 * 
	 * @param velocity
	 */
	private void rotateVelocityVector(int itemIndex, float[] velocity) {
		Matrix matrix = new Matrix();
		matrix.setRotate(mItemList.get(itemIndex).getRotation());
		matrix.mapPoints(velocity);
	}

	private void addAndMeasureItem(View child) {
		int measureW;
		int measureH;

		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			measureW = measureH = MeasureSpec.UNSPECIFIED;
		} else {
			measureW = MeasureSpec.EXACTLY | params.width;
			measureH = MeasureSpec.EXACTLY | params.height;
		}

		addViewInLayout(child, -1, params, true);
		child.measure(measureW, measureH);
	}

	public void animateIntro() {
		int nCard = mItemList.size();
		if (nCard == 0)
			return;

		float offsetRotate = SUM_DEGREE / nCard;
		for (int i = 0; i < nCard; ++i) {
			View item = mItemList.get(i);
			ObjectAnimator oa = ObjectAnimator.ofFloat(item, "rotation", 0, -offsetRotate * (nCard - i));
			oa.setInterpolator(new AccelerateInterpolator());
			oa.setDuration(DUR_INTRO_ANIM);
			oa.start();
		}
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setSelection(int arg0) {
		throw new UnsupportedOperationException("Not supported");
	}

	private void reorderZAxis() {
		Log.d("2359", "reorderZAxis");
		for (int i = 0; i < mIndexList.size(); ++i) {
			Log.d("2359", mIndexList.get(i) + "");
			mItemList.get(mIndexList.get(i)).bringToFront();
		}

		requestLayout();
		invalidate();
	}

	private void shiftItemList(int itemIndex) {
		mIndexList.remove(Integer.valueOf(itemIndex));
		mIndexList.add(0, itemIndex);
	}

	/**
	 * Animate after a card removed from stack
	 * 
	 * @param cardView
	 *            removed card
	 */
	private void animateShiftItem() {
		int nCard = mItemList.size();
		float offsetRotate = SUM_DEGREE / nCard;
		for (int i = 0; i < nCard; ++i) {
			View item = mItemList.get(mIndexList.get(i));
			ObjectAnimator oa = ObjectAnimator.ofFloat(item, "rotation", item.getRotation(), -offsetRotate
					* (nCard - i));
			oa.setInterpolator(new AccelerateDecelerateInterpolator());
			oa.setDuration(DUR_NEXT_ANIM);
			oa.start();
		}
	}

	@Override
	public void onFlyOutEnd(View flyView) {
		int itemIndex = mItemList.indexOf(flyView);
		flyIn(itemIndex);
		reorderZAxis();
		animateShiftItem();
	}
}
