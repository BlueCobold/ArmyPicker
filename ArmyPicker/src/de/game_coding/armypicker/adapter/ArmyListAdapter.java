package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.EditHandler;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.viewgroups.ArmyListItem;
import de.game_coding.armypicker.viewgroups.ArmyListItem_;

public class ArmyListAdapter extends BaseAdapter<Army, ArmyListItem> {

	private DeleteHandler<Army> deleteHandler;
	private EditHandler<Army> editHandler;

	public ArmyListAdapter(final Context context, final List<Army> armies) {
		super(context, armies);
	}

	@Override
	protected ArmyListItem buildNewView() {
		return ArmyListItem_.build(getContext());
	}

	@Override
	protected void fillView(final ArmyListItem view, final Army item, final int position) {
		view.bind(item);
		view.setEditHandler(editHandler);
		view.setDeleteHandler(deleteHandler);
	}

	public void setDeleteHandler(final DeleteHandler<Army> handler) {
		deleteHandler = handler;
	}

	public void setEditHandler(final EditHandler<Army> handler) {
		editHandler = handler;
	}
}
