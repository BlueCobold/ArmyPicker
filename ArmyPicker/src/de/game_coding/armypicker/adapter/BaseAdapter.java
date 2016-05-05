package de.game_coding.armypicker.adapter;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import de.game_coding.armypicker.util.WeakSparseArray;

public abstract class BaseAdapter<M, V extends View> extends ArrayAdapter<M> {

	private final WeakSparseArray<V> views = new WeakSparseArray<V>();

	public BaseAdapter(final Context context, final M[] objects) {
		super(context, 0, objects);
	}

	public BaseAdapter(final Context context, final List<M> objects) {
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

		views.putItem(position, view);
		fillView(view, getItem(position), position, parent);

		return view;
	}

	protected WeakSparseArray<V> getViews() {
		return views;
	}

	public void notifyDataChanged(final M item) {
		for (int i = 0; i < getCount(); i++) {
			if (getItem(i).equals(item)) {
				final WeakReference<V> ref = views.get(i);
				if (ref == null) {
					continue;
				}
				final V view = ref.get();
				if (view != null) {
					fillView(view, item, i, null);
				}
				return;
			}
		}
	}

	public void fillWithItems(final LinearLayout container, final ViewGroup parent) {
		for (int i = 0; i < getCount(); i++) {
			container.addView(getView(i, null, parent));
		}
	}

	protected void fillView(final V view, final M item, final int position, final ViewGroup parent) {
	}

	protected abstract V buildNewView();
}