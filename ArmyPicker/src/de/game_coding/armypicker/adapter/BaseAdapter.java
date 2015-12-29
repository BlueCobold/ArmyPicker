package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class BaseAdapter<T, V extends View> extends ArrayAdapter<T> {

	private final SparseArray<V> views = new SparseArray<V>();

	public BaseAdapter(final Context context, final T[] objects) {
		super(context, 0, objects);
	}

	public BaseAdapter(final Context context, final List<T> objects) {
		super(context, 0, objects);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		final V view;
		if (convertView == null) {
			view = buildNewView();
		} else {
			view = (V) convertView;
		}

		fillView(view, getItem(position), position, parent);
		views.put(position, view);

		return view;
	}

	public void notifyDataChanged(final T item) {
		for (int i = 0; i < getCount(); i++) {
			if (getItem(i).equals(item)) {
				final V view = views.get(i);
				if (view != null) {
					fillView(view, item, i, null);
				}
				return;
			}
		}
	}

	protected void fillView(final V view, final T item, final int position, final ViewGroup parent) {
	}

	protected abstract V buildNewView();
}