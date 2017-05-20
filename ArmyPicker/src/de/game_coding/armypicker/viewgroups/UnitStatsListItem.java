package de.game_coding.armypicker.viewgroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.adapter.BaseAdapter;
import de.game_coding.armypicker.adapter.UnitGameRuleListAdapter;
import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import de.game_coding.armypicker.util.UnitUtils;
import de.game_coding.armypicker.util.WeaponUtils;
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

	public void bind(final StatsEntry item, final String[] headers, final Collection<StatsEntry> weapons,
		final UnitStatsSummaries showSummaries) {

		String entryName = "";
		if (item != null) {
			entryName = item.getName();
		}
		title.setText(entryName);

		table.removeAllViews();
		addRow(headers);
		addRow(item.getValues());
		for (final StatsEntry sec : item.getSecondaries()) {
			addRow(sec.getValues());
		}

		final List<GameRule> rules = new ArrayList<GameRule>();
		rules.addAll(item.getGameRules());
		rules.addAll(WeaponUtils.getGearRules(weapons));
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

	private void buildRuleEntries(final BaseAdapter<?, ?> adapter) {
		ruleList.removeAllViews();
		adapter.fillWithItems(ruleList, this);
	}

	private void addRow(final String[] values) {
		final TableRow tableRow = new TableRow(getContext());
		for (final String statName : values) {
			final TextView text = new TextView(getContext());
			text.setText(statName);
			text.setGravity(Gravity.CENTER_HORIZONTAL);

			final int width = table.getWidth() / values.length;
			/*
			 * if (statName.equals("M")) { width += (int) (width * 0.4); } else
			 * if (statName.equals("S") || statName.equals("T")) { width -=
			 * (int) (width * 0.2); }
			 */
			text.setLayoutParams(new TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT));
			text.setTextColor(text.getResources().getColor(R.color.text_color));
			tableRow.addView(text);
		}
		table.addView(tableRow);
	}

}
