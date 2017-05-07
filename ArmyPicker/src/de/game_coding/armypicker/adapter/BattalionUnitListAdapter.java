package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.BattalionChoice;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitOption;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.model.UnitRequirement;
import de.game_coding.armypicker.viewgroups.BattalionUnitListItem;
import de.game_coding.armypicker.viewgroups.BattalionUnitListItem_;
import de.game_coding.armypicker.viewmodel.BattalionRequirementDetails;

public class BattalionUnitListAdapter extends BaseAdapter<String, BattalionUnitListItem> {

	private final BattalionRequirement requirement;

	private DeleteHandler<String> deleteHandler;

	private ItemClickedListener<String> addHandler;

	private ItemClickedListener<String> editUnitHandler;

	private final BattalionRequirementDetails details;

	public BattalionUnitListAdapter(final Context context, final Collection<UnitRequirement> objects,
		final BattalionRequirement requirement, final BattalionRequirementDetails details) {
		super(context, getNames(objects, requirement));
		this.requirement = requirement;
		this.details = details;
	}

	private static List<String> getNames(final Collection<UnitRequirement> objects, final BattalionRequirement item) {
		final List<String> names = new ArrayList<String>();
		for (final UnitRequirement unit : objects) {
			if (!names.contains(unit.getUnitName())) {
				final String name = unit.getUnitName();
				final BattalionRequirement parent = getParent(name, item);
				final BattalionChoice choice = choiceType(name, parent);
				int max = maxCount(name, parent);
				final int selectedUnits = count(name, parent);
				if (choice == BattalionChoice.X_OF) {
					max += selectedUnits - parent.getAssignedUnits().size();
				}
				if (max > 0) {
					names.add(unit.getUnitName());
				}
			}
		}
		return names;
	}

	@Override
	protected BattalionUnitListItem buildNewView() {
		return BattalionUnitListItem_.build(getContext());
	}

	@Override
	protected void fillView(final BattalionUnitListItem view, final String item, final int position,
		final ViewGroup parent) {
		final BattalionRequirement parentRequirement = getParent(item, requirement);

		final int count = count(item, parentRequirement);
		view.setAddHandler(addHandler);
		view.setDeleteHandler(deleteHandler);
		view.setClickHandler(new ItemClickedListener<String>() {
			@Override
			public void onItemClicked(final String clickedItem) {
				onClickItem(clickedItem);
			}
		});
		final BattalionChoice choice = choiceType(item, parentRequirement);
		final int selectedUnits = count(item, parentRequirement);
		final int min = minCount(item);
		int max = maxCount(item, parentRequirement);
		if (choice == BattalionChoice.X_OF) {
			max += selectedUnits - parentRequirement.getAssignedUnits().size();
		}
		final boolean minOk = (choice == BattalionChoice.X_OF
			&& parentRequirement.getAssignedUnits().size() >= parentRequirement.getMinCount())
			|| (choice == BattalionChoice.X_OF_EACH && count >= min);
		final boolean canDelete = count > 0
			&& (count > min || (choice == BattalionChoice.X_OF && parentRequirement.getRequiredUnits().size() > 1));
		final boolean canAdd = selectedUnits < max;
		view.bind(item, count, minOk, max, canDelete, canAdd, details, getSelectedOptions(item));
	}

	private static BattalionRequirement getParent(final String item, final BattalionRequirement requirement) {
		for (final UnitRequirement unit : requirement.getRequiredUnits()) {
			if (unit.getUnitName().equals(item)) {
				return requirement;
			}
		}
		for (final BattalionRequirement sub : requirement.getAssignedSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final UnitRequirement unit : sub.getRequiredUnits()) {
				if (unit.getUnitName().equals(item)) {
					return sub;
				}
			}
		}
		for (final BattalionRequirement sub : requirement.getRequiredSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final UnitRequirement unit : sub.getRequiredUnits()) {
				if (unit.getUnitName().equals(item)) {
					return sub;
				}
			}
		}
		return null;
	}

	private boolean onClickItem(final String item) {
		if (editUnitHandler != null) {
			editUnitHandler.onItemClicked(item);
			return true;
		}
		return false;
	}

	private String getSelectedOptions(final String item) {
		final List<Unit> units = new ArrayList<Unit>();
		for (final Unit unit : requirement.getAssignedUnits()) {
			if (unit.getName().equals(item)) {
				units.add(unit);
				break;
			}
		}
		for (final BattalionRequirement sub : requirement.getAssignedSubBattalions()) {
			if (!sub.isMeta()) {
				continue;
			}
			for (final Unit unit : sub.getAssignedUnits()) {
				if (unit.getName().equals(item)) {
					units.add(unit);
				}
			}
		}
		String result = new String();
		for (final Unit unit : units) {
			for (final UnitOptionGroup group : unit.getOptions()) {
				for (final UnitOption option : group.getOptions()) {
					if (option.getAmountSelected() == 0) {
						continue;
					}
					if (!result.isEmpty()) {
						result += ", ";
					}
					result += option.getLongName();
					if (option.getAmountSelected() > 1) {
						result += " [" + option.getAmountSelected() + "]";
					}
				}
			}
		}
		return result;
	}

	private static BattalionChoice choiceType(final String item, final BattalionRequirement req) {
		for (final UnitRequirement unit : req.getRequiredUnits()) {
			if (unit.getUnitName().equals(item)) {
				return req.getChoice();
			}
		}
		return null;
	}

	private int minCount(final String item) {
		UnitRequirement requiredUnit = null;
		for (final UnitRequirement unit : requirement.getRequiredUnits()) {
			if (unit.getUnitName().equals(item)) {
				requiredUnit = unit;
			}
		}
		if (requiredUnit == null) {
			for (final BattalionRequirement sub : requirement.getRequiredSubBattalions()) {
				if (!sub.isMeta()) {
					continue;
				}
				for (final UnitRequirement unit : sub.getRequiredUnits()) {
					if (unit.getUnitName().equals(item)) {
						requiredUnit = unit;
						break;
					}
				}
			}
		}
		if (requiredUnit == null) {
			return 0;
		}
		return requiredUnit.getMin();
	}

	private static int maxCount(final String item, final BattalionRequirement req) {
		UnitRequirement requiredUnit = null;
		final BattalionRequirement parent = req;
		for (final UnitRequirement unit : req.getRequiredUnits()) {
			if (unit.getUnitName().equals(item)) {
				requiredUnit = unit;
			}
		}
		if (requiredUnit == null) {
			return 0;
		}
		return parent.getMaxCount() * requiredUnit.getMax();
	}

	private static int count(final String item, final BattalionRequirement req) {
		int count = 0;
		for (final Unit unit : req.getAssignedUnits()) {
			if (unit.getName().equals(item)) {
				count++;
			}
		}
		return count;
	}

	public void setDeleteHandler(final DeleteHandler<String> handler) {
		deleteHandler = handler;
	}

	public void setAddHandler(final ItemClickedListener<String> handler) {
		addHandler = handler;
	}

	public void setEditUnitHandler(final ItemClickedListener<String> handler) {
		this.editUnitHandler = handler;
	}
}
