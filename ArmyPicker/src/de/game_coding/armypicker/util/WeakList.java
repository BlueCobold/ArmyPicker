package de.game_coding.armypicker.util;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;

public interface WeakList<T> extends List<WeakReference<T>> {

	Collection<T> get();

	void addItem(T item);

}
