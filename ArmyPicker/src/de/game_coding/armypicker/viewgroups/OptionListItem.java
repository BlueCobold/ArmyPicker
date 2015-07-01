package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.UnitOption;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.model.UnitOptionGroup.GroupType;
import de.game_coding.armypicker.util.UIUtil;

@EViewGroup(R.layout.item_option_list)
public class OptionListItem extends RelativeLayout {

	@ViewById(R.id.option_name)
	TextView name;

	@ViewById(R.id.option_points)
	TextView costs;

	@ViewById(R.id.option_amount)
	TextView amount;

	@ViewById(R.id.option_points_total)
	TextView total;

	@ViewById(R.id.option_delete)
	View delete;

	@ViewById(R.id.option_add)
	View add;

	private UnitOption option;

	private UnitOptionGroup optionGroup;

	private IValueChangedNotifier notifier;

	public OptionListItem(final Context context) {
		super(context);
	}

	public void bind(final UnitOption item, final UnitOptionGroup optionGroup) {
		option = item;
		this.optionGroup = optionGroup;

		name.setText(option.getName());
		costs.setText("(" + option.getCosts() + ")");

		refresh();
	}

	@Click(R.id.option_add)
	void onAddClicked() {
		if (optionGroup.canSelectMore(option)) {
			option.setAmountSelected(option.getAmountSelected()
				+ (optionGroup.getType() == GroupType.ONE_PER_MODEL ? optionGroup.getLimit() : 1));
			updateValues();
		}
	}

	@Click(R.id.option_delete)
	void onDeleteClicked() {
		option
			.setAmountSelected((optionGroup.getType() == GroupType.ONE_PER_MODEL || optionGroup.getType() == GroupType.ONE_PER_MODEL_EXEPT_ONE) ? 0
				: Math.max(0, option.getAmountSelected() - 1));
		updateValues();
	}

	private void updateValues() {
		refresh();
		if (notifier != null) {
			notifier.onValueChanged();
		}
	}

	public void refresh() {
		optionGroup.validateAmounts();

		amount.setText("[" + option.getAmountSelected() + "]");
		UIUtil.show(amount, option.getAmountSelected() > 0);

		total.setText(String.valueOf(option.getAmountSelected() * option.getCosts()));

		setVisibility(optionGroup.isEnabled() && (option.getParentId() < 0 || option.isEnabled()) ? View.VISIBLE
			: View.GONE);

		UIUtil.show(delete, option.getAmountSelected() > 0);
		UIUtil.show(add, optionGroup.canSelectMore(option) && option.isEnabled());
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}
}
