package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import de.game_coding.armypicker.viewgroups.UnitStatsListItem;
import de.game_coding.armypicker.viewgroups.UnitStatsListItem_;
import de.game_coding.armypicker.viewmodel.UnitStatsSummaries;

public class UnitStatsListAdapter extends BaseAdapter<UnitStats.StatsEntry, UnitStatsListItem> {

	private final List<UnitStats> units;
	private final UnitStatsSummaries showStatsSummaries;
	private final Collection<StatsEntry> weapons;

	public UnitStatsListAdapter(final Context context, final UnitStats units, final Collection<StatsEntry> weapons,
		final UnitStatsSummaries showStatsSummaries) {
		super(context, units.getEntries());
		this.showStatsSummaries = showStatsSummaries;
		this.units = new ArrayList<UnitStats>();
		this.units.add(units);
		this.weapons = weapons;
	}

	public UnitStatsListAdapter(final Context context, final List<UnitStats> units,
		final Collection<StatsEntry> weapons, final UnitStatsSummaries showStatsSummaries) {
		super(context, merge(units));
		this.units = units;
		this.showStatsSummaries = showStatsSummaries;
		this.weapons = weapons;
	}

	@Override
	protected UnitStatsListItem buildNewView() {
		return UnitStatsListItem_.build(getContext());
	}

	private static List<StatsEntry> merge(final List<UnitStats> stats) {
		final List<StatsEntry> merged = new ArrayList<UnitStats.StatsEntry>();
		for (final UnitStats stat : stats) {
			merged.addAll(stat.getEntries());
		}
		return merged;
	}

	@Override
	protected void fillView(final UnitStatsListItem view, final StatsEntry item, final int position,
		final ViewGroup parent) {
		view.bind(item, findByIndex(units, position).getHeaders(), weapons, showStatsSummaries);
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
}
