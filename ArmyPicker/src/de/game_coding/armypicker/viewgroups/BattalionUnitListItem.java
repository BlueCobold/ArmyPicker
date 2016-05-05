package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.viewmodel.BattalionRequirementDetails;

@EViewGroup(R.layout.item_battalion_unit_list)
public class BattalionUnitListItem extends RelativeLayout {

	@ViewById(R.id.unit_req_name)
	protected TextView name;

	@ViewById(R.id.unit_req_delete)
	protected View deleteButton;

	@ViewById(R.id.unit_req_add)
	protected View addButton;

	@ViewById(R.id.unit_req_amount)
	protected TextView amount;

	@ViewById(R.id.unit_req_description)
	protected TextView description;

	private DeleteHandler<String> deleteHandler;

	private String unitName;

	private ItemClickedListener<String> addHandler;

	private ItemClickedListener<String> clickHandler;

	public BattalionUnitListItem(final Context context) {
		super(context);
	}

	@Click(R.id.unit_req_delete)
	protected void onDeleteClick() {
		if (deleteHandler != null) {
			deleteHandler.onDelete(unitName);
		}
	}

	@Click(R.id.unit_req_add)
	protected void onAddClick() {
		if (addHandler != null) {
			addHandler.onItemClicked(unitName);
		}
	}

	@Click(R.id.unit_req_name)
	protected void onNameClick() {
		if (clickHandler != null) {
			clickHandler.onItemClicked(unitName);
		}
	}

	public void bind(final String item, final int count, final boolean minOk, final int maxCount,
		final boolean deletable, final boolean addable, final BattalionRequirementDetails details,
		final String summary) {
		this.unitName = item;
		name.setText(unitName);
		amount.setText("[" + count + "/" + maxCount + "]");
		amount.setTextColor(minOk ? name.getCurrentTextColor() : Color.RED);
		deleteButton.setVisibility(deletable ? View.VISIBLE : View.INVISIBLE);
		addButton.setVisibility(addable ? View.VISIBLE : View.INVISIBLE);
		description.setText(summary);
		description.setVisibility(
			details != BattalionRequirementDetails.NONE && !summary.isEmpty() ? View.VISIBLE : View.GONE);
	}

	public void setDeleteHandler(final DeleteHandler<String> handler) {
		deleteHandler = handler;
	}

	public void setAddHandler(final ItemClickedListener<String> handler) {
		addHandler = handler;
	}

	public void setClickHandler(final ItemClickedListener<String> handler) {
		clickHandler = handler;
	}
}
