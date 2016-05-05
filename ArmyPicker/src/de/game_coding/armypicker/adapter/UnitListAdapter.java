package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.Battalion;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.util.UnitUtils;
import de.game_coding.armypicker.viewgroups.UnitListItem;
import de.game_coding.armypicker.viewgroups.UnitListItem_;
import de.game_coding.armypicker.viewmodel.UnitSummaries;

public class UnitListAdapter extends BaseUnitAdapter<UnitListItem> {

	private IValueChangedNotifier notifier;
	private DeleteHandler<Unit> deleteHandler;
	private final boolean showHeader;
	private final UnitSummaries showSummaries;
	private final Army army;

	public UnitListAdapter(final Context context, final Army army, final boolean showHeader,
		final UnitSummaries showSummaries) {
		super(context, join(army.getUnits(), getBattalionUnits(army)).toArray(new Unit[army.getUnits().size()]));
		this.army = army;
		this.showHeader = showHeader;
		this.showSummaries = showSummaries;
	}

	private static List<Unit> join(final List<Unit> units, final List<Unit> battalionUnits) {
		final ArrayList<Unit> list = new ArrayList<Unit>();
		list.addAll(units);
		list.addAll(battalionUnits);
		return list;
	}

	private static List<Unit> getBattalionUnits(final Army a) {
		final ArrayList<Unit> list = new ArrayList<Unit>();
		for (final Battalion b : a.getBattalions()) {
			list.addAll(getBattalionUnits(b.getRequirement()));
		}
		return list;
	}

	private static List<Unit> getBattalionUnits(final BattalionRequirement requirement) {
		final ArrayList<Unit> list = new ArrayList<Unit>();
		list.addAll(requirement.getAssignedUnits());
		for (final BattalionRequirement sub : requirement.getAssignedSubBattalions()) {
			list.addAll(getBattalionUnits(sub));
		}
		return list;
	}

	@Override
	protected UnitListItem buildNewView() {
		return UnitListItem_.build(getContext());
	}

	@Override
	protected void fillView(final UnitListItem view, final Unit item, final int position, final ViewGroup parent) {

		view.setDeleteHandler(army.getUnits().contains(item) ? deleteHandler : null);
		view.bind(item, UnitUtils.getStats(item.getStatsReferences(), army.getStats()), showSummaries);
		view.setNotifier(notifier);

		if (showHeader && (position == 0 || item.getType() != getItem(position - 1).getType())) {
			view.setHeader(getUnitTypeName(item, view));
		} else {
			view.setHeader(null);
		}

		view.setLongClickable(true);
		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(final View v) {
				final ItemClickedListener<Unit> handler = getLongClickHandler();
				if (handler != null) {
					handler.onItemClicked(item);
				}
				return true;
			}
		});
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}

	public void setDeleteHandler(final DeleteHandler<Unit> deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}
