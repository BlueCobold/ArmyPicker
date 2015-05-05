package de.game_coding.armypicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.UnitOption;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.model.UnitOptionGroup.GroupType;

public class OptionListAdapter extends ArrayAdapter<UnitOption> {

	private final UnitOptionGroup optionGroup;

	private IValueChangedNotifier notifier;

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

		final TextView total = (TextView) view.findViewById(R.id.option_points_total);
		total.setText(String.valueOf(option.getAmountSelected() * option.getCosts()));

		final View add = view.findViewById(R.id.option_add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (optionGroup.canSelectMore()) {
					option.setAmountSelected(option.getAmountSelected()
							+ (optionGroup.getType() == GroupType.ONE_PER_MODEL ? optionGroup.getLimit() : 1));
					optionGroup.validateAmounts();
					amount.setText("[" + option.getAmountSelected() + "]");
					total.setText(String.valueOf(option.getAmountSelected() * option.getCosts()));
					if (notifier != null) {
						notifier.onValueChanged();
					}
				}
			}
		});

		final View delete = view.findViewById(R.id.option_delete);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				option.setAmountSelected((optionGroup.getType() == GroupType.ONE_PER_MODEL) ? 0 : Math.max(0,
						option.getAmountSelected() - 1));
				optionGroup.validateAmounts();
				amount.setText("[" + option.getAmountSelected() + "]");
				total.setText(String.valueOf(option.getAmountSelected() * option.getCosts()));
				if (notifier != null) {
					notifier.onValueChanged();
				}
			}
		});
		return view;
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}
}
