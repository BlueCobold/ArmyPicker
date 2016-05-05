package de.game_coding.armypicker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.game_coding.armypicker.adapter.BattalionListAdapter;
import de.game_coding.armypicker.adapter.BattalionRequirementListAdapter;
import de.game_coding.armypicker.adapter.UnitListAdapter;
import de.game_coding.armypicker.listener.CompletionHandler;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.listener.ProgressClickListener;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.Battalion;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitRequirement;
import de.game_coding.armypicker.util.CloneUtil;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.util.UIUtil;
import de.game_coding.armypicker.viewmodel.BattalionRequirementDetails;
import de.game_coding.armypicker.viewmodel.UnitSummaries;

@EActivity(R.layout.activity_batallion)
@OptionsMenu(R.menu.battalion_activity_menu)
public class BattalionActivity extends Activity {

	public static final String EXTRA_ARMY = "BattalionActivity.EXTRA_ARMY";

	private Army army;

	@ViewById(R.id.battalions_list)
	protected ListView battalions;

	@ViewById(R.id.battalion_available_choices_view)
	protected View selectionView;

	@ViewById(R.id.battalion_available_selection)
	protected ListView availableBattalions;

	@ViewById(R.id.battalion_available_sub_choices_view)
	protected View subSelectionView;

	@ViewById(R.id.battalion_available_sub_selection)
	protected ListView availableSubBattalions;

	@ViewById(R.id.battalion_unit_edit_view)
	protected View subUnitEditView;

	@ViewById(R.id.battalion_unit_edit_list)
	protected ListView editUnits;

	@ViewById(R.id.battalion_points)
	protected TextView pointLabel;

	@ViewById(R.id.chance_view)
	protected View chanceView;

	protected BattalionRequirement addToRequirement;

	protected CompletionHandler onSubSelectedHandler;

	protected BattalionRequirementDetails details = BattalionRequirementDetails.NONE;

	@AfterViews
	protected void init() {
		army = getIntent().getParcelableExtra(EXTRA_ARMY);

		battalions.setAdapter(newBattalionAdapter());

		final BattalionListAdapter adapter = new BattalionListAdapter(this, army.getBattalionTemplates(), false,
			details);
		availableBattalions.setAdapter(adapter);

		UIUtil.show(selectionView, army.getBattalions().size() == 0);
	}

	@Click(R.id.battalions_show_chance_calculator)
	protected void onShowChanceView() {
		chanceView.setVisibility(View.VISIBLE);
	}

	@Click(R.id.chance_view)
	protected void onHideChanceView() {
		chanceView.setVisibility(View.GONE);
	}

	@ItemClick(R.id.battalion_available_selection)
	protected void onSelectNewBattalion(final Battalion battalion) {
		battalions.setAdapter(null);
		final Battalion entry = CloneUtil.clone(battalion, Battalion.CREATOR);
		army.addBattalion(entry);
		battalions.setAdapter(newBattalionAdapter());
		UIUtil.hide(selectionView);
		FileUtil.storeArmy(army, this);
	}

	@Click(R.id.battalion_select_abort)
	protected void onAbortSelection() {
		UIUtil.hide(selectionView);
	}

	@ItemClick(R.id.battalion_available_sub_selection)
	protected void onSelectSubBattalion(final BattalionRequirement battalion) {
		if (addToRequirement != null) {
			assignViaDeepClone(addToRequirement, battalion);
			addToRequirement = null;
		}
		UIUtil.hide(subSelectionView);
		FileUtil.storeArmy(army, this);
		onSubSelectedHandler.onCompleted();
		onSubSelectedHandler = null;
	}

	@Click(R.id.battalion_unit_edit_abort)
	protected void onAbortUnitEdit() {
		UIUtil.hide(subUnitEditView);
	}

	private void assignViaDeepClone(final BattalionRequirement parent, final BattalionRequirement child) {
		parent.assignSubBattalion(getDeepClone(parent, child));
	}

	private BattalionRequirement getDeepClone(final BattalionRequirement parent, final BattalionRequirement child) {
		final int index = parent.getRequiredSubBattalions().indexOf(child);
		if (index >= 0 && parent.getRequiredSubBattalions().get(index) == child) {
			final BattalionRequirement clone = CloneUtil.clone(child, BattalionRequirement.CREATOR);
			for (final BattalionRequirement sub : clone.getRequiredSubBattalions()) {
				if (sub.isMeta()) {
					clone.assignSubBattalion(CloneUtil.clone(sub, BattalionRequirement.CREATOR));
				}
			}
			return clone;
		}
		for (final BattalionRequirement assignedSub : parent.getAssignedSubBattalions()) {
			final BattalionRequirement node = getDeepClone(assignedSub, child);
			if (node != null) {
				return assignedSub.assignSubBattalion(node);
			}
		}
		for (final BattalionRequirement templateSub : parent.getRequiredSubBattalions()) {
			final BattalionRequirement node = getDeepClone(templateSub, child);
			if (node != null) {
				return CloneUtil.clone(templateSub, BattalionRequirement.CREATOR).assignSubBattalion(node);
			}
		}
		return null;
	}

	@Click(R.id.battalion_sub_select_abort)
	protected void onAbortSubSelection() {
		UIUtil.hide(subSelectionView);
	}

	private ListAdapter newBattalionAdapter() {
		final BattalionListAdapter adapter = new BattalionListAdapter(this, army.getBattalions(), true, details);
		adapter.setDeleteHandler(new DeleteHandler<Battalion>() {
			@Override
			public void onDelete(final Battalion b) {
				army.removeBattalion(b);
				FileUtil.storeArmy(army, BattalionActivity.this);
				battalions.setAdapter(newBattalionAdapter());
			}
		});
		adapter.setAddSubHandler(new ProgressClickListener<BattalionRequirement>() {

			@Override
			public void onItemClicked(final BattalionRequirement item, final CompletionHandler onSubSelected) {
				BattalionRequirement current = item;
				while (current.getRequiredSubBattalions().size() == 1
					&& (current.getRequiredUnits().size() == 0 || current == item)) {
					current = current.getRequiredSubBattalions().get(0);
				}
				if (current.getRequiredSubBattalions().size() == 1) {
					assignViaDeepClone(item, current.getRequiredSubBattalions().get(0));
					onSubSelected.onCompleted();
					return;
				}
				addToRequirement = item;
				onSubSelectedHandler = onSubSelected;
				UIUtil.show(subSelectionView);
				final BattalionRequirementListAdapter subAdapter = new BattalionRequirementListAdapter(
					BattalionActivity.this, mergeSubs(item), true, details);
				availableSubBattalions.setAdapter(subAdapter);
			}
		});
		adapter.setAddUnitHandler(new ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>>() {
			@Override
			public void onItemClicked(final Pair<UnitRequirement, BattalionRequirement> item) {
				for (final Unit unit : army.getUnitTemplates()) {
					if (unit.getName().equals(item.first.getUnitName())) {
						item.second.assignUnit(CloneUtil.clone(unit, Unit.CREATOR));
					}
				}
			}
		});
		adapter.setEditUnitHandler(new ItemClickedListener<Collection<Unit>>() {
			@Override
			public void onItemClicked(final Collection<Unit> items) {
				updatePoints(items);
				final Army units = new Army(army.getName(), army.getUnitTemplates(), army.getTemplateVersion());
				for (final Unit unit : items) {
					units.addUnit(unit);
				}

				final UnitListAdapter adapter = new UnitListAdapter(BattalionActivity.this, units, false,
					UnitSummaries.NONE);
				UIUtil.show(subUnitEditView);

				adapter.setNotifier(new IValueChangedNotifier() {
					@Override
					public void onValueChanged() {
						updatePoints(items);
						FileUtil.storeArmy(army, BattalionActivity.this);
					}
				});
				editUnits.setAdapter(adapter);
			}
		});
		adapter.setChangeNotifier(new IValueChangedNotifier() {
			@Override
			public void onValueChanged() {
				FileUtil.storeArmy(army, BattalionActivity.this);
			}
		});
		return adapter;
	}

	private void updatePoints(final Collection<Unit> items) {
		int points = 0;
		for (final Unit unit : items) {
			points += unit.getTotalCosts();
		}
		pointLabel.setText(String.valueOf(points));
	}

	private List<BattalionRequirement> mergeSubs(final BattalionRequirement item) {
		final List<BattalionRequirement> subs = new ArrayList<BattalionRequirement>();
		for (final BattalionRequirement sub : item.getRequiredSubBattalions()) {
			BattalionRequirement add = sub;
			for (final BattalionRequirement assigned : item.getAssignedSubBattalions()) {
				if (assigned.getName().equals(add.getName())) {
					add = assigned;
					break;
				}
			}
			subs.add(add);
		}
		return subs;
	}

	@Override
	public void onBackPressed() {
		if (subUnitEditView.getVisibility() == View.VISIBLE) {
			onAbortUnitEdit();
		} else if (chanceView.getVisibility() == View.VISIBLE) {
			onHideChanceView();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void finish() {
		final Bundle result = new Bundle();
		result.putParcelable(EXTRA_ARMY, army);
		final Intent intent = new Intent();
		intent.putExtras(result);
		setResult(RESULT_OK, intent);

		super.finish();
	}

	@OptionsItem(R.id.action_add)
	protected void onAddBattalion() {
		UIUtil.show(selectionView);
	}

	@OptionsItem(R.id.action_show_summary)
	protected void onChangeDetails() {
		details = BattalionRequirementDetails.values()[(details.ordinal() + 1)
			% BattalionRequirementDetails.values().length];
		battalions.setAdapter(newBattalionAdapter());

		if (addToRequirement != null) {
			final BattalionRequirementListAdapter subAdapter = new BattalionRequirementListAdapter(
				BattalionActivity.this, mergeSubs(addToRequirement), true, details);
			availableSubBattalions.setAdapter(subAdapter);
		}

		final BattalionListAdapter adapter = new BattalionListAdapter(this, army.getBattalionTemplates(), false,
			details);
		availableBattalions.setAdapter(adapter);
	}
}
