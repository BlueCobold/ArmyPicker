package de.game_coding.armypicker.viewgroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.adapter.BattalionRequirementListAdapter;
import de.game_coding.armypicker.adapter.UnitGameRuleListAdapter;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Battalion;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitRequirement;
import de.game_coding.armypicker.util.UnitUtils;
import de.game_coding.armypicker.viewmodel.BattalionRequirementDetails;

@EViewGroup(R.layout.item_battalion_list)
public class BattalionListItem extends RelativeLayout {

	@ViewById(R.id.battalion_name)
	protected TextView name;

	@ViewById(R.id.battalion_amount)
	protected TextView amount;

	@ViewById(R.id.battalion_description)
	protected TextView description;

	@ViewById(R.id.battalion_rules)
	protected LinearLayout rules;

	@ViewById(R.id.battalion_delete)
	protected View deleteButton;

	@ViewById(R.id.battalion_add)
	protected View addButton;

	@ViewById(R.id.battalion_sub_list)
	protected LinearLayout requirements;

	private DeleteHandler<Battalion> deleteHandler;

	private Battalion battalion;

	private ItemClickedListener<BattalionRequirement> addSubHandler;

	private DeleteHandler<BattalionRequirement> deleteSubHandler;

	private ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>> addUnitHandler;

	private IValueChangedNotifier notifier;

	private ItemClickedListener<Collection<Unit>> editUnitHandler;

	public BattalionListItem(final Context context) {
		super(context);
	}

	@Click(R.id.battalion_delete)
	protected void onDeleteClick() {
		if (deleteHandler != null) {
			deleteHandler.onDelete(battalion);
		}
	}

	@Click(R.id.battalion_add)
	protected void onAddClick() {
		if (addSubHandler != null) {
			addSubHandler.onItemClicked(battalion.getRequirement());
		}
	}

	@Click(R.id.battalion_name)
	protected void onNameClick() {
		if (editUnitHandler != null) {
			editUnitHandler.onItemClicked(getAllUnits(battalion.getRequirement()));
		}
	}

	private Collection<Unit> getAllUnits(final BattalionRequirement item) {
		final List<Unit> units = new ArrayList<Unit>();
		units.addAll(item.getAssignedUnits());
		for (final BattalionRequirement sub : item.getAssignedSubBattalions()) {
			units.addAll(getAllUnits(sub));
		}
		return units;
	}

	public void bind(final Battalion item, final boolean deletable, final BattalionRequirementDetails details) {
		this.battalion = item;
		name.setClickable(deletable);
		name.setText(item.getTypeName());
		amount.setVisibility(deletable ? View.VISIBLE : View.GONE);
		amount.setText("[" + item.getRequirement().getAssignedSubBattalions().size() + "/"
			+ item.getRequirement().getMaxCount() + "]");
		deleteButton.setVisibility(deletable ? View.VISIBLE : View.GONE);
		addButton.setVisibility(deletable ? View.VISIBLE : View.GONE);
		requirements.removeAllViews();
		if (!item.getRequirement().getAssignedSubBattalions().isEmpty() && deletable) {
			final BattalionRequirementListAdapter adapter = new BattalionRequirementListAdapter(getContext(),
				item.getRequirement().getAssignedSubBattalions(), false, details);
			adapter.setAddHandler(addSubHandler);
			adapter.setDeleteHandler(deleteSubHandler);
			adapter.setAddUnitHandler(addUnitHandler);
			adapter.setChangeNotifier(notifier);
			adapter.setEditUnitHandler(editUnitHandler);
			adapter.fillWithItems(requirements, this);
		}

		final Collection<GameRule> gameRules = item.getRules();
		if (details == BattalionRequirementDetails.SUMMARIES && !gameRules.isEmpty()) {
			description.setText(UnitUtils.getRulesSummaries(gameRules));
			description.setVisibility(View.VISIBLE);
		} else {
			description.setVisibility(View.GONE);
		}

		if (details == BattalionRequirementDetails.RULES && !gameRules.isEmpty()) {
			final UnitGameRuleListAdapter ruleAdapter = new UnitGameRuleListAdapter(getContext(), gameRules);
			rules.removeAllViews();
			ruleAdapter.fillWithItems(rules, this);
			rules.setVisibility(View.VISIBLE);
		} else {
			rules.setVisibility(View.GONE);
		}
	}

	public void setDeleteHandler(final DeleteHandler<Battalion> handler) {
		deleteHandler = handler;
	}

	public void setAddSubHandler(final ItemClickedListener<BattalionRequirement> handler) {
		addSubHandler = handler;
	}

	public void setDeleteSubHandler(final DeleteHandler<BattalionRequirement> handler) {
		deleteSubHandler = handler;
	}

	public void setAddUnitHandler(final ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>> handler) {
		addUnitHandler = handler;
	}

	public void setChangeNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}

	public void setEditUnitHandler(final ItemClickedListener<Collection<Unit>> handler) {
		this.editUnitHandler = handler;
	}
}
