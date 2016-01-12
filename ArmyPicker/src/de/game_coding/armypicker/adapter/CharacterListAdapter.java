package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.viewgroups.CharacterListItem;
import de.game_coding.armypicker.viewgroups.CharacterListItem_;
import de.game_coding.armypicker.viewmodel.CharacterViewModel;

public class CharacterListAdapter extends BaseAdapter<CharacterViewModel, CharacterListItem> {

	public CharacterListAdapter(final Context context, final List<CharacterViewModel> characters) {
		super(context, characters);
	}

	private ItemClickedListener<CharacterViewModel> imageRequestHandler;

	private ItemClickedListener<CharacterViewModel> optionRequestHandler;

	private DeleteHandler<CharacterViewModel> deleteHandler;

	@Override
	protected CharacterListItem buildNewView() {
		return CharacterListItem_.build(getContext());
	}

	@Override
	protected void fillView(final CharacterListItem view, final CharacterViewModel item, final int position,
		final ViewGroup parent) {
		view.bind(item);
		view.setOnImageRequestListener(imageRequestHandler);
		view.setOnOptionRequestListener(optionRequestHandler);
		view.setOnDeleteHandler(deleteHandler);
	}

	public void setOnImageRequestListener(final ItemClickedListener<CharacterViewModel> imageRequestHandler) {
		this.imageRequestHandler = imageRequestHandler;
	}

	public void setOptionRequestListener(final ItemClickedListener<CharacterViewModel> optionRequestHandler) {
		this.optionRequestHandler = optionRequestHandler;
	}

	public void setOnDeleteHandler(final DeleteHandler<CharacterViewModel> deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}
