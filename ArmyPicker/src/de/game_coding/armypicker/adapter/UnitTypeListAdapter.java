package de.game_coding.armypicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Unit;

public class UnitTypeListAdapter extends ArrayAdapter<Unit> {

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
		final TextView title = (TextView) view.findViewById(R.id.list_item_name);
		title.setText(getItem(position).getName());

		final TextView points = (TextView) view.findViewById(R.id.list_item_points);
		points.setText(String.valueOf(getItem(position).getTotalCosts()));
		return view;
	}
}
