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
import de.game_coding.armypicker.model.UnitStats.StatsEntry;

@EViewGroup(R.layout.item_unit_stats_list)
public class UnitStatsListItem extends RelativeLayout {

	@ViewById(R.id.list_item_name)
	protected TextView title;

	@ViewById(R.id.item_list_table)
	protected TableLayout table;

	public UnitStatsListItem(final Context context) {
		super(context);
	}

	public void bind(final StatsEntry item, final String[] headers) {

		String entryName = "";
		if (item != null) {
			entryName = item.getName();
		}
		title.setText(entryName);

		table.removeAllViews();
		addRow(headers);
		addRow(item.getValues());
	}

	private void addRow(final String[] values) {
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
