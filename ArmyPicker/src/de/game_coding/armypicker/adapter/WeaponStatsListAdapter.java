package de.game_coding.armypicker.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;

public class WeaponStatsListAdapter extends ArrayAdapter<UnitStats.StatsEntry> {

	private final UnitStats stats;

	public WeaponStatsListAdapter(final Context context, final UnitStats stats) {
		super(context, R.layout.item_weapon_stats_list, stats.getEntries());
		this.stats = stats;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_weapon_stats_list, parent, false);
		}

		String entryName = "";
		final StatsEntry entry = getItem(position);
		if (entry != null) {
			entryName = entry.getName();
		}
		final TextView title = (TextView) view.findViewById(R.id.list_item_name);
		title.setText(entryName);

		final TableLayout table = (TableLayout) view.findViewById(R.id.item_list_table);
		table.removeAllViews();
		addRow(entry.getValues(), table, parent);
		for (final StatsEntry weapon : entry.getSecondaries()) {
			addRow(weapon.getValues(), table, parent);
		}
		return view;
	}

	private void addRow(final String[] values, final TableLayout table, final ViewGroup parent) {
		boolean empty = true;
		for (final String value : values) {
			empty &= value.isEmpty();
		}
		if (empty) {
			return;
		}
		final TableRow tableRow = new TableRow(getContext());
		final float[] percents = new float[] { 0.22f, 0.20f, 0.12f, 0.12f, 0.34f };
		for (int i = 0; i < values.length && i < stats.getHeaders().length; i++) {
			final String value = values[i];
			final String header = stats.getHeaders()[i];
			final TextView text = new TextView(getContext());
			text.setText(header + value);
			if (i > 0) {
				text.setGravity(Gravity.CENTER_HORIZONTAL);
			}
			text.setLayoutParams(new TableRow.LayoutParams((int) (parent.getWidth() * percents[i]),
				TableRow.LayoutParams.WRAP_CONTENT));
			text.setTextColor(text.getResources().getColor(R.color.text_color));
			tableRow.addView(text);
		}
		table.addView(tableRow);
	}
}
