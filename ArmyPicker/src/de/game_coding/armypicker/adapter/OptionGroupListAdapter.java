package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.viewgroups.OptionGroupListItem;
import de.game_coding.armypicker.viewgroups.OptionGroupListItem_;

public class OptionGroupListAdapter extends BaseAdapter<UnitOptionGroup, OptionGroupListItem> {

	private IValueChangedNotifier notifier;
	private final List<OptionGroupListItem> views = new ArrayList<OptionGroupListItem>();

	public OptionGroupListAdapter(final Context context, final List<UnitOptionGroup> optionGroups) {
		super(context, optionGroups);
	}

	@Override
	protected OptionGroupListItem buildNewView() {
		return OptionGroupListItem_.build(getContext());
	}

	@Override
	protected void fillView(final OptionGroupListItem view, final UnitOptionGroup item, final int position) {
		view.bind(item);

		if (!views.contains(view)) {
			views.add(view);
		}

		view.setNotifier(new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				for (final OptionGroupListItem other : views) {
					if (other != view) {
						other.notifyValueChanged();
					}
					other.refresh();
				}
				if (notifier != null) {
					notifier.onValueChanged();
				}
			}
		});
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}
}
