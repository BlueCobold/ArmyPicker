package de.game_coding.armypicker.listener;

public interface ProgressClickListener<T> {

	void onItemClicked(T item, CompletionHandler completion);
}
