package de.game_coding.armypicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.util.UIUtil;

public class UnitTypeListAdapter extends BaseUnitAdapter {

	public UnitTypeListAdapter(final Context context, final Unit[] units) {
		super(context, R.layout.item_unit_type_list, units);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_unit_type_list, parent, false);
		}
		final Unit unit = getItem(position);

		final TextView title = (TextView) view.findViewById(R.id.list_item_name);
		title.setText(unit.getName());

		final TextView points = (TextView) view.findViewById(R.id.list_item_points);
		points.setText(String.valueOf(unit.getTotalCosts()));

		final TextView type = (TextView) view.findViewById(R.id.unit_type_header);
		if (position == 0 || unit.getType() != getItem(position - 1).getType()) {
			type.setText("== " + getUnitTypeName(unit, view) + " ==");
			UIUtil.show(type);
		} else {
			type.setVisibility(View.GONE);
		}
		return view;
	}
}
