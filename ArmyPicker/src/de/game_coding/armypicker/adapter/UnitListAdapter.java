package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitOptionGroup;

public class UnitListAdapter extends ArrayAdapter<Unit> {

	public interface DeleteHandler {
		void onDelete(Unit unit, int position);
	}

	private IValueChangedNotifier notifier;

	private DeleteHandler deleteHandler;

	public UnitListAdapter(final Context context, final List<Unit> units) {
		super(context, R.layout.item_unit_list, units);
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

		final LinearLayout options = (LinearLayout) view.findViewById(R.id.unit_options_list);
		final OptionGroupListAdapter adapter = newAdapter(new ArrayList<UnitOptionGroup>(), unit, view);
		buildEntries(options, adapter);

		final View rootView = view;

		final View add = view.findViewById(R.id.unit_add);
		if (unit.getMaxAmount() == unit.getInitialAmount()) {
			add.setVisibility(View.INVISIBLE);
			add.setEnabled(false);
		}
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (unit.getAmount() < unit.getMaxAmount()) {
					unit.setAmount(unit.getAmount() + 1);
					costs.setText(String.valueOf(unit.getTotalCosts()));
					amount.setText("[" + unit.getAmount() + "]");
					if (notifier != null) {
						notifier.onValueChanged();
					}
					if (options.getChildCount() > 0) {
						buildEntries(options, newAdapter(unit.getOptions(), unit, rootView));
					}
				}
			}
		});

		final View delete = view.findViewById(R.id.unit_delete);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (unit.getAmount() == unit.getInitialAmount()) {
					if (deleteHandler != null) {
						deleteHandler.onDelete(unit, position);
					}
				} else {
					unit.setAmount(unit.getAmount() - 1);
					costs.setText(String.valueOf(unit.getTotalCosts()));
					amount.setText("[" + unit.getAmount() + "]");
					if (options.getChildCount() > 0) {
						buildEntries(options, newAdapter(unit.getOptions(), unit, rootView));
					}
				}
				if (notifier != null) {
					notifier.onValueChanged();
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

	private OptionGroupListAdapter newAdapter(final List<UnitOptionGroup> entries, final Unit unit, final View rootView) {
		final OptionGroupListAdapter adapter = new OptionGroupListAdapter(getContext(), entries);
		adapter.setNotifier(new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				final TextView costs = (TextView) rootView.findViewById(R.id.unit_points);
				costs.setText(String.valueOf(unit.getTotalCosts()));
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
		if (options.getChildCount() == 0) {
			buildEntries(options, newAdapter(unit.getOptions(), unit, view));
		} else {
			buildEntries(options, newAdapter(new ArrayList<UnitOptionGroup>(), unit, view));
		}
	}

	public void setNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}

	public void setDeleteHandler(final DeleteHandler deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}
