package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.UnitOption;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.model.UnitOptionGroup.GroupType;
import de.game_coding.armypicker.util.UIUtil;

public class OptionListAdapter extends ArrayAdapter<UnitOption> {

	private final UnitOptionGroup optionGroup;

	private IValueChangedNotifier notifier;

	private final List<IValueChangedNotifier> onValidate = new ArrayList<IValueChangedNotifier>();

	public OptionListAdapter(final Context context, final UnitOptionGroup optionGroup) {
		super(context, R.layout.item_option_list, optionGroup.getOptions());
		this.optionGroup = optionGroup;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_option_list, parent, false);
		}
		final UnitOption option = getItem(position);

		final TextView name = (TextView) view.findViewById(R.id.option_name);
		name.setText(option.getName());

		final TextView costs = (TextView) view.findViewById(R.id.option_points);
		costs.setText("(" + option.getCosts() + ")");

		final TextView amount = (TextView) view.findViewById(R.id.option_amount);
		amount.setText("[" + option.getAmountSelected() + "]");
		UIUtil.show(amount, option.getAmountSelected() > 0);

		final TextView total = (TextView) view.findViewById(R.id.option_points_total);
		total.setText(String.valueOf(option.getAmountSelected() * option.getCosts()));

		initButtons(option, amount, total, view);
		return view;
	}

	private void initButtons(final UnitOption option, final TextView amount, final TextView total, final View rootView) {

		final View delete = rootView.findViewById(R.id.option_delete);
		final View add = rootView.findViewById(R.id.option_add);
		validateButtons(option, delete, add);

		final IValueChangedNotifier handler = new IValueChangedNotifier() {
			@Override
			public void onValueChanged() {
				validateButtons(option, delete, add);
				updateViews(option, amount, total);
			}
		};
		onValidate.add(handler);

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (optionGroup.canSelectMore(option)) {
					option.setAmountSelected(option.getAmountSelected()
						+ (optionGroup.getType() == GroupType.ONE_PER_MODEL ? optionGroup.getLimit() : 1));
					updateValues(option, amount, total);
				}
			}
		});

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				option
					.setAmountSelected((optionGroup.getType() == GroupType.ONE_PER_MODEL || optionGroup.getType() == GroupType.ONE_PER_MODEL_EXEPT_ONE) ? 0
						: Math.max(0, option.getAmountSelected() - 1));
				updateValues(option, amount, total);
			}
		});
	}

	private void validateButtons(final UnitOption option, final View delete, final View add) {
		UIUtil.show(delete, option.getAmountSelected() > 0);
		UIUtil.show(add, optionGroup.canSelectMore(option) && option.isEnabled());
	}

	private void updateValues(final UnitOption option, final TextView amount, final TextView total) {
		optionGroup.validateAmounts();
		for (final IValueChangedNotifier handler : onValidate) {
			handler.onValueChanged();
		}
		if (notifier != null) {
			notifier.onValueChanged();
		}
		updateViews(option, amount, total);
	}

	private void updateViews(final UnitOption option, final TextView amount, final TextView total) {
		amount.setText("[" + option.getAmountSelected() + "]");
		UIUtil.show(amount, option.getAmountSelected() > 0);
		total.setText(String.valueOf(option.getAmountSelected() * option.getCosts()));
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
