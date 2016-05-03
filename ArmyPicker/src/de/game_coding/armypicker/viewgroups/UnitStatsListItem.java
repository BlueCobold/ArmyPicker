package de.game_coding.armypicker.viewgroups;

import java.util.List;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.adapter.UnitGameRuleListAdapter;
import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import de.game_coding.armypicker.util.UnitUtils;
import de.game_coding.armypicker.viewmodel.UnitStatsSummaries;

@EViewGroup(R.layout.item_unit_stats_list)
public class UnitStatsListItem extends RelativeLayout {

	@ViewById(R.id.list_item_name)
	protected TextView title;

	@ViewById(R.id.item_list_table)
	protected TableLayout table;

	@ViewById(R.id.unit_list_item_rules_summary)
	protected TextView rulesSummary;

	@ViewById(R.id.unit_list_item_gamerules_list)
	protected LinearLayout ruleList;

	public UnitStatsListItem(final Context context) {
		super(context);
	}

	public void bind(final StatsEntry item, final String[] headers, UnitStatsSummaries showSummaries) {

		String entryName = "";
		if (item != null) {
			entryName = item.getName();
		}
		title.setText(entryName);

		table.removeAllViews();
		addRow(headers);
		addRow(item.getValues());

		final List<GameRule> rules = item.getGameRules();
		if (showSummaries == UnitStatsSummaries.ALL_SUMMARIES && !rules.isEmpty()) {
			rulesSummary.setText(UnitUtils.getRulesSummaries(rules));
			rulesSummary.setVisibility(View.VISIBLE);
		} else {
			rulesSummary.setVisibility(View.GONE);
		}

		if (showSummaries == UnitStatsSummaries.ALL_DETAILS && !rules.isEmpty()) {
			final UnitGameRuleListAdapter ruleAdapter = new UnitGameRuleListAdapter(getContext(), rules);
			buildRuleEntries(ruleAdapter);
			ruleList.setVisibility(View.VISIBLE);
		} else {
			ruleList.setVisibility(View.GONE);
		}
	}

	private void buildRuleEntries(final BaseAdapter adapter) {
		ruleList.removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			ruleList.addView(adapter.getView(i, null, this));
		}
	}

	private void addRow(final String[] values) {
		final TableRow tableRow = new TableRow(getContext());
		for (final String statName : values) {
			final TextView text = new TextView(getContext());
			text.setText(statName);
			text.setGravity(Gravity.CENTER_HORIZONTAL);
			text.setLayoutParams(
				new TableRow.LayoutParams(table.getWidth() / values.length, TableRow.LayoutParams.WRAP_CONTENT));
			text.setTextColor(text.getResources().getColor(R.color.text_color));
			tableRow.addView(text);
		}
		table.addView(tableRow);
	}

}
