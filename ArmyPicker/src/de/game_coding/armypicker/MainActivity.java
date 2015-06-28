package de.game_coding.armypicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import de.game_coding.armypicker.adapter.ArmyListAdapter;
import de.game_coding.armypicker.adapter.ArmyListAdapter.DeleteHandler;
import de.game_coding.armypicker.adapter.ArmyListAdapter.EditHandler;
import de.game_coding.armypicker.adapter.ArmyTypeListAdapter;
import de.game_coding.armypicker.builder.GreenSpaceMonkBuilder;
import de.game_coding.armypicker.builder.IArmyTemplateBuilder;
import de.game_coding.armypicker.builder.SpaceClownBuilder;
import de.game_coding.armypicker.builder.SpaceElveBuilder;
import de.game_coding.armypicker.builder.SpaceMonkBuilder;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.util.CloneUtil;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.util.UIUtil;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	protected static final String TAG = MainActivity.class.getName();

	private static final IArmyTemplateBuilder SPACE_ELVES = new SpaceElveBuilder();
	private static final IArmyTemplateBuilder SPACE_MONKS = new SpaceMonkBuilder();
	private static final IArmyTemplateBuilder SPACE_CLOWNS = new SpaceClownBuilder();
	private static final IArmyTemplateBuilder GREEN_SPACE_MONKS = new GreenSpaceMonkBuilder();

	private static final List<Army> ARMY_TEMPLATES = new ArrayList<Army>() {
		private static final long serialVersionUID = 1493878691032538962L;

		{
			add(new Army(SPACE_ELVES.getName(), SPACE_ELVES.getTemplates()).attachStats(SPACE_ELVES.getStats())
				.attachWeapons(SPACE_ELVES.getWeapons()));
			add(new Army(SPACE_CLOWNS.getName(), SPACE_CLOWNS.getTemplates()).attachStats(SPACE_CLOWNS.getStats())
				.attachWeapons(SPACE_CLOWNS.getWeapons()));
			add(new Army(SPACE_MONKS.getName(), SPACE_MONKS.getTemplates()).attachStats(SPACE_MONKS.getStats())
				.attachWeapons(SPACE_MONKS.getWeapons()));
			add(new Army(GREEN_SPACE_MONKS.getName(), GREEN_SPACE_MONKS.getTemplates()).attachStats(
				GREEN_SPACE_MONKS.getStats()).attachWeapons(GREEN_SPACE_MONKS.getWeapons()));
		}
	};

	protected static final int EDIT_ARMY = 10;

	private final List<Army> armies = new ArrayList<Army>();

	protected Army editArmy;

	private int editedArmyIndex;

	@ViewById(R.id.army_available_armies_view)
	View selectionView;

	@ViewById(R.id.army_selection)
	ListView armyListView;

	@ViewById(R.id.army_edit_army)
	View editView;

	@ViewById(R.id.army_name_edit)
	EditText editArmyName;

	@ViewById(R.id.chance_view)
	View chanceView;

	private ChanceCalculator calculator;

	@AfterViews
	protected void init() {

		final List<Army> army = FileUtil.readArmies(this);
		if (army != null) {
			armies.addAll(army);
		}

		armyListView.setAdapter(newArmyAdapter(armyListView));
		initArmyList();
		UIUtil.hide(editView);
		selectionView.setVisibility(View.INVISIBLE);
		calculator = new ChanceCalculator(chanceView);
	}

	@Click(R.id.army_show_chance_calculator)
	void onShowChanceView() {
		chanceView.setVisibility(View.VISIBLE);
	}

	@Click(R.id.chance_view)
	void onHideChanceView() {
		chanceView.setVisibility(View.GONE);
	}

	@Click(R.id.army_select_abort)
	void onAbortArmySelection() {
		selectionView.setVisibility(View.INVISIBLE);
	}

	@Click(R.id.army_edit_ok)
	void onEditFinished() {
		editArmy.setName(editArmyName.getText().toString());
		UIUtil.hide(editView);
	}

	private void initArmyList() {
		final ListView newArmyList = (ListView) findViewById(R.id.army_available_army_selection);
		newArmyList.setAdapter(new ArmyTypeListAdapter(this, ARMY_TEMPLATES));
		newArmyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				armyListView.setAdapter(null);
				final Army army = CloneUtil.clone((Army) parent.getAdapter().getItem(position), Army.CREATOR);
				armies.add(army);
				army.setId(getUniqueArmyId());
				final ArmyListAdapter adapter = newArmyAdapter(armyListView);
				armyListView.setAdapter(adapter);
				switchToArmy(armies.size() - 1);
				selectionView.setVisibility(View.INVISIBLE);
			}
		});
	}

	private int getUniqueArmyId() {
		final Random random = new Random();
		boolean exists;
		int id = 0;
		do {
			exists = false;
			id = random.nextInt();
			for (final Army army : armies) {
				if (army.getId() == id) {
					exists = true;
					break;
				}
			}
		} while (exists);
		return id;
	}

	private void switchToArmy(final int position) {
		final Army army = armies.get(position);
		Log.d(TAG, "Clicked on item " + army.getName());
		editedArmyIndex = position;
		final Intent intent = new Intent(MainActivity.this, ArmyActivity.class);
		intent.putExtra(ArmyActivity.EXTRA_ARMY, army);
		startActivityForResult(intent, EDIT_ARMY);
	}

	private ArmyListAdapter newArmyAdapter(final ListView armyList) {
		final ArmyListAdapter adapter = new ArmyListAdapter(this, armies);
		armyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				switchToArmy(position);
			}
		});
		adapter.setDeleteHandler(new DeleteHandler() {

			@Override
			public void onDelete(final Army army, final int position) {
				armyList.setAdapter(null);
				adapter.remove(army);
				armyList.setAdapter(adapter);
				FileUtil.delete(army, MainActivity.this);
			}
		});

		adapter.setEditHandler(new EditHandler() {

			@Override
			public void onEdit(final Army army, final int position) {
				editArmy = army;
				editArmyName.setText(army.getName());
				editView.setVisibility(View.VISIBLE);
			}
		});
		return adapter;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == EDIT_ARMY) {
			if (resultCode == RESULT_OK) {
				final Bundle bundle = data.getExtras();
				final Army army = bundle.getParcelable(ArmyActivity.EXTRA_ARMY);
				armyListView.setAdapter(null);
				armies.remove(editedArmyIndex);
				armies.add(editedArmyIndex, army);
				armyListView.setAdapter(newArmyAdapter(armyListView));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			selectionView.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
		return true;
	}
}
