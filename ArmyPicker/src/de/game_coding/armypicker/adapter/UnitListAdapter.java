package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.viewgroups.UnitListItem;
import de.game_coding.armypicker.viewgroups.UnitListItem_;

public class UnitListAdapter extends BaseUnitAdapter<UnitListItem> {

	private IValueChangedNotifier notifier;
	private DeleteHandler<Unit> deleteHandler;
	private final boolean showHeader;
	private final boolean showSummaries;

	public UnitListAdapter(final Context context, final List<Unit> units, final boolean showHeader,
		final boolean showSummaries) {
		super(context, units);
		this.showHeader = showHeader;
		this.showSummaries = showSummaries;
	}

	@Override
	protected UnitListItem buildNewView() {
		return UnitListItem_.build(getContext());
	}

	@Override
	protected void fillView(final UnitListItem view, final Unit item, final int position) {

		view.bind(item, showSummaries);
		view.setDeleteHandler(deleteHandler);
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
