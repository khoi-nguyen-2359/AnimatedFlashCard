package khoinguyen.sample.animflashcard.adapter;

import java.util.List;

import khoinguyen.sample.animflashcard.Card;
import khoinguyen.sample.animflashcard.R;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CardAdapter extends StackAdapter<Card> {

	static class ViewHolder {
		TextView mCardContent;
	}
	
	public static final int[] COLOR_ID_ARRAY = { R.color.card_colors_01, R.color.card_colors_02,
		R.color.card_colors_03, R.color.card_colors_04, R.color.card_colors_05, R.color.card_colors_06,
		R.color.card_colors_07, R.color.card_colors_08 };
	
	public static int COLOR_INDEX;
	
	public static final int CARD_WIDTH = 500;
	public static final int CARD_HEIGHT = 300;

	public CardAdapter(Context context, int resViewId, List<Card> data) {
		super(context, resViewId, data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(mResViewId, null);
			GradientDrawable shapeBg = (GradientDrawable) convertView.getBackground();
			shapeBg.setColor(mContext.getResources().getColor(COLOR_ID_ARRAY[COLOR_INDEX = ++COLOR_INDEX % COLOR_ID_ARRAY.length]));
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(CARD_WIDTH, CARD_HEIGHT);
			convertView.setLayoutParams(lp);
			
			viewHolder = new ViewHolder();
			viewHolder.mCardContent = (TextView) convertView.findViewById(R.id.tv_card_content);
			
			convertView.setTag(viewHolder);
		}
		
		Card data = getItem(position);
		viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.mCardContent.setText(data.getContent());
		
		return convertView;
	}
}
