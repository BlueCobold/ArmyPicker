package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;

@EViewGroup(R.layout.item_weapon_stats_list)
public class WeaponStatsListItem extends RelativeLayout {

	@ViewById(R.id.list_item_name)
	protected TextView title;

	@ViewById(R.id.item_list_table)
	protected TableLayout table;

	private UnitStats stats;

	private StatsEntry entry;

	public WeaponStatsListItem(final Context context) {
		super(context);
	}

	public void bind(final UnitStats stats, final StatsEntry statsEntry) {
		entry = statsEntry;
		this.stats = stats;
		String entryName = "";
		if (entry != null) {
			entryName = entry.getName();
		}
		title.setText(entryName);
		table.removeAllViews();
		addRow(entry.getValues(), table, this);
		for (final StatsEntry weapon : entry.getSecondaries()) {
			addRow(weapon.getValues(), table, this);
		}
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