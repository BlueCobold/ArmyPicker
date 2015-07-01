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

	private final List<IValueChangedNotifier> onValidate = new ArrayList<IValueChangedNotifier>();

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

		final IValueChangedNotifier handler = new IValueChangedNotifier() {
			@Override
			public void onValueChanged() {
				view.refresh();
			}
		};
		onValidate.add(handler);

		view.setNotifier(new IValueChangedNotifier() {
			@Override
			public void onValueChanged() {
				for (final IValueChangedNotifier h : onValidate) {
					if (h != handler) {
						h.onValueChanged();
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
		for (final IValueChangedNotifier handler : onValidate) {
			handler.onValueChanged();
		}
	}
}
