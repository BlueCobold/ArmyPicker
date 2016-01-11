package de.game_coding.armypicker.util;

import java.lang.ref.WeakReference;

import android.util.SparseArray;

public class WeakSparseArray<T> extends SparseArray<WeakReference<T>> {

	public void putItem(final int key, final T value) {
		put(key, new WeakReference<T>(value));
	}
}
