package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

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

public class UnitStatsListAdapter extends ArrayAdapter<UnitStats.StatsEntry> {

	private final List<UnitStats> stats;

	public UnitStatsListAdapter(final Context context, final UnitStats stats) {
		super(context, R.layout.item_unit_stats_list, stats.getEntries());
		this.stats = new ArrayList<UnitStats>();
		this.stats.add(stats);
	}

	public UnitStatsListAdapter(final Context context, final List<UnitStats> stats) {
		super(context, R.layout.item_unit_stats_list, merge(stats));
		this.stats = stats;
	}

	private static List<StatsEntry> merge(final List<UnitStats> stats) {
		final List<StatsEntry> merged = new ArrayList<UnitStats.StatsEntry>();
		for (final UnitStats stat : stats) {
			merged.addAll(stat.getEntries());
		}
		return merged;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_unit_stats_list, parent, false);
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
		addRow(findByIndex(stats, position).getHeaders(), table);
		addRow(entry.getValues(), table);
		return view;
	}

	private UnitStats findByIndex(final List<UnitStats> stats, final int position) {
		int skipped = 0;
		int types = 0;
		while (types < stats.size()) {
			if (position - skipped < stats.get(types).getEntries().size()) {
				return stats.get(types);
			}
			skipped += stats.get(types).getEntries().size();
			types++;
		}
		return null;
	}

	private void addRow(final String[] values, final TableLayout table) {
		final TableRow tableRow = new TableRow(getContext());
		for (final String statName : values) {
			final TextView text = new TextView(getContext());
			text.setText(statName);
			text.setGravity(Gravity.CENTER_HORIZONTAL);
			text.setLayoutParams(new TableRow.LayoutParams(table.getWidth() / values.length,
				TableRow.LayoutParams.WRAP_CONTENT));
			text.setTextColor(text.getResources().getColor(R.color.text_color));
			tableRow.addView(text);
		}
		table.addView(tableRow);
	}
}
