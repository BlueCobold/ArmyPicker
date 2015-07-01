package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Unit;

public abstract class BaseUnitAdapter<V extends View> extends BaseAdapter<Unit, V> {

	private ItemClickedListener<Unit> longClickHandler;

	public BaseUnitAdapter(final Context context, final Unit[] objects) {
		super(context, objects);
	}

	public BaseUnitAdapter(final Context context, final List<Unit> objects) {
		super(context, objects);
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
		case LORD_OF_WAR:
			return view.getResources().getString(R.string.type_low);

		default:
			break;
		}
		return "";
	}

	public void setLongClickHandler(final ItemClickedListener<Unit> longClickHandler) {
		this.longClickHandler = longClickHandler;
	}

	protected ItemClickedListener<Unit> getLongClickHandler() {
		return longClickHandler;
	}
}