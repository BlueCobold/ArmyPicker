package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.adapter.OptionListAdapter;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.UnitOptionGroup;

@EViewGroup(R.layout.item_option_group_list)
public class OptionGroupListItem extends RelativeLayout {

	private static final String CLOSE = "\u2191 ";

	private static final String OPEN = "\u2193 ";

	@ViewById(R.id.option_list)
	protected LinearLayout options;

	@ViewById(R.id.option_collapse_header)
	protected TextView collapseHeader;

	@ViewById(R.id.option_warnings_header)
	protected TextView warnings;

	private UnitOptionGroup group;

	private OptionListAdapter adapter;

	public OptionGroupListItem(final Context context) {
		super(context);
	}

	public void bind(final UnitOptionGroup group) {
		this.group = group;
		adapter = new OptionListAdapter(getContext(), group);
		buildEntries();

		refresh();
	}

	public void refresh() {
		updateWarnings();
		setVisibility(group.isEnabled() ? View.VISIBLE : View.GONE);
		updateCollapseHeader();
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		if (adapter != null) {
			adapter.setNotifier(notifier);
		}
	}

	public void notifyValueChanged() {
		if (adapter != null) {
			adapter.refreshViews();
		}
	}

	@Click(R.id.option_collapse_header)
	protected void onCollapseHeaderClicked() {
		group.collapse(!group.isCollapsed());
		updateCollapseHeader();
		buildEntries();
	}

	private void buildEntries() {
		options.removeAllViews();
		if (group.isCollapsed()) {
			return;
		}
		for (int i = 0; i < adapter.getCount(); i++) {
			options.addView(adapter.getView(i, null, this));
		}
	}

	private void updateWarnings() {
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

	private void updateCollapseHeader() {
		collapseHeader.setVisibility(!group.isCollapsible() || !group.isEnabled() ? View.GONE : View.VISIBLE);
		collapseHeader.setText((group.isCollapsed() ? OPEN : CLOSE) + group.getExpansionTitle() + " ["
			+ group.getAmountSelected() + "]");
	}

}
