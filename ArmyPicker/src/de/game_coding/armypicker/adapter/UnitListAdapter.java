package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.util.UIUtil;

public class UnitListAdapter extends BaseUnitAdapter {

	public interface DeleteHandler {
		void onDelete(Unit unit, int position);
	}

	private IValueChangedNotifier notifier;

	private DeleteHandler deleteHandler;

	private final boolean showHeader;

	public UnitListAdapter(final Context context, final List<Unit> units, final boolean showHeader) {
		super(context, R.layout.item_unit_list, units);
		this.showHeader = showHeader;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_unit_list, parent, false);
		}
		final Unit unit = getItem(position);

		final TextView title = (TextView) view.findViewById(R.id.unit_name);
		title.setText(unit.getName());

		final TextView costs = (TextView) view.findViewById(R.id.unit_points);
		costs.setText(String.valueOf(unit.getTotalCosts()));

		final TextView amount = (TextView) view.findViewById(R.id.unit_amount);
		amount.setText("[" + unit.getAmount() + "]");
		UIUtil.show(amount, unit.getMaxAmount() > 1);

		final LinearLayout options = (LinearLayout) view.findViewById(R.id.unit_options_list);
		final OptionGroupListAdapter adapter = newAdapter(new ArrayList<UnitOptionGroup>(), unit, view);
		buildEntries(options, adapter);

		final TextView optionPoints = (TextView) view.findViewById(R.id.unit_options_points);
		optionPoints.setText(String.valueOf(unit.getTotalOptionCosts()));
		optionPoints.setVisibility(View.GONE);

		final View rootView = view;

		final TextView type = (TextView) view.findViewById(R.id.unit_type_header);
		if (showHeader && (position == 0 || unit.getType() != getItem(position - 1).getType())) {
			type.setText("== " + getUnitTypeName(unit, view) + " ==");
			UIUtil.show(type);
		} else {
			type.setVisibility(View.GONE);
		}

		final View add = view.findViewById(R.id.unit_add);
		final View delete = view.findViewById(R.id.unit_delete);

		if (unit.getMaxAmount() == unit.getInitialAmount()) {
			add.setVisibility(View.INVISIBLE);
			add.setEnabled(false);
		}
		UIUtil.show(add, unit.getAmount() < unit.getMaxAmount());

		amount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (unit.getAmount() < unit.getMaxAmount()) {
					unit.setAmount(unit.getMaxAmount());
				} else {
					unit.setAmount(unit.getInitialAmount());
				}
				updateValues(unit, costs, amount, options, optionPoints, add, rootView);
			}
		});

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (unit.getAmount() < unit.getMaxAmount()) {
					unit.setAmount(unit.getAmount() + 1);
					updateValues(unit, costs, amount, options, optionPoints, add, rootView);
				}
			}
		});

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (unit.getAmount() == unit.getInitialAmount()) {
					if (deleteHandler != null) {
						deleteHandler.onDelete(unit, position);
					}
				} else {
					unit.setAmount(unit.getAmount() - 1);
					updateValues(unit, costs, amount, options, optionPoints, add, rootView);
				}
			}
		});

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				openCloseOptions(v, unit);
			}
		});
		return view;
	}

	private void updateValues(final Unit unit, final TextView costs, final TextView amount, final LinearLayout options,
		final TextView optionPoints, final View add, final View rootView) {
		costs.setText(String.valueOf(unit.getTotalCosts()));
		optionPoints.setText(String.valueOf(unit.getTotalOptionCosts()));
		amount.setText("[" + unit.getAmount() + "]");
		UIUtil.show(amount, unit.getMaxAmount() > 1);
		if (options.getChildCount() > 0) {
			buildEntries(options, newAdapter(unit.getOptions(), unit, rootView));
		}
		if (notifier != null) {
			notifier.onValueChanged();
		}
		UIUtil.show(add, unit.getAmount() < unit.getMaxAmount());
	}

	private OptionGroupListAdapter newAdapter(final List<UnitOptionGroup> entries, final Unit unit, final View rootView) {
		final OptionGroupListAdapter adapter = new OptionGroupListAdapter(getContext(), entries);
		adapter.setNotifier(new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				final TextView costs = (TextView) rootView.findViewById(R.id.unit_points);
				costs.setText(String.valueOf(unit.getTotalCosts()));
				final TextView optionPoints = (TextView) rootView.findViewById(R.id.unit_options_points);
				optionPoints.setText(String.valueOf(unit.getTotalOptionCosts()));
				if (notifier != null) {
					notifier.onValueChanged();
				}
			}
		});
		return adapter;
	}

	private void buildEntries(final LinearLayout rootView, final BaseAdapter adapter) {
		rootView.removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			rootView.addView(adapter.getView(i, null, rootView));
		}
	}

	private void openCloseOptions(final View view, final Unit unit) {
		final LinearLayout options = (LinearLayout) view.findViewById(R.id.unit_options_list);
		final TextView optionPoints = (TextView) view.findViewById(R.id.unit_options_points);
		if (options.getChildCount() == 0) {
			buildEntries(options, newAdapter(unit.getOptions(), unit, view));
			if ((unit.getOptions().size() > 0 && unit.getOptions().get(0).getOptions().size() > 1)
				|| unit.getOptions().size() > 1) {
				optionPoints.setVisibility(View.VISIBLE);
			}
		} else {
			buildEntries(options, newAdapter(new ArrayList<UnitOptionGroup>(), unit, view));
			optionPoints.setVisibility(View.GONE);
		}
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}

	public void setDeleteHandler(final DeleteHandler deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}
