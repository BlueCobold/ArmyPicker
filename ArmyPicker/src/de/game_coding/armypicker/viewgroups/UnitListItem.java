package de.game_coding.armypicker.viewgroups;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.adapter.OptionGroupListAdapter;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitOption;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.util.UIUtil;

@EViewGroup(R.layout.item_unit_list)
public class UnitListItem extends RelativeLayout {

	@ViewById(R.id.unit_name)
	TextView title;

	@ViewById(R.id.unit_points)
	TextView costs;

	@ViewById(R.id.unit_source)
	TextView source;

	@ViewById(R.id.unit_amount)
	TextView amount;

	@ViewById(R.id.unit_options_list)
	LinearLayout options;

	@ViewById(R.id.unit_options_points)
	TextView optionPoints;

	@ViewById(R.id.unit_options_summary)
	TextView summary;

	@ViewById(R.id.unit_type_header)
	TextView type;

	@ViewById(R.id.unit_add)
	View add;

	@ViewById(R.id.unit_delete)
	View delete;

	private Unit unit;

	private DeleteHandler<Unit> deleteHandler;

	private IValueChangedNotifier notifier;

	private boolean showSummaries;

	public UnitListItem(final Context context) {
		super(context);
	}

	public void bind(final Unit item, final boolean showSummaries) {
		unit = item;
		this.showSummaries = showSummaries;

		title.setText(unit.getName());
		costs.setText(String.valueOf(unit.getTotalCosts()));

		if (unit.getSubtitle() != null && !unit.getSubtitle().isEmpty()) {
			source.setText(unit.getSubtitle());
			source.setVisibility(View.VISIBLE);
		} else {
			source.setVisibility(View.GONE);
		}

		amount.setText("[" + unit.getAmount() + "]");
		UIUtil.show(amount, unit.getMaxAmount() > 1);

		final OptionGroupListAdapter adapter = newAdapter(new ArrayList<UnitOptionGroup>());
		buildEntries(adapter);

		optionPoints.setText(String.valueOf(unit.getTotalOptionCosts()));
		optionPoints.setVisibility(View.GONE);

		final String selectedOptions = getSelectedOptions();

		if (showSummaries && !selectedOptions.isEmpty()) {
			summary.setVisibility(View.VISIBLE);
			summary.setText(selectedOptions);
		} else {
			summary.setVisibility(View.GONE);
		}

		UIUtil.show(add, unit.getAmount() < unit.getMaxAmount());

		if (unit.getMaxAmount() == unit.getInitialAmount()) {
			add.setVisibility(View.INVISIBLE);
			add.setEnabled(false);
		}

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				openCloseOptions();
			}
		});
	}

	@Click(R.id.unit_amount)
	void onAmountClicked() {

		if (unit.getAmount() < unit.getMaxAmount()) {
			unit.setAmount(unit.getMaxAmount());
		} else {
			unit.setAmount(unit.getInitialAmount());
		}
		updateValues();
	}

	@Click(R.id.unit_add)
	void onAddClicked() {
		if (unit.getAmount() < unit.getMaxAmount()) {
			unit.setAmount(unit.getAmount() + 1);
			updateValues();
		}
	}

	@Click(R.id.unit_delete)
	void onDeleteClicked() {

		if (unit.getAmount() == unit.getInitialAmount()) {
			if (deleteHandler != null) {
				deleteHandler.onDelete(unit);
			}
		} else {
			unit.setAmount(unit.getAmount() - 1);
			updateValues();
		}
	}

	private void openCloseOptions() {
		if (options.getChildCount() == 0) {
			buildEntries(newAdapter(unit.getOptions()));
			summary.setVisibility(View.GONE);
			if ((unit.getOptions().size() > 0 && unit.getOptions().get(0).getOptions().size() > 1)
				|| unit.getOptions().size() > 1) {
				optionPoints.setVisibility(View.VISIBLE);
			}
		} else {
			final String selectedOptions = getSelectedOptions();
			if (showSummaries && !selectedOptions.isEmpty()) {
				summary.setVisibility(View.VISIBLE);
				summary.setText(selectedOptions);
			}
			buildEntries(newAdapter(new ArrayList<UnitOptionGroup>()));
			optionPoints.setVisibility(View.GONE);
		}
	}

	private void buildEntries(final BaseAdapter adapter) {
		options.removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			options.addView(adapter.getView(i, null, this));
		}
	}

	private OptionGroupListAdapter newAdapter(final List<UnitOptionGroup> entries) {
		final OptionGroupListAdapter adapter = new OptionGroupListAdapter(getContext(), entries);
		adapter.setNotifier(new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				costs.setText(String.valueOf(unit.getTotalCosts()));
				optionPoints.setText(String.valueOf(unit.getTotalOptionCosts()));
				if (notifier != null) {
					notifier.onValueChanged();
				}
			}
		});
		return adapter;
	}

	private void updateValues() {
		costs.setText(String.valueOf(unit.getTotalCosts()));
		optionPoints.setText(String.valueOf(unit.getTotalOptionCosts()));
		amount.setText("[" + unit.getAmount() + "]");
		UIUtil.show(amount, unit.getMaxAmount() > 1);
		if (options.getChildCount() > 0) {
			buildEntries(newAdapter(unit.getOptions()));
		}
		if (notifier != null) {
			notifier.onValueChanged();
		}
		UIUtil.show(add, unit.getAmount() < unit.getMaxAmount());
	}

	private String getSelectedOptions() {
		String result = new String();
		for (final UnitOptionGroup group : unit.getOptions()) {
			for (final UnitOption option : group.getOptions()) {
				if (option.getAmountSelected() == 0) {
					continue;
				}
				if (!result.isEmpty()) {
					result += ", ";
				}
				result += option.getLongName();
				if (option.getAmountSelected() > 1) {
					result += " [" + option.getAmountSelected() + "]";
				}
			}
		}
		return result;
	}

	public void setHeader(final String unitTypeName) {
		if (unitTypeName != null) {
			type.setText("== " + unitTypeName + " ==");
			UIUtil.show(type);
		} else {
			type.setVisibility(View.GONE);
		}
	}

	public void setDeleteHandler(final DeleteHandler<Unit> handler) {
		deleteHandler = handler;
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}

}
