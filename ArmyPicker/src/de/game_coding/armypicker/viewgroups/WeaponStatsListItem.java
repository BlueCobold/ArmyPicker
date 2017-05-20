package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;

@EViewGroup(R.layout.item_weapon_stats_list)
public class WeaponStatsListItem extends RelativeLayout {

	@ViewById(R.id.list_item_name)
	protected TextView title;

	@ViewById(R.id.item_list_table)
	protected TableLayout itemTable;

	@ViewById(R.id.item_rules_table)
	protected TableLayout rulesTable;

	private UnitStats stats;

	private StatsEntry entry;

	public WeaponStatsListItem(final Context context) {
		super(context);
	}

	public void bind(final UnitStats stats, final StatsEntry statsEntry, final int width, final boolean showRules) {
		entry = statsEntry;
		this.stats = stats;
		String entryName = "";
		if (entry != null) {
			entryName = entry.getName();
		}
		title.setText(entryName);
		itemTable.removeAllViews();
		rulesTable.removeAllViews();
		addRow(itemTable, entry.getValues(), width, null);
		for (final StatsEntry weapon : entry.getSecondaries()) {
			addRow(itemTable, weapon.getValues(), width, null);
		}
		if (showRules) {
			for (final GameRule rule : entry.getGameRules()) {
				addRow(rulesTable, new String[] { rule.getTitle(), rule.getDescription() }, width,
					new float[] { 0.4f, 0.6f });
			}
		}
	}

	private void addRow(final TableLayout table, final String[] values, final int width, final float[] entryWidths) {
		boolean empty = true;
		for (final String value : values) {
			empty &= value.isEmpty();
		}
		if (empty) {
			return;
		}
		final TableRow tableRow = new TableRow(getContext());
		float[] percents = entryWidths;
		if (percents == null) {
			percents = new float[] { 0.18f, 0.20f, 0.12f, 0.12f, 0.12f, 0.26f };
		}

		for (int i = 0; i < values.length && i < stats.getHeaders().length; i++) {
			final String value = values[i];
			final String header = stats.getHeaders()[i];
			final TextView text = new TextView(getContext());
			text.setText(header + value);
			if (i > 0) {
				text.setGravity(Gravity.CENTER_HORIZONTAL);
			}
			text.setTextColor(text.getResources().getColor(R.color.text_color));
			text.setLayoutParams(
				new TableRow.LayoutParams((int) (width * percents[i]), TableRow.LayoutParams.WRAP_CONTENT));
			tableRow.addView(text);
		}
		table.addView(tableRow);
	}
}
