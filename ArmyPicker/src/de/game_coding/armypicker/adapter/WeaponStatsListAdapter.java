package de.game_coding.armypicker.adapter;

import android.content.Context;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import de.game_coding.armypicker.viewgroups.WeaponStatsListItem;
import de.game_coding.armypicker.viewgroups.WeaponStatsListItem_;

public class WeaponStatsListAdapter extends BaseAdapter<UnitStats.StatsEntry, WeaponStatsListItem> {

	private final UnitStats stats;

	public WeaponStatsListAdapter(final Context context, final UnitStats stats) {
		super(context, stats.getEntries());
		this.stats = stats;
	}

	@Override
	protected WeaponStatsListItem buildNewView() {
		return WeaponStatsListItem_.build(getContext());
	}

	@Override
	protected void fillView(final WeaponStatsListItem view, final StatsEntry item, final int position) {
		view.bind(stats, item);
	}
}
