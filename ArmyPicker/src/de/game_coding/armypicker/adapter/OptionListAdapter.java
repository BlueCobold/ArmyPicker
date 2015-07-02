package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.UnitOption;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.viewgroups.OptionListItem;
import de.game_coding.armypicker.viewgroups.OptionListItem_;

public class OptionListAdapter extends BaseAdapter<UnitOption, OptionListItem> {

	private final UnitOptionGroup optionGroup;

	private IValueChangedNotifier notifier;

	private final List<OptionListItem> views = new ArrayList<OptionListItem>();

	public OptionListAdapter(final Context context, final UnitOptionGroup optionGroup) {
		super(context, optionGroup.getOptions());
		this.optionGroup = optionGroup;
	}

	@Override
	protected OptionListItem buildNewView() {
		return OptionListItem_.build(getContext());
	}

	@Override
	protected void fillView(final OptionListItem view, final UnitOption item, final int position) {
		view.bind(item, optionGroup);

		if (!views.contains(view)) {
			views.add(view);
		}

		view.setNotifier(new IValueChangedNotifier() {
			@Override
			public void onValueChanged() {
				for (final OptionListItem v : views) {
					if (v != view) {
						v.refresh();
					}
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

	public void refreshViews() {
		for (final OptionListItem view : views) {
			view.refresh();
		}
	}
}
