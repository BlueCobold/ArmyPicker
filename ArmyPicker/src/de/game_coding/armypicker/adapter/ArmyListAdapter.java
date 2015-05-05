package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Army;

public class ArmyListAdapter extends ArrayAdapter<Army> {

	public interface DeleteHandler {
		void onDelete(Army army, int position);
	}

	private DeleteHandler deleteHandler;

	public ArmyListAdapter(final Context context, final List<Army> armies) {
		super(context, R.layout.item_army_list, armies);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_army_list, parent, false);
		}

		final Army army = getItem(position);

		final TextView title = (TextView) view.findViewById(R.id.list_item_name);
		title.setText(getItem(position).getName());

		final TextView points = (TextView) view.findViewById(R.id.list_item_points);
		points.setText(String.valueOf(army.getTotalCosts()));

		final View delete = view.findViewById(R.id.list_item_delete);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				if (deleteHandler != null) {
					deleteHandler.onDelete(army, position);
				}
			}
		});
		return view;
	}

	public void setDeleteHandler(final DeleteHandler deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}
