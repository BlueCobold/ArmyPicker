package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.UnitOptionGroup;

public class OptionGroupListAdapter extends ArrayAdapter<UnitOptionGroup> {

	private static final String CLOSE = "\u2191 ";
	private static final String OPEN = "\u2193 ";
	private IValueChangedNotifier notifier;
	private final List<OptionListAdapter> adapters = new ArrayList<OptionListAdapter>();
	private final List<IValueChangedNotifier> warningHandlers = new ArrayList<IValueChangedNotifier>();

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

		view.setBackgroundColor((position % 2 == 0) ? 0x0 : 0x0800ffff);
		final LinearLayout options = (LinearLayout) view.findViewById(R.id.option_list);
		final OptionListAdapter adapter = new OptionListAdapter(getContext(), group);
		adapters.add(adapter);

		final TextView collapseHeader = (TextView) view.findViewById(R.id.option_collapse_header);
		collapseHeader.setVisibility(!group.isCollapsable() || !group.isEnabled() ? View.GONE : View.VISIBLE);
		if (group.isCollapsable()) {
			collapseHeader.setText((group.isCollapsed() ? OPEN : CLOSE) + group.getExpansionTitle());
			collapseHeader.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					group.collapse(!group.isCollapsed());
					collapseHeader.setText((group.isCollapsed() ? OPEN : CLOSE) + group.getExpansionTitle());
					buildEntries(options, adapter, group);
				}
			});
		}
		buildEntries(options, adapter, group);

		final View rootView = view;
		updateWarnings(rootView, group);

		final IValueChangedNotifier handler = new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				if (notifier != null) {
					notifier.onValueChanged();
				}
				for (final IValueChangedNotifier warningHandler : warningHandlers) {
					warningHandler.onValueChanged();
				}
				for (final OptionListAdapter other : adapters) {
					if (!other.equals(adapter)) {
						other.refreshViews();
					}
				}
			}
		};
		warningHandlers.add(new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				updateWarnings(rootView, group);
				updateCollapseHeader(rootView, group);
			}
		});
		adapter.setNotifier(handler);

		return view;
	}

	private void updateCollapseHeader(final View rootView, final UnitOptionGroup group) {
		final TextView collapseHeader = (TextView) rootView.findViewById(R.id.option_collapse_header);
		collapseHeader.setVisibility(!group.isCollapsable() || !group.isEnabled() ? View.GONE : View.VISIBLE);
	}

	private void updateWarnings(final View rootView, final UnitOptionGroup group) {
		final TextView warnings = (TextView) rootView.findViewById(R.id.option_warnings_header);
		if (group.getActiveWarnings().size() == 0) {
			warnings.setText("");
			warnings.setVisibility(View.GONE);
		} else {
			String messages = new String();
			for (final String message : group.getActiveWarnings()) {
				if (messages.length() > 0) {
					messages += "\n";
				}
				messages += message;
			}
			warnings.setText(messages);
			warnings.setVisibility(View.VISIBLE);
		}
	}

	private void buildEntries(final LinearLayout rootView, final OptionListAdapter adapter, final UnitOptionGroup group) {
		rootView.removeAllViews();
		if (group.isCollapsed()) {
			return;
		}
		for (int i = 0; i < adapter.getCount(); i++) {
			rootView.addView(adapter.getView(i, null, rootView));
		}
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}
}
