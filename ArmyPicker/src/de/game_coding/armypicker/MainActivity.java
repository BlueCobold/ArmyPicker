package de.game_coding.armypicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import de.game_coding.armypicker.adapter.ArmyListAdapter;
import de.game_coding.armypicker.adapter.ArmyTypeListAdapter;
import de.game_coding.armypicker.builder.EvilSpaceElveBuilder;
import de.game_coding.armypicker.builder.GreenSpaceMonkBuilder;
import de.game_coding.armypicker.builder.IArmyTemplateBuilder;
import de.game_coding.armypicker.builder.SpaceClownBuilder;
import de.game_coding.armypicker.builder.SpaceElveBuilder;
import de.game_coding.armypicker.builder.SpaceMonkBuilder;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.EditHandler;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.util.CloneUtil;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.util.UIUtil;
import de.game_coding.armypicker.viewgroups.DownloadView;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main_activity_menu)
public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getName();

	private static final IArmyTemplateBuilder SPACE_ELVES = new SpaceElveBuilder();
	private static final IArmyTemplateBuilder SPACE_MONKS = new SpaceMonkBuilder();
	private static final IArmyTemplateBuilder SPACE_CLOWNS = new SpaceClownBuilder();
	private static final IArmyTemplateBuilder GREEN_SPACE_MONKS = new GreenSpaceMonkBuilder();
	private static final IArmyTemplateBuilder EVIL_SPACE_ELVES = new EvilSpaceElveBuilder();

	private static final List<Army> ARMY_TEMPLATES = new ArrayList<Army>() {
		private static final long serialVersionUID = 1493878691032538962L;

		{
			add(build(SPACE_ELVES));
			add(build(SPACE_CLOWNS));
			add(build(EVIL_SPACE_ELVES));
			add(build(SPACE_MONKS));
			add(build(GREEN_SPACE_MONKS));
		}

		private Army build(final IArmyTemplateBuilder builder) {
			return new Army(builder.getName(), builder.getTemplates(), builder.getVersion()) //
				.attachStats(builder.getStats()) //
				.attachWeapons(builder.getWeapons()) //
				.attachBattalions(builder.getBattalions());
		}
	};

	private static final int EDIT_ARMY = 10;

	private final List<Army> armies = new ArrayList<Army>();

	private Army editArmy;

	private int editedArmyIndex;

	private boolean changingTemplate;

	@ViewById(R.id.army_available_armies_view)
	protected View selectionView;

	@ViewById(R.id.army_selection)
	protected ListView armyListView;

	@ViewById(R.id.army_available_army_selection)
	protected ListView availableArmies;

	@ViewById(R.id.army_edit_army)
	protected View editView;

	@ViewById(R.id.army_name_edit)
	protected EditText editArmyName;

	@ViewById(R.id.chance_view)
	protected View chanceView;

	@ViewById(R.id.army_download_view)
	protected DownloadView downloadView;

	@ViewById(R.id.army_download_container)
	protected View downloadContainer;

	@AfterViews
	protected void init() {

		final List<Army> army = FileUtil.readArmies(this);
		if (army != null) {
			armies.addAll(army);
		}

		downloadView.setArmies(ARMY_TEMPLATES);
		armyListView.setAdapter(newArmyAdapter(armyListView));
		UIUtil.hide(editView);
		selectionView.setVisibility(View.INVISIBLE);
	}

	@Click(R.id.army_show_chance_calculator)
	protected void onShowChanceView() {
		chanceView.setVisibility(View.VISIBLE);
	}

	@Click(R.id.chance_view)
	protected void onHideChanceView() {
		chanceView.setVisibility(View.GONE);
	}

	@Click(R.id.army_select_abort)
	protected void onAbortArmySelection() {
		selectionView.setVisibility(View.INVISIBLE);
	}

	@Click(R.id.army_edit_ok)
	protected void onEditFinished() {
		editArmy.setName(editArmyName.getText().toString());
		UIUtil.hide(editView);
		FileUtil.storeArmy(editArmy, this);
	}

	@ItemClick(R.id.army_available_army_selection)
	protected void onNewArmySelected(final Army army) {
		if (!changingTemplate) {
			armyListView.setAdapter(null);
			final Army newArmy = CloneUtil.clone(army, Army.CREATOR);
			armies.add(newArmy);
			newArmy.setId(getUniqueArmyId());
			final ArmyListAdapter adapter = newArmyAdapter(armyListView);
			armyListView.setAdapter(adapter);
			switchToArmy(armies.get(armies.size() - 1));
			selectionView.setVisibility(View.INVISIBLE);
		} else {
			final Army oldArmy = armies.get(editedArmyIndex);
			oldArmy.setUnitTemplates(CloneUtil.clone(army.getUnitTemplates(), Unit.CREATOR));
			oldArmy.setWeapons(CloneUtil.clone(army.getWeapons(), UnitStats.CREATOR));
			oldArmy.setStats(CloneUtil.clone(army.getStats(), UnitStats.CREATOR));
			oldArmy.setTemplateVersion(army.getTemplateVersion());
			changingTemplate = false;
			selectionView.setVisibility(View.INVISIBLE);
		}
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

	@ItemClick(R.id.army_selection)
	protected void switchToArmy(final Army army) {
		editedArmyIndex = armies.indexOf(army);
		ArmyActivity_.intent(this).extra(ArmyActivity.EXTRA_ARMY, army).startForResult(EDIT_ARMY);
	}

	@ItemLongClick(R.id.army_selection)
	protected void switchArmyTemplate(final Army army) {
		changingTemplate = true;
		editedArmyIndex = armies.indexOf(army);
		addNewArmy();
	}

	private ArmyListAdapter newArmyAdapter(final ListView armyList) {
		final ArmyListAdapter adapter = new ArmyListAdapter(this, armies);
		adapter.setDeleteHandler(new DeleteHandler<Army>() {

			@Override
			public void onDelete(final Army army) {
				armyList.setAdapter(null);
				adapter.remove(army);
				armyList.setAdapter(adapter);
				FileUtil.delete(army, MainActivity.this);
			}
		});

		adapter.setEditHandler(new EditHandler<Army>() {

			@Override
			public void onEdit(final Army army) {
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
	public void onBackPressed() {
		if (chanceView.getVisibility() == View.VISIBLE) {
			onHideChanceView();
		} else {
			super.onBackPressed();
		}
	}

	@OptionsItem(R.id.action_add)
	protected void addNewArmy() {

		availableArmies.setAdapter(new ArmyTypeListAdapter(this, ARMY_TEMPLATES));
		selectionView.setVisibility(View.VISIBLE);
	}

	@OptionsItem(R.id.action_download)
	protected void downloadTemplate() {
		downloadContainer.setVisibility(View.VISIBLE);
	}
}
