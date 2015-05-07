package de.game_coding.armypicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import de.game_coding.armypicker.adapter.UnitListAdapter;
import de.game_coding.armypicker.adapter.UnitTypeListAdapter;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.util.CloneUtil;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.util.UIUtil;

public class ArmyActivity extends Activity {

	public static final String EXTRA_ARMY = "ArmyActivity.EXTRA_ARMY";

	private Army army;

	private View selectionView;

	private TextView pointLabel;

	private ListView armyList;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_army);
		army = getIntent().getParcelableExtra(EXTRA_ARMY);

		getActionBar().setTitle(army.getName());

		armyList = (ListView) findViewById(R.id.army_unit_selection);
		final UnitListAdapter adapter = newUnitAdapter();
		armyList.setAdapter(adapter);

		pointLabel = (TextView) findViewById(R.id.army_points);
		pointLabel.setText(String.valueOf(army.getTotalCosts()));

		final ListView newUnitList = (ListView) findViewById(R.id.army_available_unit_selection);
		newUnitList.setAdapter(new UnitTypeListAdapter(this, army.getUnitTemplates()));
		newUnitList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				armyList.setAdapter(null);
				army.getUnits().add(CloneUtil.clone((Unit) parent.getAdapter().getItem(position), Unit.CREATOR));
				armyList.setAdapter(newUnitAdapter());
				pointLabel.setText(String.valueOf(army.getTotalCosts()));
				UIUtil.hide(selectionView);
				FileUtil.storeArmy(army, ArmyActivity.this);
			}
		});

		selectionView = findViewById(R.id.army_available_units_view);
		UIUtil.show(selectionView, army.getUnits().size() == 0);

		final View abort = findViewById(R.id.army_select_abort);
		abort.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				UIUtil.hide(selectionView);
			}
		});
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

	private UnitListAdapter newUnitAdapter() {
		final UnitListAdapter adapter = new UnitListAdapter(this, army.getUnits());
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
		return adapter;
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
			break;

		default:
			break;
		}
		return true;
	}
}
