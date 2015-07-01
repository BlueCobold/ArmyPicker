package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.util.UIUtil;

@EViewGroup(R.layout.item_unit_type_list)
public class UnitSelectionListItem extends RelativeLayout {

	@ViewById(R.id.list_item_name)
	TextView title;

	@ViewById(R.id.unit_type_header)
	TextView type;

	@ViewById(R.id.list_item_source)
	TextView source;

	@ViewById(R.id.list_item_points)
	TextView points;

	public UnitSelectionListItem(final Context context) {
		super(context);
	}

	@AfterViews
	void init() {
		type.setVisibility(View.GONE);
	}

	public void bind(final Unit unit) {
		title.setText(unit.getName());
		points.setText(String.valueOf(unit.getTotalCosts()));
		if (unit.getSubtitle() != null && !unit.getSubtitle().isEmpty()) {
			source.setText(unit.getSubtitle());
			source.setVisibility(View.VISIBLE);
		} else {
			source.setVisibility(View.GONE);
		}
	}

	public void setHeader(final String unitTypeName) {
		if (unitTypeName != null) {
			type.setText("== " + unitTypeName + " ==");
			UIUtil.show(type);
		} else {
			type.setVisibility(View.GONE);
		}
	}
}
