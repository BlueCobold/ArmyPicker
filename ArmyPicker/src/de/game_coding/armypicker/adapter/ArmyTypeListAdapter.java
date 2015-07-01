package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.viewgroups.ArmySelectionListItem;
import de.game_coding.armypicker.viewgroups.ArmySelectionListItem_;

public class ArmyTypeListAdapter extends BaseAdapter<Army, ArmySelectionListItem> {

	public ArmyTypeListAdapter(final Context context, final List<Army> armies) {
		super(context, armies);
	}

	@Override
	protected ArmySelectionListItem buildNewView() {
		return ArmySelectionListItem_.build(getContext());
	}

	@Override
	protected void fillView(final ArmySelectionListItem view, final Army item, final int position) {
		view.bind(item);
	}
}
