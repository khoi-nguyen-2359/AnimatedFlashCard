package khoinguyen.sample.animflashcard;

import java.util.ArrayList;
import java.util.List;

import khoinguyen.sample.animflashcard.adapter.StackAdapter;
import khoinguyen.sample.animflashcard.anim.FlyInAnimation;
import khoinguyen.sample.animflashcard.anim.FlyOutAnimation;
import khoinguyen.sample.animflashcard.anim.TranslateAnimation;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;

/**
 * This StackView is main class of the project. It's usage is implemented to be
 * the same as ListView. It can take in an adapter to support views, add those
 * views then measure and layout them. <br/>
 * StackView is not implementd scrolling to browse items, but it has animations
 * and guesture handling.<br/>
 * Overall:<br/>
 * - StackView add childs and position them at its center.<br/>
 * - Init guesture detector to handle fling gesture.<br/>
 * - Init fly-out, fly-in animations<br/>
 * - When an item receives fling geature, it flys out of StackView's bounds to
 * fling direction. Remaining items rotate to new position in the stack (each
 * item shifts up to one position)<br/>
 * - When an item is out of StackView bound, a listener is called back to
 * trigger fly-in animation on that item. Fly-in animation end when the item
 * reach center of StackView lying at the bottom of stack (behind all items)<br/>
 * <br/>
 * 
 * @author khoi2359
 * 
 */
public class StackView extends AdapterView<StackAdapter> implements FlyOutAnimation.OnFlyOutListener {

    // sum rotate degree of all items
    public static final int SUM_DEGREE = 120;

    // duration of shift item animation
    public static final long DUR_SHIFT_ANIM = 300;

    // duration of fly in animation
    public static final long DUR_FLY_IN = 700;

    // duration of intro animation
    public static final long DUR_INTRO_ANIM = 1000;

    class OnItemTouchListener implements OnTouchListener {
        private GestureDetector mItemGestureDetector;
        private int mItemIndex;
        public OnItemTouchListener(int itemIndex, GestureDetector itemGestureDetector) {
            mItemGestureDetector = itemGestureDetector;
            mItemIndex = itemIndex;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                View item = mItemList.get(mItemIndex);
//                item.setPivotX(item.getWidth()/2);
//                item.setPivotY(item.getHeight()/2);
//                item.setRotation(0)
            }
            return mItemGestureDetector.onTouchEvent(event);
        }

    }

    /**
     * Gesture detector to handle fling guesture for each item in stack.
     * 
     * @author anhkhoi
     */
    class OnItemGestureListener extends GestureDetector.SimpleOnGestureListener {
        private int mItemIndex;

        private float mLastScrollX = -1;
        private float mLastScrollY = -1;
        
        /**
         * @param itemIndex
         *            index position of the item which this listener is
         *            listening to.
         */
        public OnItemGestureListener(int itemIndex) {
            this.mItemIndex = itemIndex;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("khoinguyen", "onFling");
            
            float[] velocity = { velocityX, velocityY};
            rotateVector(this.mItemIndex, velocity);
            flyOut(this.mItemIndex, velocity[0], velocity[1]);
            shiftItemList(mItemIndex);

            mLastScrollX = mLastScrollY = -1;

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("khoinguyen", "onScroll");
            /* Calculate distance X and Y in screen's coordinate */
            if (mLastScrollX == -1 || mLastScrollY == -1) {
                mLastScrollX = e1.getRawX();
                mLastScrollY = e1.getRawY();
            }
            
            float diffX = e2.getRawX() - mLastScrollX;
            float diffY = e2.getRawY() - mLastScrollY;
            
            View item = mItemList.get(mItemIndex);
            item.setX(item.getX() + diffX);
            item.setY(item.getY() + diffY);

            mLastScrollX = e2.getRawX();
            mLastScrollY = e2.getRawY();

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("khoinguyen", "onDown");
            final View item = mItemList.get(mItemIndex);
            
            float dx = item.getWidth() / 2 - e.getX();
            float dy = item.getHeight() / 2 - e.getY();
            float angle = (float) Math.toDegrees(Math.abs(Math.atan(dx/dy)));
            if (dy < 0)
                angle = 180 - angle;
            if (dx < 0)
                angle = -angle;
            
            Rect oldRect, newRect;
            oldRect = new Rect();
            newRect = new Rect();
            item.getGlobalVisibleRect(oldRect);
            Log.d("khoinguyen", "x,y="+oldRect.left+","+oldRect.top);
            /* change pivot point for rotating */
            item.setPivotX(e.getX());
            item.setPivotY(e.getY());
            item.getGlobalVisibleRect(newRect);
            Log.d("khoinguyen", "x,y="+newRect.left+","+newRect.top);
            
            /**
             * After change pivot point, rotating is re-apply with the new one. This may make position changes on the view.
             * Below do a translation to bring the view back to its position as it is with the old pivot point.
             * */
            item.setX(item.getX() + oldRect.left - newRect.left);
            item.setY(item.getY() + oldRect.top - newRect.top);
            ObjectAnimator oa = ObjectAnimator.ofFloat(item, "rotation", angle);
            oa.setDuration(300);
            oa.setInterpolator(new AccelerateDecelerateInterpolator());
            oa.start();
            return true;
        }
    };

    private StackAdapter mAdapter;

    // Rotation offset between cards
    public int mRotateOffset;
    
    // store child view items of stack
    private List<View> mItemList;

    // fly out animators of each child view item
    private List<FlyOutAnimation> mFlyOutAnimList;

    // fly in animators of each child view item
    private List<FlyInAnimation> mFlyInAnimList;

    // index list indicating current position of each child item. ex [3,1,2]
    // showing item 3 is currently at position 0, etc.
    private List<Integer> mIndexList;

    // Observer object for furthur implementation for
    // adapter.notifyDatasetChanged
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            initItemList();
            removeAllViewsInLayout();
            requestLayout();
            postInvalidate();
        }
    };

    public StackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StackView(Context context) {
        super(context);
    }

    @Override
    public StackAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(StackAdapter adapter) {
        if (mAdapter != null)
            mAdapter.unregisterDataSetObserver(mDataSetObserver);

        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);

        mAdapter.notifyDataSetChanged();
    }

    private void addAndMeasureAllItems() {
        if (mItemList != null && mItemList.size() != 0)
            for (View child : mItemList)
                addAndMeasureItem(child);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("2359", "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        addAndMeasureAllItems();
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d("2359", "onLayout");
        super.onLayout(changed, left, top, right, bottom);
        layoutAllItems();
    }

    private void initItemList() {
        int nItem = mAdapter.getCount();
        mRotateOffset = SUM_DEGREE / nItem;
        mItemList = new ArrayList<View>();
        mIndexList = new ArrayList<Integer>();
        for (int i = 0; i < nItem; ++i) {
            View newItem = mAdapter.getView(i, null, this);
            mItemList.add(newItem);
            mIndexList.add(i);
        }

        initFlyInAnimList();
        initFlyOutAnimList();
        initItemGestureDetector();
    }

    private void initItemGestureDetector() {
        int nItem = mItemList.size();
        for (int i = 0; i < nItem; ++i) {
            GestureDetector itemGestureDetector = new GestureDetector(getContext(), new OnItemGestureListener(i));
            OnItemTouchListener itemTouchListener = new OnItemTouchListener(i, itemGestureDetector);
            mItemList.get(i).setOnTouchListener(itemTouchListener);
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

    /**
     * Execute fly in animation for the card at given index
     * 
     * @param itemIndex
     *            Index of item.
     */
    private void flyIn(int itemIndex) {
        FlyInAnimation flyIn = mFlyInAnimList.get(itemIndex);
        View item = mItemList.get(itemIndex);
        flyIn.setValues(item.getX(), (getWidth() - item.getWidth()) / 2, item.getY(),
                (getHeight() - item.getHeight()) / 2, item.getRotation(), getOffsetRatote(mItemList.size())
                        * (mItemList.size() - mIndexList.get(itemIndex)));
        flyIn.start();
    }

    /**
     * Execute fly out animation on an item.
     * 
     * @param itemIndex
     *            index of the item to animate
     * @param vX
     *            velocity on X axis
     * @param vY
     *            velocity on Y axis
     */
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

    /**
     * Layout child items at center of StackView
     */
    private void layoutAllItems() {
        int nChild = mItemList.size();
        for (int index = 0; index < nChild; index++) {
            View item = mItemList.get(index);

            int width = item.getMeasuredWidth();
            int height = item.getMeasuredHeight();
            int left = (getWidth() - width) / 2;
            int top = (getHeight() - height) / 2;

            /* position children at center */
            item.layout(left, top, left + width, top + height);
        }
    }

    /**
     * Because velocity passed in onFling is on view's coordinate system, so
     * when card is rotated, transform the velocity to the view parent's
     * coordinate system.
     * 
     * @param velocity
     *            pair of x/y velocities.
     */
    private void rotateVector(int itemIndex, float[] velocity) {
        Matrix matrix = new Matrix();
        matrix.setRotate(mItemList.get(itemIndex).getRotation());
        matrix.mapPoints(velocity);
    }

    /**
     * Add child item to layout. If item has its own layout params, use them,
     * otherwise use wrap_content for item's width/height
     */
    private void addAndMeasureItem(View child) {
        int measureW;
        int measureH;

        LayoutParams params = child.getLayoutParams();

        // respect the layout params of child item, otherwise select a default
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

    /**
     * Return the rotation offset between each item
     * 
     * @param nItem
     * @return
     */
    private float getOffsetRatote(int nItem) {
        float offsetRotate = SUM_DEGREE / nItem;
        return offsetRotate < mRotateOffset ? offsetRotate : mRotateOffset;
    }

    /**
     * Intro animation of stack. Rotate each child item to a degree
     * corresponding to item's index.
     */
    public void animateIntro() {
        int nItem = mItemList.size();
        if (nItem == 0)
            return;

        float offsetRotate = getOffsetRatote(nItem);
        for (int i = 0; i < nItem; ++i) {
            View item = mItemList.get(mIndexList.get(i));
            ObjectAnimator oa = ObjectAnimator.ofFloat(item, "rotation", 0, -offsetRotate * (nItem - i));
            oa.setInterpolator(new AccelerateInterpolator());
            oa.setDuration(DUR_INTRO_ANIM);
            oa.start();
        }
    }

    /**
     * Correct the z order of all items.
     */
    private void reorderZAxis() {
        Log.d("2359", "reorderZAxis");
        for (int i = 0; i < mIndexList.size(); ++i)
            mItemList.get(mIndexList.get(i)).bringToFront();

        invalidate();
    }

    /**
     * Shift item index in index list.
     * 
     * @param itemIndex
     *            The item index is going to be moved from its position to
     *            bottom
     */
    private void shiftItemList(int itemIndex) {
        mIndexList.remove(Integer.valueOf(itemIndex));
        mIndexList.add(0, itemIndex);
    }

    /**
     * Animation effect after an item removed from stack. Each item rotate to
     * its new position (after one goes to bottom)
     */
    private void animateShiftItem(int itemIndex) {
        int nItem = mItemList.size();
        float offsetRotate = getOffsetRatote(nItem);
        for (int i = 0; i < nItem; ++i) {
            // if (mIndexList.get(i) == itemIndex)
            // continue;
            View item = mItemList.get(mIndexList.get(i));
            ObjectAnimator oa = ObjectAnimator.ofFloat(item, "rotation", item.getRotation(), -offsetRotate
                    * (nItem - i));
            oa.setInterpolator(new AccelerateDecelerateInterpolator());
            oa.setDuration(DUR_SHIFT_ANIM);
            oa.start();
        }
    }

    @Override
    public void onFlyOutEnd(View flyView) {
        int itemIndex = mItemList.indexOf(flyView);
        flyIn(itemIndex);
        reorderZAxis();
        animateShiftItem(itemIndex);
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setSelection(int arg0) {
        throw new UnsupportedOperationException("Not supported");
    }
}
