package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Unit;

public class BaseUnitAdapter extends ArrayAdapter<Unit> {

	public BaseUnitAdapter(final Context context, final int resource) {
		super(context, resource);
	}

	public BaseUnitAdapter(final Context context, final int resource, final int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public BaseUnitAdapter(final Context context, final int resource, final Unit[] objects) {
		super(context, resource, objects);
	}

	public BaseUnitAdapter(final Context context, final int resource, final List<Unit> objects) {
		super(context, resource, objects);
	}

	public BaseUnitAdapter(final Context context, final int resource, final int textViewResourceId, final Unit[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public BaseUnitAdapter(final Context context, final int resource, final int textViewResourceId,
		final List<Unit> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	protected String getUnitTypeName(final Unit unit, final View view) {
		switch (unit.getType()) {
		case ELITE:
			return view.getResources().getString(R.string.type_elite);
		case FAST_ATTACK:
			return view.getResources().getString(R.string.type_fast);
		case HQ:
			return view.getResources().getString(R.string.type_hq);
		case STANDARD:
			return view.getResources().getString(R.string.type_standard);
		case SUPPORT:
			return view.getResources().getString(R.string.type_heavy);

		default:
			break;
		}
		return "";
	}

}