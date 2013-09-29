package com.example.animatedflashcard;

import java.util.ArrayList;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements CardView.CardFlyOutListener {
	public static final int MAX_CARD = 25;
	public static final int NUM_CARD = 10;
	public static final int SUM_DEGREE = 120;
	public static final int DUR_INTRO_ANIM = 1000;
	public static final int DUR_NEXT_ANIM = 300;
	public static final int DUR_FLY_IN = 700;

	private ArrayList<CardView> mCardList;
	private ViewGroup mContentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		createCards(NUM_CARD);
		animateIntro();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh: {
			clearCards();
			createCards(NUM_CARD);
			animateIntro();
			break;
		}

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Animate after a card removed from stack
	 * 
	 * @param cardView
	 *            removed card
	 */
	private void animateShiftCard() {
		int nCard = mCardList.size();
		float offsetRotate = SUM_DEGREE / nCard;
		for (int i = 0; i < nCard; ++i) {
			CardView card = mCardList.get(i);
			ObjectAnimator oa = ObjectAnimator.ofFloat(card, "rotation", card.getRotation(), -offsetRotate
					* (nCard - i));
			oa.setInterpolator(new AccelerateDecelerateInterpolator());
			oa.setDuration(DUR_NEXT_ANIM);
			oa.start();
		}
	}

	private void animateIntro() {
		if (mCardList == null)
			return;

		int nCard = mCardList.size();
		float offsetRotate = SUM_DEGREE / nCard;
		for (int i = 0; i < nCard; ++i) {
			CardView card = mCardList.get(i);
			ObjectAnimator oa = ObjectAnimator.ofFloat(card, "rotation", -offsetRotate, -offsetRotate * (nCard - i));
			oa.setInterpolator(new AccelerateInterpolator());
			oa.setDuration(DUR_INTRO_ANIM);
			oa.start();
		}
	}

	private void createCards(int numCard) {
		if (mContentView == null)
			return;

		if (numCard > MAX_CARD)
			numCard = MAX_CARD;

		int cardWidth = mContentView.getWidth() / 3;
		int cardHeight = mContentView.getHeight() / 3;
		if (cardWidth < cardHeight) {
			cardWidth = cardWidth ^ cardHeight;
			cardHeight = cardWidth ^ cardHeight;
			cardWidth = cardWidth ^ cardHeight;
		}
		float cardX = (mContentView.getWidth() - cardWidth) / 2;
		float cardY = (mContentView.getHeight() - cardHeight) / 2;

		mCardList = new ArrayList<CardView>();
		for (int i = 0; i < numCard; ++i) {
			CardView cv = new CardView(this, mContentView, this);
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(cardWidth, cardHeight);
			cv.setLayoutParams(lp);
			cv.setX(cardX);
			cv.setY(cardY);
			cv.setCardLabel("Flash card No. " + (numCard - i));
			mContentView.addView(cv);
			mCardList.add(cv);
		}
	}

	private void initContentView() {
		mContentView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_main, null);
		setContentView(mContentView);
	}

	private void clearCards() {
		if (mCardList == null)
			return;

		for (CardView cv : mCardList) {
			mContentView.removeView(cv);
		}
		mCardList.clear();
	}

	@Override
	public void onCardFlyOutEnd(CardView flyCard) {
		reorderZAxis();
		animateShiftCard();
	}

	private void reorderZAxis() {
		for (CardView cv : mCardList)
			cv.bringToFront();

		mContentView.requestLayout();
		mContentView.invalidate();
	}

	@Override
	public void onCardFlyOutStart(CardView flyCard) {
		reorderCardList(flyCard);
	}

	private void reorderCardList(CardView flyCard) {
		mCardList.remove(flyCard);
		mCardList.add(0, flyCard);
	}
}
