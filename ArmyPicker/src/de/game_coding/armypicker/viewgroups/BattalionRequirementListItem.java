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
import de.game_coding.armypicker.adapter.BattalionUnitListAdapter;
import de.game_coding.armypicker.adapter.UnitGameRuleListAdapter;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.BattalionChoice;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitRequirement;
import de.game_coding.armypicker.util.UnitUtils;
import de.game_coding.armypicker.viewmodel.BattalionRequirementDetails;

@EViewGroup(R.layout.item_battalion_requirement_list)
public class BattalionRequirementListItem extends RelativeLayout {

	@ViewById(R.id.battalion_req_name)
	protected TextView name;

	@ViewById(R.id.battalion_req_count)
	protected TextView count;

	@ViewById(R.id.battalion_req_description)
	protected TextView description;

	@ViewById(R.id.battalion_req_rules)
	protected LinearLayout rules;

	@ViewById(R.id.battalion_req_unit_list)
	protected LinearLayout unitList;

	@ViewById(R.id.battalion_req_add)
	protected View addButton;

	@ViewById(R.id.battalion_req_delete)
	protected View deleteButton;

	private BattalionRequirement item;

	private ItemClickedListener<BattalionRequirement> addHandler;

	private DeleteHandler<BattalionRequirement> deleteHandler;

	private ItemClickedListener<Collection<Unit>> editUnitHandler;

	private ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>> addUnitHandler;

	private IValueChangedNotifier notifier;

	public BattalionRequirementListItem(final Context context) {
		super(context);
	}

	public void bind(final BattalionRequirement item, final boolean readOnly,
		final BattalionRequirementDetails details) {
		this.item = item;
		name.setClickable(!readOnly);
		name.setText(item.getName());
		// name.setClickable(true);
		unitList.removeAllViews();
		if (!readOnly) {
			final Collection<UnitRequirement> units = new ArrayList<UnitRequirement>();
			units.addAll(item.getRequiredUnits());
			for (final BattalionRequirement sub : item.getAssignedSubBattalions()) {
				if (sub.isMeta()) {
					units.addAll(sub.getRequiredUnits());
				}
			}
			if (!units.isEmpty()) {
				final BattalionUnitListAdapter adapter = new BattalionUnitListAdapter(getContext(), units, item,
					details);
				adapter.setAddHandler(new ItemClickedListener<String>() {
					@Override
					public void onItemClicked(final String unitName) {
						addUnitHandler.onItemClicked(new Pair<UnitRequirement, BattalionRequirement>(
							findTemplate(unitName), findAssignmentParent(unitName)));
						bind(item, readOnly, details);
					}
				});
				adapter.setEditUnitHandler(new ItemClickedListener<String>() {
					@Override
					public void onItemClicked(final String unitName) {
						if (editUnitHandler != null) {
							editUnitHandler.onItemClicked(findAll(unitName));
						}
					}
				});
				adapter.setDeleteHandler(new DeleteHandler<String>() {
					@Override
					public void onDelete(final String unitName) {
						final BattalionRequirement parent = findParent(unitName);
						parent.removeUnit(findOne(unitName));
						bind(item, readOnly, details);
						if (notifier != null) {
							notifier.onValueChanged();
						}
					}
				});
				adapter.fillWithItems(unitList, this);
			}
		}
		int selected = item.getAssignedSubBattalions().size();
		if (item.getChoice() == BattalionChoice.X_OF && item.getRequiredSubBattalions().isEmpty()) {
			selected = item.getAssignedUnits().size();
		}
		count.setText("[" + selected + "/" + item.getMaxCount() + "]");
		count
			.setVisibility((readOnly || item.getChoice() == BattalionChoice.X_OF_EACH) ? View.INVISIBLE : View.VISIBLE);
		addButton.setVisibility((readOnly || !hasMoreSubs(item)) ? View.INVISIBLE : View.VISIBLE);
		deleteButton.setVisibility(readOnly ? View.INVISIBLE : View.VISIBLE);

		rules.setVisibility(
			item.getRules() != null && item.getRules().size() > 0 && details == BattalionRequirementDetails.RULES
				? View.VISIBLE : View.GONE);

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

	private static boolean hasMoreSubs(final BattalionRequirement item) {
		for (final BattalionRequirement sub : item.getRequiredSubBattalions()) {
			if (!sub.isMeta()) {
				return true;
			}
		}
		return false;
	}

	@Click(R.id.battalion_req_name)
	protected void onNameClicked() {
		if (editUnitHandler != null) {
			editUnitHandler.onItemClicked(getAllUnits(item));
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

	@Click(R.id.battalion_req_add)
	protected void onAddBattalionRequirement() {
		if (addHandler != null) {
			addHandler.onItemClicked(item);
		}
	}

	@Click(R.id.battalion_req_delete)
	protected void onDeleteBattalionRequirement() {
		if (deleteHandler != null) {
			deleteHandler.onDelete(item);
		}
	}

	private UnitRequirement findTemplate(final String name) {
		for (final UnitRequirement unit : item.getRequiredUnits()) {
			if (unit.getUnitName().equals(name)) {
				return unit;
			}
		}
		for (final BattalionRequirement sub : item.getRequiredSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final UnitRequirement unit : sub.getRequiredUnits()) {
				if (unit.getUnitName().equals(name)) {
					return unit;
				}
			}
		}
		return null;
	}

	private BattalionRequirement findAssignmentParent(final String name) {
		for (final UnitRequirement unit : item.getRequiredUnits()) {
			if (unit.getUnitName().equals(name)) {
				return item;
			}
		}
		for (final BattalionRequirement sub : item.getAssignedSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final UnitRequirement unit : sub.getRequiredUnits()) {
				if (unit.getUnitName().equals(name)) {
					return sub;
				}
			}
		}
		return null;
	}

	private Unit findOne(final String name) {
		for (final Unit unit : item.getAssignedUnits()) {
			if (unit.getName().equals(name)) {
				return unit;
			}
		}
		for (final BattalionRequirement sub : item.getAssignedSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final Unit unit : sub.getAssignedUnits()) {
				if (unit.getName().equals(name)) {
					return unit;
				}
			}
		}
		return null;
	}

	private Collection<Unit> findAll(final String name) {
		final List<Unit> found = new ArrayList<Unit>();
		for (final Unit unit : item.getAssignedUnits()) {
			if (unit.getName().equals(name)) {
				found.add(unit);
			}
		}
		for (final BattalionRequirement sub : item.getAssignedSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final Unit unit : sub.getAssignedUnits()) {
				if (unit.getName().equals(name)) {
					found.add(unit);
				}
			}
		}
		return found;
	}

	private BattalionRequirement findParent(final String name) {
		for (final Unit unit : item.getAssignedUnits()) {
			if (unit.getName().equals(name)) {
				return item;
			}
		}
		for (final BattalionRequirement sub : item.getAssignedSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final Unit unit : sub.getAssignedUnits()) {
				if (unit.getName().equals(name)) {
					return sub;
				}
			}
		}
		return null;
	}

	public void setAddHandler(final ItemClickedListener<BattalionRequirement> handler) {
		addHandler = handler;
	}

	public void setDeleteHandler(final DeleteHandler<BattalionRequirement> handler) {
		deleteHandler = handler;
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
