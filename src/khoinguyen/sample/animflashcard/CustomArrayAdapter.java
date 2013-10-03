package khoinguyen.sample.animflashcard;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CustomArrayAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mData;
    protected int mResViewId;

    public CustomArrayAdapter(Context context, int resViewId, List<T> data) {
        mData = data;
        mContext = context;
        mResViewId = resViewId;
    }

    public void remove(T t) {
        mData.remove(t);
    }

    public void remove(int position) {
        mData.remove(position);
    }

    public void add(int position, T object) {
        mData.add(position, object);
    }

    public void add(T object) {
        mData.add(object);
    }

    public void addAll(Collection<? extends T> addition) {
        for (T t : addition) {
            mData.add(t);
        }
    }

    public void addAll(int location, Collection<? extends T> addition) {
        for (T t : addition) {
            mData.add(location++, t);
        }
    }

    public void clear() {
        mData.clear();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}