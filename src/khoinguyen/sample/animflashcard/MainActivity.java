package khoinguyen.sample.animflashcard;

import java.util.ArrayList;
import java.util.List;

import khoinguyen.sample.animflashcard.adapter.CardAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private StackView mStackView;
	private CardAdapter mCardAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mStackView = (StackView) findViewById(R.id.stack_view);
		mCardAdapter = new CardAdapter(this, R.layout.view_card, new ArrayList<Card>());
		// create sample items for demo
		mCardAdapter.addAll(createCardList());
		mStackView.setAdapter(mCardAdapter);
	    mStackView.animateIntro();
	}

	private List<Card> createCardList() {
		List<Card> cardList = new ArrayList<Card>();

		cardList.add(new Card("Title 1", "Content 1"));
		cardList.add(new Card("Title 2", "Content 2"));
		cardList.add(new Card("Title 3", "Content 3"));
		cardList.add(new Card("Title 4", "Content 4"));
		cardList.add(new Card("Title 5", "Content 5"));
		cardList.add(new Card("Title 6", "Content 6"));
		cardList.add(new Card("Title 7", "Content 7"));

		return cardList;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh: {
		    mStackView.setAdapter(mCardAdapter);
			mStackView.animateIntro();
			break;
		}
		default:
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}
}
