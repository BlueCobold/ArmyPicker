package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Army;

@EViewGroup(R.layout.item_unit_type_list)
public class ArmySelectionListItem extends RelativeLayout {

	@ViewById(R.id.list_item_name)
	protected TextView title;

	@ViewById(R.id.unit_type_header)
	protected TextView type;

	@ViewById(R.id.list_item_points)
	protected TextView points;

	public ArmySelectionListItem(final Context context) {
		super(context);
	}

	@AfterViews
	protected void init() {
		type.setVisibility(View.GONE);
	}

	public void bind(final Army army) {
		title.setText(army.getName());
		points.setText("v" + army.getTemplateVersion());
	}
}
