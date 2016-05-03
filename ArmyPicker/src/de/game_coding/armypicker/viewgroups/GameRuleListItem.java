package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.GameRule;

@EViewGroup(R.layout.item_unit_gamerule_list)
public class GameRuleListItem extends LinearLayout {

	public GameRuleListItem(Context context) {
		super(context);
	}

	@ViewById(R.id.rule_title)
	protected TextView title;

	@ViewById(R.id.rule_description)
	protected TextView description;

	public void bind(final GameRule item) {
		title.setText(item.getTitle());
		description.setText(item.getDescription());
	}
}
