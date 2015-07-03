package de.game_coding.armypicker.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeakArrayList<E> extends ArrayList<WeakReference<E>> implements WeakList<E> {

	private static final long serialVersionUID = 910077247507206633L;

	@Override
	public Collection<E> get() {
		final WeakList<E> nulls = new WeakArrayList<E>();
		final List<E> values = new ArrayList<E>();
		for (final WeakReference<E> item : this) {
			final E e = item.get();
			if (e != null) {
				values.add(e);
			} else {
				nulls.add(item);
			}
		}
		removeAll(nulls);
		return values;
	}

	@Override
	public void addItem(final E item) {
		add(new WeakReference<E>(item));
	}
}
