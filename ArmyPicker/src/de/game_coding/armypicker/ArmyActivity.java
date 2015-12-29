package de.game_coding.armypicker;

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

@EActivity(R.layout.activity_army)
@OptionsMenu(R.menu.army_activity_menu)
public class ArmyActivity extends Activity {

	private static final String SETTING_SHOW_TYPES = "ArmyActivity.SHOW_TYPES";

	private static final String SETTING_SHOW_SUMMARIES = "ArmyActivity.SHOW_SUMMARIES";

	public static final String EXTRA_ARMY = "ArmyActivity.EXTRA_ARMY";

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
	protected View statsView;

	@ViewById(R.id.army_specific_unit_gear_view)
	protected View specificGearView;

	private boolean showTypes;

	private boolean showSummaries;

	private List<UnitStats> stats;

	@AfterViews
	protected void init() {
		restoreSettings();

		army = getIntent().getParcelableExtra(EXTRA_ARMY);

		getActionBar().setTitle(army.getName());

		armyList.setAdapter(newUnitAdapter());
		newUnitList.setAdapter(new UnitTypeListAdapter(this, army.getUnitTemplates()));

		pointLabel.setText(String.valueOf(army.getTotalCosts()));

		stats = CloneUtil.clone(army.getStats(), UnitStats.CREATOR);
		sortStatsByName(stats);
		statsList.setAdapter(new UnitStatsListAdapter(this, stats));

		final UnitStats weapons = CloneUtil.clone(army.getWeapons(), UnitStats.CREATOR);
		sortStatsByName(weapons);
		weaponList.setAdapter(new WeaponStatsListAdapter(this, weapons));

		UIUtil.show(selectionView, army.getUnits().size() == 0);
	}

	@Click(R.id.army_select_abort)
	protected void abortUnitSelection() {
		UIUtil.hide(selectionView);
	}

	@Click(R.id.army_show_chance_calculator)
	protected void showChanceView() {
		chanceView.setVisibility(View.VISIBLE);
	}

	@Click(R.id.chance_view)
	protected void hideChanceView() {
		chanceView.setVisibility(View.GONE);
	}

	@Click(R.id.army_show_characters_lists)
	protected void switchToCharacterSelection() {
		CharacterActivity.shareArmy(army);
		CharacterActivity_.intent(this).extra(CharacterActivity.EXTRA_ARMY_ID, army.getId()).start();
	}

	@ItemClick(R.id.army_available_unit_selection)
	protected void selectNewUnit(final Unit unit) {
		armyList.setAdapter(null);
		army.getUnits().add(CloneUtil.clone(unit, Unit.CREATOR));
		if (showTypes) {
			sortUnits(army);
		}
		armyList.setAdapter(newUnitAdapter());
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
	}

	@Click(R.id.army_show_unit_lists)
	protected void showUnitList() {
		statsList.setVisibility(View.GONE);
		armyTitle.setVisibility(View.VISIBLE);
		pointLabel.setVisibility(View.VISIBLE);
		armyList.setVisibility(View.VISIBLE);
		weaponList.setVisibility(View.GONE);
	}

	@Click(R.id.army_show_weapon_stats)
	protected void showWeaponList() {
		armyList.setVisibility(View.GONE);
		armyTitle.setVisibility(View.GONE);
		pointLabel.setVisibility(View.GONE);
		statsList.setVisibility(View.GONE);
		weaponList.setVisibility(View.VISIBLE);
	}

	private void restoreSettings() {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		showTypes = settings.getBoolean(SETTING_SHOW_TYPES, false);
		showSummaries = settings.getBoolean(SETTING_SHOW_SUMMARIES, false);
	}

	private void storeSettings() {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final Editor editor = settings.edit();
		editor.putBoolean(SETTING_SHOW_TYPES, showTypes);
		editor.putBoolean(SETTING_SHOW_SUMMARIES, showSummaries);
		editor.apply();
	}

	private UnitListAdapter newUnitAdapter() {
		final UnitListAdapter adapter = new UnitListAdapter(this, army.getUnits(), showTypes, showSummaries);
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
				armyList.setAdapter(null);
				army.getUnits().remove(unit);
				pointLabel.setText(String.valueOf(army.getTotalCosts()));
				armyList.setAdapter(newUnitAdapter());
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
		statsView.setVisibility(View.GONE);
	}

	@ItemLongClick(R.id.army_available_unit_selection)
	protected void showStatsWindow(final Unit unit) {
		statsView.setVisibility(View.VISIBLE);
		final UnitStats stats = getStats(unit.getStatsReferences(), army.getStats());
		specificStatsList.setAdapter(new UnitStatsListAdapter(ArmyActivity.this, stats));
		if (stats.getEntries().size() > 1) {
			specificInlineWeaponList.setVisibility(View.GONE);
		} else {
			final UnitStats gear = getGear(army.getWeapons(),
				stats.getEntries().toArray(new StatsEntry[stats.getEntries().size()]));
			specificInlineWeaponList.setVisibility(gear.getEntries().size() > 0 ? View.VISIBLE : View.GONE);
			if (gear.getEntries().size() > 0) {
				specificInlineWeaponList.setAdapter(new WeaponStatsListAdapter(ArmyActivity.this, gear));
			}
		}
	}

	@ItemClick(R.id.army_specific_weapon_stats_list)
	protected void hideSpecificGearView() {
		specificGearView.setVisibility(View.GONE);
	}

	@ItemLongClick({ R.id.army_unit_stats_list, R.id.army_specific_unit_stats_list })
	protected void showGearWindow(final StatsEntry statsEntry) {
		specificGearView.setVisibility(View.VISIBLE);
		specificWeaponList.setAdapter(new WeaponStatsListAdapter(ArmyActivity.this, getGear(army.getWeapons(),
			statsEntry)));
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

	private static UnitStats getStats(final List<Integer> references, final List<UnitStats> list) {
		UnitStats result = null;
		for (final UnitStats statsType : list) {
			for (final Integer id : references) {
				final StatsEntry stats = statsType.find(id);
				if (stats != null) {
					if (result == null) {
						result = new UnitStats(statsType.getHeaders());
					}
					result.appendEntry(stats);
				}
			}
			if (result != null) {
				return result;
			}
		}
		return new UnitStats();
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
		armyList.setAdapter(null);
		armyList.setAdapter(newUnitAdapter());
		storeSettings();
	}

	@OptionsItem(R.id.action_show_summary)
	protected void showHideSummaries() {
		showSummaries = !showSummaries;
		showUnitList();
		armyList.setAdapter(null);
		armyList.setAdapter(newUnitAdapter());
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
