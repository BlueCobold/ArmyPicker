package de.game_coding.armypicker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import de.game_coding.armypicker.adapter.BaseUnitAdapter;
import de.game_coding.armypicker.adapter.UnitListAdapter;
import de.game_coding.armypicker.adapter.UnitStatsListAdapter;
import de.game_coding.armypicker.adapter.UnitTypeListAdapter;
import de.game_coding.armypicker.adapter.WeaponStatsListAdapter;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import de.game_coding.armypicker.util.CloneUtil;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.util.UIUtil;

public class ArmyActivity extends Activity {

	private static final String SETTING_SHOW_TYPES = "ArmyActivity.SHOW_TYPES";
	private static final String SETTING_SHOW_SUMMARIES = "ArmyActivity.SHOW_SUMMARIES";

	public static final String EXTRA_ARMY = "ArmyActivity.EXTRA_ARMY";

	private Army army;

	private View selectionView;

	private TextView pointLabel;

	private ListView armyList;

	private boolean showTypes;
	private boolean showSummaries;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		restoreSettings();

		setContentView(R.layout.activity_army);
		army = getIntent().getParcelableExtra(EXTRA_ARMY);

		getActionBar().setTitle(army.getName());

		armyList = (ListView) findViewById(R.id.army_unit_selection);
		final UnitListAdapter adapter = newUnitAdapter();
		armyList.setAdapter(adapter);

		pointLabel = (TextView) findViewById(R.id.army_points);
		pointLabel.setText(String.valueOf(army.getTotalCosts()));

		final ListView newUnitList = (ListView) findViewById(R.id.army_available_unit_selection);
		final UnitTypeListAdapter unitTypeListAdapter = new UnitTypeListAdapter(this, army.getUnitTemplates());
		newUnitList.setAdapter(unitTypeListAdapter);
		newUnitList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				armyList.setAdapter(null);
				army.getUnits().add(CloneUtil.clone((Unit) parent.getAdapter().getItem(position), Unit.CREATOR));
				if (showTypes) {
					sortUnits(army);
				}
				armyList.setAdapter(newUnitAdapter());
				pointLabel.setText(String.valueOf(army.getTotalCosts()));
				UIUtil.hide(selectionView);
				FileUtil.storeArmy(army, ArmyActivity.this);
			}
		});
		newUnitList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position,
				final long id) {
				showStatsWindow(army.getUnitTemplates()[position]);
				return true;
			}
		});

		final ListView statsList = (ListView) findViewById(R.id.army_unit_stats_list);
		statsList.setAdapter(new UnitStatsListAdapter(this, army.getStats()));
		statsList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position,
				final long id) {
				showGearWindow(army.getStats().getEntries().get(position));
				return true;
			}
		});

		final ListView weaponList = (ListView) findViewById(R.id.army_weapon_stats_list);
		weaponList.setAdapter(new WeaponStatsListAdapter(this, army.getWeapons()));

		selectionView = findViewById(R.id.army_available_units_view);
		UIUtil.show(selectionView, army.getUnits().size() == 0);

		final View abort = findViewById(R.id.army_select_abort);
		abort.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				UIUtil.hide(selectionView);
			}
		});

		final View statsButton = findViewById(R.id.army_show_unit_stats);
		statsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				showUnitStats();
			}
		});
		final View unitButton = findViewById(R.id.army_show_unit_lists);
		unitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				showUnitList();
			}
		});
		final View weaponButton = findViewById(R.id.army_show_weapon_stats);
		weaponButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				showWeaponList();
			}
		});
		final View statsView = findViewById(R.id.army_specific_unit_stats_view);
		statsView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				statsView.setVisibility(View.GONE);
			}
		});
		final View gearView = findViewById(R.id.army_specific_unit_gear_view);
		gearView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				gearView.setVisibility(View.GONE);
			}
		});
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

	private void showUnitStats() {
		final View title = findViewById(R.id.army_title);
		final ListView statsList = (ListView) findViewById(R.id.army_unit_stats_list);
		final ListView weaponList = (ListView) findViewById(R.id.army_weapon_stats_list);
		armyList.setVisibility(View.GONE);
		title.setVisibility(View.GONE);
		pointLabel.setVisibility(View.GONE);
		statsList.setVisibility(View.VISIBLE);
		weaponList.setVisibility(View.GONE);
	}

	private void showUnitList() {
		final View title = findViewById(R.id.army_title);
		final ListView statsList = (ListView) findViewById(R.id.army_unit_stats_list);
		final ListView weaponList = (ListView) findViewById(R.id.army_weapon_stats_list);
		statsList.setVisibility(View.GONE);
		title.setVisibility(View.VISIBLE);
		pointLabel.setVisibility(View.VISIBLE);
		armyList.setVisibility(View.VISIBLE);
		weaponList.setVisibility(View.GONE);
	}

	private void showWeaponList() {
		final View title = findViewById(R.id.army_title);
		final ListView statsList = (ListView) findViewById(R.id.army_unit_stats_list);
		final ListView weaponList = (ListView) findViewById(R.id.army_weapon_stats_list);
		armyList.setVisibility(View.GONE);
		title.setVisibility(View.GONE);
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
		adapter.setDeleteHandler(new UnitListAdapter.DeleteHandler() {

			@Override
			public void onDelete(final Unit unit, final int position) {
				armyList.setAdapter(null);
				army.getUnits().remove(unit);
				pointLabel.setText(String.valueOf(army.getTotalCosts()));
				armyList.setAdapter(newUnitAdapter());
				FileUtil.storeArmy(army, ArmyActivity.this);
			}
		});
		addDetailListener(adapter);
		return adapter;
	}

	private void addDetailListener(final BaseUnitAdapter unitAdapter) {
		unitAdapter.setLongClickHandler(new UnitTypeListAdapter.RequestDetailsHandler() {

			@Override
			public void onUnitClicked(final Unit unit, final int position) {
				showStatsWindow(unit);
			}
		});
	}

	private void showStatsWindow(final Unit unit) {
		final View statsView = findViewById(R.id.army_specific_unit_stats_view);
		statsView.setVisibility(View.VISIBLE);
		final ListView statsList = (ListView) findViewById(R.id.army_specific_unit_stats_list);
		final UnitStats stats = getStats(unit.getStatsReferences(), army.getStats());
		statsList.setAdapter(new UnitStatsListAdapter(ArmyActivity.this, stats));
		final OnItemClickListener closeListener = new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				statsView.setVisibility(View.GONE);
			}
		};
		statsList.setOnItemClickListener(closeListener);
		final ListView weaponList = (ListView) findViewById(R.id.army_specific_weapon_stats_inline_list);
		if (stats.getEntries().size() > 1) {
			statsList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position,
					final long id) {
					showGearWindow(stats.getEntries().get(position));
					return true;
				}
			});
			weaponList.setVisibility(View.GONE);
		} else {
			weaponList.setVisibility(stats.getEntries().size() > 0 ? View.VISIBLE : View.GONE);
			if (stats.getEntries().size() > 0) {
				weaponList.setAdapter(new WeaponStatsListAdapter(ArmyActivity.this, getGear(army.getWeapons(), stats
					.getEntries().toArray(new StatsEntry[stats.getEntries().size()]))));
				weaponList.setOnItemClickListener(closeListener);
			}
		}
	}

	private void showGearWindow(final StatsEntry statsEntry) {
		final View gearView = findViewById(R.id.army_specific_unit_gear_view);
		gearView.setVisibility(View.VISIBLE);
		final ListView weaponList = (ListView) findViewById(R.id.army_specific_weapon_stats_list);
		weaponList.setAdapter(new WeaponStatsListAdapter(ArmyActivity.this, getGear(army.getWeapons(), statsEntry)));
		final OnItemClickListener closeHandler = new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				gearView.setVisibility(View.GONE);
			}
		};
		weaponList.setOnItemClickListener(closeHandler);
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

	private static UnitStats getStats(final List<Integer> references, final UnitStats statsType) {
		final UnitStats result = new UnitStats(statsType.getHeaders());
		for (final Integer id : references) {
			final StatsEntry stats = statsType.find(id);
			if (stats != null) {
				result.appendEntry(stats);
			}
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.army_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			UIUtil.show(selectionView);
			showUnitList();
			break;

		case R.id.action_sort:
			showTypes = !showTypes;
			if (showTypes) {
				sortUnits(army);
			}
			showUnitList();
			armyList.setAdapter(null);
			armyList.setAdapter(newUnitAdapter());
			storeSettings();
			break;

		case R.id.action_show_summary:
			showSummaries = !showSummaries;
			showUnitList();
			armyList.setAdapter(null);
			armyList.setAdapter(newUnitAdapter());
			storeSettings();
		default:
			break;
		}
		return true;
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
