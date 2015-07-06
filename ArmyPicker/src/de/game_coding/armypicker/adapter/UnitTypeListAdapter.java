package de.game_coding.armypicker.adapter;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.viewgroups.UnitSelectionListItem;
import de.game_coding.armypicker.viewgroups.UnitSelectionListItem_;

public class UnitTypeListAdapter extends BaseUnitAdapter<UnitSelectionListItem> {

	public UnitTypeListAdapter(final Context context, final Unit[] units) {
		super(context, units);
	}

	@Override
	protected UnitSelectionListItem buildNewView() {
		return UnitSelectionListItem_.build(getContext());
	}

	@Override
	protected void fillView(final UnitSelectionListItem view, final Unit unit, final int position,
		final ViewGroup parent) {
		view.bind(unit);
		if (position == 0 || unit.getType() != getItem(position - 1).getType()) {
			view.setHeader(getUnitTypeName(unit, view));
		} else {
			view.setHeader(null);
		}
	}
}
