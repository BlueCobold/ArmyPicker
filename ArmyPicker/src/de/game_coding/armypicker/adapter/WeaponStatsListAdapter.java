package de.game_coding.armypicker.adapter;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import de.game_coding.armypicker.viewgroups.WeaponStatsListItem;
import de.game_coding.armypicker.viewgroups.WeaponStatsListItem_;
import de.game_coding.armypicker.viewmodel.WeaponStatsSummaries;

public class WeaponStatsListAdapter extends BaseAdapter<UnitStats.StatsEntry, WeaponStatsListItem> {

	private final UnitStats stats;
	private final WeaponStatsSummaries showSummaries;

	public WeaponStatsListAdapter(final Context context, final UnitStats stats,
		final WeaponStatsSummaries showSummaries) {
		super(context, stats.getEntries());
		this.stats = stats;
		this.showSummaries = showSummaries;
	}

	@Override
	protected WeaponStatsListItem buildNewView() {
		return WeaponStatsListItem_.build(getContext());
	}

	@Override
	protected void fillView(final WeaponStatsListItem view, final StatsEntry item, final int position,
		final ViewGroup parent) {
		view.bind(stats, item, parent.getWidth(), showSummaries != WeaponStatsSummaries.NONE);
	}
}
