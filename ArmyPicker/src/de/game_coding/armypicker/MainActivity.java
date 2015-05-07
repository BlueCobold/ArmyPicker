package de.game_coding.armypicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import de.game_coding.armypicker.adapter.ArmyListAdapter;
import de.game_coding.armypicker.adapter.ArmyListAdapter.DeleteHandler;
import de.game_coding.armypicker.adapter.ArmyListAdapter.EditHandler;
import de.game_coding.armypicker.adapter.ArmyTypeListAdapter;
import de.game_coding.armypicker.builder.SpaceElveBuilder;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.util.CloneUtil;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.util.UIUtil;

public class MainActivity extends Activity {

	protected static final String TAG = MainActivity.class.getName();

	private static final Unit[] SPACE_ELVES = SpaceElveBuilder.getTemplates();

	private static final List<Army> ARMY_TEMPLATES = new ArrayList<Army>() {
		private static final long serialVersionUID = 1493878691032538962L;

		{
			add(new Army(SpaceElveBuilder.getName(), SPACE_ELVES));
		}
	};

	protected static final int EDIT_ARMY = 10;

	private final List<Army> armies = new ArrayList<Army>();

	private View selectionView;

	private int editedArmyIndex;

	private ListView armyListView;

	private View editView;

	protected Army editArmy;

	private EditText editArmyName;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final List<Army> army = FileUtil.readArmies(this);
		if (army != null) {
			armies.addAll(army);
		}
		setContentView(R.layout.activity_main);

		armyListView = (ListView) findViewById(R.id.army_selection);
		armyListView.setAdapter(newArmyAdapter(armyListView));

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

		editView = findViewById(R.id.army_edit_army);
		UIUtil.hide(editView);

		editArmyName = (EditText) findViewById(R.id.army_name_edit);

		final View editOk = findViewById(R.id.army_edit_ok);
		editOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				editArmy.setName(editArmyName.getText().toString());
				UIUtil.hide(editView);
			}
		});

		selectionView = findViewById(R.id.army_available_armies_view);
		selectionView.setVisibility(View.INVISIBLE);

		final View abort = findViewById(R.id.army_select_abort);
		abort.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
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
