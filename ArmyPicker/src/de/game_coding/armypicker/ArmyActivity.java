package de.game_coding.armypicker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.game_coding.armypicker.adapter.UnitListAdapter;
import de.game_coding.armypicker.adapter.UnitStatsListAdapter;
import de.game_coding.armypicker.adapter.UnitTypeListAdapter;
import de.game_coding.armypicker.adapter.WeaponStatsListAdapter;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import de.game_coding.armypicker.util.CloneUtil;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.util.UIUtil;
import de.game_coding.armypicker.util.UnitUtils;
import de.game_coding.armypicker.util.WeaponUtils;
import de.game_coding.armypicker.viewmodel.UnitStatsSummaries;
import de.game_coding.armypicker.viewmodel.UnitSummaries;
import de.game_coding.armypicker.viewmodel.WeaponStatsSummaries;

@EActivity(R.layout.activity_army)
@OptionsMenu(R.menu.army_activity_menu)
public class ArmyActivity extends Activity {

	private static final String SETTING_SHOW_TYPES = "ArmyActivity.SHOW_TYPES";

	private static final String SETTING_SHOW_SUMMARIES = "ArmyActivity.SHOW_SUMMARIES";

	private static final String SETTING_SHOW_SPECIFIC_GEAR_SUMMARIES = "ArmyActivity.SETTING_SHOW_SPECIFIC_GEAR_SUMMARIES";
	private static final String SETTING_SHOW_GEAR_SUMMARIES = "ArmyActivity.SETTING_SHOW_GEAR_SUMMARIES";

	public static final String EXTRA_ARMY = "ArmyActivity.EXTRA_ARMY";

	private static final int EDIT_ARMY = 10;

	private Army army;

	@ViewById(R.id.army_available_units_view)
	protected View selectionView;

	@ViewById(R.id.army_points)
	protected TextView pointLabel;

	@ViewById(R.id.army_title)
	protected TextView armyTitle;

	@ViewById(R.id.army_unit_selection)
	protected ListView armyList;

	@ViewById(R.id.army_unit_stats_list)
	protected ListView statsList;

	@ViewById(R.id.army_specific_unit_stats_list)
	protected ListView specificStatsList;

	@ViewById(R.id.army_available_unit_selection)
	protected ListView newUnitList;

	@ViewById(R.id.army_weapon_stats_list)
	protected ListView weaponList;

	@ViewById(R.id.army_specific_weapon_stats_inline_list)
	protected ListView specificInlineWeaponList;

	@ViewById(R.id.army_specific_weapon_stats_list)
	protected ListView specificWeaponList;

	@ViewById(R.id.chance_view)
	protected View chanceView;

	@ViewById(R.id.army_specific_unit_stats_view)
	protected View specificStatsView;

	@ViewById(R.id.army_specific_unit_gear_view)
	protected View specificGearView;

	@ViewById(R.id.army_show_weapon_stats)
	protected ImageButton showWeaponsButton;

	@ViewById(R.id.army_show_unit_stats)
	protected ImageButton showStatsButton;

	@ViewById(R.id.army_show_unit_lists)
	protected ImageButton showUnitListButton;

	@ViewById(R.id.army_show_chance_calculator)
	protected ImageButton showCalculatorButton;

	private boolean showTypes;

	private UnitSummaries showSummaries = UnitSummaries.NONE;

	private UnitStatsSummaries showStatsSummaries = UnitStatsSummaries.NONE;

	private WeaponStatsSummaries specificGearSummaries = WeaponStatsSummaries.NONE;
	private WeaponStatsSummaries gearSummaries = WeaponStatsSummaries.NONE;

	private List<UnitStats> stats;

	private Unit shownStatsUnit;

	private StatsEntry shownStatsEntry;

	@AfterViews
	protected void init() {
		restoreSettings();

		showUnitListButton.setColorFilter(0xff60E0FF);

		army = getIntent().getParcelableExtra(EXTRA_ARMY);

		getActionBar().setTitle(army.getName());

		armyList.setAdapter(newUnitAdapter());
		newUnitList.setAdapter(new UnitTypeListAdapter(this, army.getUnitTemplates()));

		pointLabel.setText(String.valueOf(army.getTotalCosts()));

		stats = CloneUtil.clone(army.getStats(), UnitStats.CREATOR);
		sortStatsByName(stats);
		final Collection<StatsEntry> empty = new ArrayList<UnitStats.StatsEntry>();
		statsList.setAdapter(new UnitStatsListAdapter(this, stats, empty, showStatsSummaries));

		final UnitStats weapons = CloneUtil.clone(army.getWeapons(), UnitStats.CREATOR);
		sortStatsByName(weapons);
		weaponList.setAdapter(new WeaponStatsListAdapter(this, weapons, WeaponStatsSummaries.NONE));

		UIUtil.show(selectionView, army.getUnits().size() == 0);
	}

	@Click(R.id.army_select_abort)
	protected void abortUnitSelection() {
		UIUtil.hide(selectionView);
	}

	@Click(R.id.army_show_chance_calculator)
	protected void showChanceView() {
		chanceView.setVisibility(View.VISIBLE);
		showCalculatorButton.setColorFilter(0xff60E0FF);
	}

	@Click(R.id.chance_view)
	protected void hideChanceView() {
		chanceView.setVisibility(View.GONE);
		showCalculatorButton.setColorFilter(0xffffffff);
	}

	@Click(R.id.army_show_characters_lists)
	protected void switchToCharacterSelection() {
		CharacterActivity.shareArmy(army);
		CharacterActivity_.intent(this).extra(CharacterActivity.EXTRA_ARMY_ID, army.getId()).start();
	}

	@ItemClick(R.id.army_available_unit_selection)
	protected void selectNewUnit(final Unit unit) {
		army.addUnit(CloneUtil.clone(unit, Unit.CREATOR));
		if (showTypes) {
			sortUnits(army);
		}
		setAdapter(armyList, newUnitAdapter());
		pointLabel.setText(String.valueOf(army.getTotalCosts()));
		UIUtil.hide(selectionView);
		FileUtil.storeArmy(army, ArmyActivity.this);
	}

	@Click({ R.id.army_specific_unit_stats_view, R.id.army_specific_unit_gear_view })
	protected void hideStatsOrGearView(final View view) {
		view.setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		storeSettings();
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

	@Click(R.id.army_show_unit_stats)
	protected void showUnitStats() {
		armyList.setVisibility(View.GONE);
		armyTitle.setVisibility(View.GONE);
		pointLabel.setVisibility(View.GONE);
		statsList.setVisibility(View.VISIBLE);
		weaponList.setVisibility(View.GONE);
		showWeaponsButton.setColorFilter(0xffffffff);
		showStatsButton.setColorFilter(0xff60E0FF);
		showUnitListButton.setColorFilter(0xffffffff);
	}

	@Click(R.id.army_show_unit_lists)
	protected void showUnitList() {
		statsList.setVisibility(View.GONE);
		armyTitle.setVisibility(View.VISIBLE);
		pointLabel.setVisibility(View.VISIBLE);
		armyList.setVisibility(View.VISIBLE);
		weaponList.setVisibility(View.GONE);
		showWeaponsButton.setColorFilter(0xffffffff);
		showStatsButton.setColorFilter(0xffffffff);
		showUnitListButton.setColorFilter(0xff60E0FF);
	}

	@Click(R.id.army_show_weapon_stats)
	protected void showWeaponList() {
		final UnitStats weapons = CloneUtil.clone(army.getWeapons(), UnitStats.CREATOR);
		sortStatsByName(weapons);
		setAdapter(weaponList, new WeaponStatsListAdapter(this, weapons, gearSummaries));
		armyList.setVisibility(View.GONE);
		armyTitle.setVisibility(View.GONE);
		pointLabel.setVisibility(View.GONE);
		statsList.setVisibility(View.GONE);
		weaponList.setVisibility(View.VISIBLE);
		showWeaponsButton.setColorFilter(0xff60E0FF);
		showStatsButton.setColorFilter(0xffffffff);
		showUnitListButton.setColorFilter(0xffffffff);
	}

	private void setAdapter(final ListView listView, final ListAdapter adapter) {
		final int index = listView.getFirstVisiblePosition();
		final View v = listView.getChildAt(0);
		final int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
		listView.setAdapter(adapter);
		listView.setSelectionFromTop(index, top);
	}

	@Click(R.id.army_show_detachments)
	protected void startBattalionSelection() {
		BattalionActivity_.intent(this).extra(BattalionActivity_.EXTRA_ARMY, army).startForResult(EDIT_ARMY);
	}

	private void restoreSettings() {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		showTypes = settings.getBoolean(SETTING_SHOW_TYPES, false);
		showSummaries = UnitSummaries.values()[settings.getInt(SETTING_SHOW_SUMMARIES, 0)];
		specificGearSummaries = WeaponStatsSummaries.values()[settings.getInt(SETTING_SHOW_SPECIFIC_GEAR_SUMMARIES, 0)];
		gearSummaries = WeaponStatsSummaries.values()[settings.getInt(SETTING_SHOW_GEAR_SUMMARIES, 0)];
	}

	private void storeSettings() {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final Editor editor = settings.edit();
		editor.putBoolean(SETTING_SHOW_TYPES, showTypes);
		editor.putInt(SETTING_SHOW_SUMMARIES, showSummaries.ordinal());
		editor.putInt(SETTING_SHOW_SPECIFIC_GEAR_SUMMARIES, specificGearSummaries.ordinal());
		editor.putInt(SETTING_SHOW_GEAR_SUMMARIES, gearSummaries.ordinal());
		editor.apply();
	}

	private UnitListAdapter newUnitAdapter() {
		final UnitListAdapter adapter = new UnitListAdapter(this, army, showTypes, showSummaries);
		adapter.setNotifier(new IValueChangedNotifier() {

			@Override
			public void onValueChanged() {
				pointLabel.setText(String.valueOf(army.getTotalCosts()));
				FileUtil.storeArmy(army, ArmyActivity.this);
			}
		});
		adapter.setDeleteHandler(new DeleteHandler<Unit>() {

			@Override
			public void onDelete(final Unit unit) {
				army.removeUnit(unit);
				pointLabel.setText(String.valueOf(army.getTotalCosts()));
				setAdapter(armyList, newUnitAdapter());
				FileUtil.storeArmy(army, ArmyActivity.this);
			}
		});
		adapter.setLongClickHandler(new ItemClickedListener<Unit>() {

			@Override
			public void onItemClicked(final Unit unit) {
				showStatsWindow(unit);
			}
		});
		return adapter;
	}

	@ItemClick({ R.id.army_specific_unit_stats_list, R.id.army_specific_weapon_stats_inline_list })
	protected void hideStatsView() {
		shownStatsUnit = null;
		specificStatsView.setVisibility(View.GONE);
	}

	@ItemLongClick(R.id.army_available_unit_selection)
	protected void showStatsWindow(final Unit unit) {
		shownStatsUnit = unit;
		specificStatsView.setVisibility(View.VISIBLE);
		final UnitStats stats = UnitUtils.getStats(unit.getStatsReferences(), army.getStats());
		final Collection<StatsEntry> weapons = WeaponUtils.getWeapons(stats, army.getWeapons());
		setAdapter(specificStatsList, new UnitStatsListAdapter(ArmyActivity.this, stats, weapons, showStatsSummaries));
		if (stats.getEntries().size() > 1) {
			specificInlineWeaponList.setVisibility(View.GONE);
		} else {
			final UnitStats gear = getGear(army.getWeapons(),
				stats.getEntries().toArray(new StatsEntry[stats.getEntries().size()]));
			specificInlineWeaponList.setVisibility(gear.getEntries().size() > 0 ? View.VISIBLE : View.GONE);
			if (gear.getEntries().size() > 0) {
				setAdapter(specificInlineWeaponList,
					new WeaponStatsListAdapter(ArmyActivity.this, gear, specificGearSummaries));
			}
		}
	}

	@ItemClick(R.id.army_specific_weapon_stats_list)
	protected void hideSpecificGearView() {
		specificGearView.setVisibility(View.GONE);
		shownStatsEntry = null;
	}

	@ItemLongClick({ R.id.army_unit_stats_list, R.id.army_specific_unit_stats_list })
	protected void showGearWindow(final StatsEntry statsEntry) {
		shownStatsEntry = statsEntry;
		specificGearView.setVisibility(View.VISIBLE);
		setAdapter(specificWeaponList, new WeaponStatsListAdapter(ArmyActivity.this,
			getGear(army.getWeapons(), statsEntry), specificGearSummaries));
	}

	private UnitStats getGear(final UnitStats lookupList, final StatsEntry... statsEntries) {
		final UnitStats result = new UnitStats(lookupList.getHeaders());
		for (final StatsEntry statsEntry : statsEntries) {
			for (final Integer refId : statsEntry.getGearReferences()) {
				final StatsEntry gear = lookupList.find(refId);
				if (gear != null) {
					result.appendEntry(gear);
				}
			}
		}
		return result;
	}

	private static void sortStatsByName(final List<UnitStats> stats) {
		for (final UnitStats stat : stats) {
			sortStatsByName(stat);
		}
	}

	private static void sortStatsByName(final UnitStats stats) {
		Collections.sort(stats.getEntries(), new Comparator<StatsEntry>() {

			@Override
			public int compare(final StatsEntry lhs, final StatsEntry rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
		});
	}

	@OptionsItem(R.id.action_add)
	protected void startUnitSelection() {
		UIUtil.show(selectionView);
		showUnitList();
	}

	@OptionsItem(R.id.action_sort)
	protected void sortUnits() {
		showTypes = !showTypes;
		if (showTypes) {
			sortUnits(army);
		}
		showUnitList();
		setAdapter(armyList, newUnitAdapter());
		storeSettings();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == EDIT_ARMY) {
			if (resultCode == RESULT_OK) {
				final Bundle bundle = data.getExtras();
				army = bundle.getParcelable(BattalionActivity.EXTRA_ARMY);
				setAdapter(armyList, newUnitAdapter());
				pointLabel.setText(String.valueOf(army.getTotalCosts()));
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (weaponList.getVisibility() == View.VISIBLE) {
			showUnitList();
		} else if (statsList.getVisibility() == View.VISIBLE) {
			showUnitList();
		} else if (specificGearView.getVisibility() == View.VISIBLE) {
			hideSpecificGearView();
		} else if (specificStatsView.getVisibility() == View.VISIBLE) {
			hideStatsView();
		} else if (selectionView.getVisibility() == View.VISIBLE) {
			abortUnitSelection();
		} else if (chanceView.getVisibility() == View.VISIBLE) {
			hideChanceView();
		} else {
			super.onBackPressed();
		}
	}

	@OptionsItem(R.id.action_show_summary)
	protected void showHideSummaries() {
		if (specificGearView.getVisibility() == View.VISIBLE && shownStatsEntry != null) {
			specificGearSummaries = WeaponStatsSummaries.values()[(specificGearSummaries.ordinal() + 1)
				% WeaponStatsSummaries.values().length];
			showGearWindow(shownStatsEntry);
			return;
		} else if (specificStatsView.getVisibility() == View.VISIBLE && shownStatsUnit != null) {
			showStatsSummaries = UnitStatsSummaries.values()[(showStatsSummaries.ordinal() + 1)
				% UnitStatsSummaries.values().length];
			showStatsWindow(shownStatsUnit);
			return;
		} else if (weaponList.getVisibility() == View.VISIBLE) {
			gearSummaries = WeaponStatsSummaries.values()[(gearSummaries.ordinal() + 1)
				% WeaponStatsSummaries.values().length];
			showWeaponList();
			return;
		}
		showSummaries = UnitSummaries.values()[(showSummaries.ordinal() + 1) % UnitSummaries.values().length];
		showUnitList();
		setAdapter(armyList, newUnitAdapter());
		storeSettings();
	}

	private static void sortUnits(final Army army) {
		Collections.sort(army.getUnits(), new Comparator<Unit>() {

			@Override
			public int compare(final Unit lhs, final Unit rhs) {
				if (lhs.getType().ordinal() < rhs.getType().ordinal()) {
					return -1;
				} else if (lhs.getType().ordinal() > rhs.getType().ordinal()) {
					return 1;
				}
				final int nameOrder = lhs.getName().compareTo(rhs.getName());
				if (nameOrder != 0) {
					return nameOrder;
				}
				if (lhs.getTotalCosts() < rhs.getTotalOptionCosts()) {
					return -1;
				} else if (lhs.getTotalCosts() > rhs.getTotalCosts()) {
					return 1;
				}
				return 0;
			}
		});
	}
}
