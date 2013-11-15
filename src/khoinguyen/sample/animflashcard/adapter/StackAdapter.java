package khoinguyen.sample.animflashcard.adapter;

import java.util.List;

import android.content.Context;

public abstract class StackAdapter<T> extends CustomArrayAdapter<T> {
	public StackAdapter(Context context, int resViewId, List<T> data) {
		super(context, resViewId, data);
	}
}
