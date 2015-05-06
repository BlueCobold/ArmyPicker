package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.UnitOptionGroup;

public class OptionGroupListAdapter extends ArrayAdapter<UnitOptionGroup> {

	private IValueChangedNotifier notifier;
	private final List<OptionListAdapter> adapters = new ArrayList<OptionListAdapter>();

	public OptionGroupListAdapter(final Context context, final List<UnitOptionGroup> optionGroups) {
		super(context, R.layout.item_option_group_list, optionGroups);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_option_group_list, parent, false);
		}
		final UnitOptionGroup group = getItem(position);

		final LinearLayout options = (LinearLayout) view.findViewById(R.id.option_list);
		final OptionListAdapter adapter = new OptionListAdapter(getContext(), group);
		adapters.add(adapter);
		buildEntries(options, adapter);

		adapter.setNotifier(new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				if (notifier != null) {
					notifier.onValueChanged();
				}
				for (final OptionListAdapter other : adapters) {
					if (!other.equals(adapter)) {
						other.refreshViews();
					}
				}
			}
		});

		return view;
	}

	private void buildEntries(final LinearLayout rootView, final OptionListAdapter adapter) {
		rootView.removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			rootView.addView(adapter.getView(i, null, rootView));
		}
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}
}
