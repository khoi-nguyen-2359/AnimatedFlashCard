package khoinguyen.sample.animflashcard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TestActivity extends Activity {

	private StackAdapterView stack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		stack = (StackAdapterView) findViewById(R.id.cardStack1);
		List<Card> data = new ArrayList<Card>();
		data.add(new Card("title 1", "content 1"));
		data.add(new Card("title 2", "content 2"));
		data.add(new Card("title 3", "content 3"));
		data.add(new Card("title 4", "content 4"));
		data.add(new Card("title 5", "content 5"));
		data.add(new Card("title 6", "content 6"));
		data.add(new Card("title 7", "content 7"));
		data.add(new Card("title 8", "content 8"));
		stack.setAdapter(new CardStackAdapter(this, R.layout.view_card, data));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_refresh)
			stack.animateIntro();
		
		return super.onMenuItemSelected(featureId, item);
	}

}
