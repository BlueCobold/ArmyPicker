package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.model.CharacterOption;
import de.game_coding.armypicker.viewgroups.CharacterOptionItem;
import de.game_coding.armypicker.viewgroups.CharacterOptionItem_;

public class CharacterOptionListAdapter extends BaseAdapter<CharacterOption, CharacterOptionItem> {

	private DeleteHandler<CharacterOption> deleteHandler;

	public CharacterOptionListAdapter(final Context context, final List<CharacterOption> objects) {
		super(context, objects);
	}

	public CharacterOptionListAdapter(final Context context, final CharacterOption[] objects) {
		super(context, objects);
	}

	@Override
	protected CharacterOptionItem buildNewView() {
		return CharacterOptionItem_.build(getContext());
	}

	@Override
	protected void fillView(final CharacterOptionItem view, final CharacterOption item, final int position,
		final ViewGroup parent) {
		view.bind(item);
		view.setOnDeleteHandler(deleteHandler);
	}

	public void setOnDeleteHandler(final DeleteHandler<CharacterOption> handler) {
		deleteHandler = handler;
	}
}
