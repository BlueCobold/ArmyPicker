package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Character;
import de.game_coding.armypicker.viewgroups.CharacterListItem;
import de.game_coding.armypicker.viewgroups.CharacterListItem_;

public class CharacterListAdapter extends BaseAdapter<Character, CharacterListItem> {

	public CharacterListAdapter(final Context context, final List<Character> characters) {
		super(context, characters);
	}

	private ItemClickedListener<Character> imageRequestHandler;

	private ItemClickedListener<Character> optionRequestHandler;

	@Override
	protected CharacterListItem buildNewView() {
		return CharacterListItem_.build(getContext());
	}

	@Override
	protected void fillView(final CharacterListItem view, final Character item, final int position,
		final ViewGroup parent) {
		view.bind(item);
		view.setOnImageRequestListener(imageRequestHandler);
		view.setOnOptionRequestListener(optionRequestHandler);
	}

	public void setOnImageRequestListener(final ItemClickedListener<Character> imageRequestHandler) {
		this.imageRequestHandler = imageRequestHandler;
	}

	public void onOptionRequestListener(final ItemClickedListener<Character> optionRequestHandler) {
		this.optionRequestHandler = optionRequestHandler;
	}
}
