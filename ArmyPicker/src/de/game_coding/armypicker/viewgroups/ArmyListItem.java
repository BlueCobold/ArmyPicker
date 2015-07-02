package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.EditHandler;
import de.game_coding.armypicker.model.Army;

@EViewGroup(R.layout.item_army_list)
public class ArmyListItem extends RelativeLayout {

	@ViewById(R.id.list_item_name)
	protected TextView title;

	@ViewById(R.id.list_item_points)
	protected TextView points;

	private DeleteHandler<Army> deleteHandler;

	private EditHandler<Army> editHandler;

	private Army army;

	public ArmyListItem(final Context context) {
		super(context);
	}

	public void setDeleteHandler(final DeleteHandler<Army> handler) {
		deleteHandler = handler;
	}

	public void setEditHandler(final EditHandler<Army> handler) {
		editHandler = handler;
	}

	@Click(R.id.list_item_delete)
	protected void onDeleteArmy() {
		if (deleteHandler != null) {
			deleteHandler.onDelete(army);
		}
	}

	@Click(R.id.list_item_edit)
	protected void onEditArmy() {
		if (editHandler != null) {
			editHandler.onEdit(army);
		}
	}

	public void bind(final Army item) {
		army = item;
		title.setText(army.getName());
		points.setText(String.valueOf(army.getTotalCosts()));
	}
}
