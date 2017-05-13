package de.game_coding.armypicker.viewgroups;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;

@EViewGroup(R.layout.chance_view)
public class ChanceCalculator extends RelativeLayout {

	@ViewById(R.id.chance_edit_bs)
	protected EditText fieldBs;

	@ViewById(R.id.chance_edit_s)
	protected EditText fieldStrength;

	@ViewById(R.id.chance_edit_t)
	protected EditText fieldToughness;

	@ViewById(R.id.chance_edit_save)
	protected EditText fieldSave;

	@ViewById(R.id.chance_edit_fpn)
	protected EditText fieldFnp;

	@ViewById(R.id.chance_edit_rolls)
	protected EditText fieldRolls;

	@ViewById(R.id.chance_result)
	protected TextView resultView;

	@ViewById(R.id.chance_edit_wounds)
	protected EditText fieldWounds;

	@ViewById(R.id.chance_edit_ap)
	protected EditText fieldAp;

	@ViewById(R.id.chance_edit_rend)
	protected EditText fieldRend;

	@ViewById(R.id.chance_average)
	protected TextView average;

	@ViewById(R.id.chance_per)
	protected TextView chancePer;

	@ViewById(R.id.chance_hits)
	protected TextView hits;

	@ViewById(R.id.chance_hit_percent)
	protected TextView hitsPercent;

	@ViewById(R.id.chance_wounds)
	protected TextView wounds;

	@ViewById(R.id.chance_wounds_percent)
	protected TextView woundsPercent;

	@ViewById(R.id.chance_saved_armours)
	protected TextView saved;

	@ViewById(R.id.chance_saved_percent)
	protected TextView savedPercent;

	@ViewById(R.id.chance_saved_fnp)
	protected TextView fnpSaved;

	@ViewById(R.id.chance_fnp_percent)
	protected TextView fnpSavedPercent;

	@ViewById(R.id.chance_reroll_to_wound)
	protected CheckBox reRollToWound;

	@ViewById(R.id.chance_edit_cover)
	protected EditText fieldCover;

	@ViewById(R.id.chance_cover_label)
	protected TextView coverLabel;

	@ViewById(R.id.chance_reroll_to_hit)
	protected CheckBox reRollToHit;

	@ViewById(R.id.chance_page)
	protected TextView page;

	private final List<Settings> settings = new ArrayList<Settings>();
	private int settingsIndex = 0;

	public ChanceCalculator(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	private class Settings {
		private final List<String> inputs = new ArrayList<String>();
		private final List<Boolean> checks = new ArrayList<Boolean>();

		public Settings() {
			for (int i = 0; i < 10; i++) {
				inputs.add("");
			}
			checks.add(false);
			checks.add(false);
			save();
		}

		public void save() {
			inputs.set(0, fieldAp.getText().toString());
			inputs.set(1, fieldBs.getText().toString());
			inputs.set(2, fieldCover.getText().toString());
			inputs.set(3, fieldFnp.getText().toString());
			inputs.set(4, fieldRend.getText().toString());
			inputs.set(5, fieldRolls.getText().toString());
			inputs.set(6, fieldSave.getText().toString());
			inputs.set(7, fieldStrength.getText().toString());
			inputs.set(8, fieldToughness.getText().toString());
			inputs.set(9, fieldWounds.getText().toString());
			checks.set(0, reRollToHit.isChecked());
			checks.set(1, reRollToWound.isChecked());
		}

		public void restore() {
			fieldAp.setText(inputs.get(0));
			fieldBs.setText(inputs.get(1));
			fieldCover.setText(inputs.get(2));
			fieldFnp.setText(inputs.get(3));
			fieldRend.setText(inputs.get(4));
			fieldRolls.setText(inputs.get(5));
			fieldSave.setText(inputs.get(6));
			fieldStrength.setText(inputs.get(7));
			fieldToughness.setText(inputs.get(8));
			fieldWounds.setText(inputs.get(9));
			reRollToHit.setChecked(checks.get(0));
			reRollToWound.setChecked(checks.get(1));
		}
	}

	@AfterViews
	protected void onAfterViews() {
		settings.add(new Settings());
		calculate();
	}

	@TextChange({ R.id.chance_edit_bs, R.id.chance_edit_cover, R.id.chance_edit_fpn, R.id.chance_edit_rolls,
		R.id.chance_edit_s, R.id.chance_edit_save, R.id.chance_edit_t, R.id.chance_edit_ap, R.id.chance_edit_rend,
		R.id.chance_edit_wounds })
	protected void calculate() {
		final int rolls = get(fieldRolls);
		final int wounds = get(fieldWounds);
		final double chance = calcChance(rolls);
		updateChance(rolls, wounds, chance);
		chancePer.setText(Double.toString(((int) (chance * 100)) / 1.0) + "%");
		average.setText(Double.toString(((int) Math.round(rolls * chance * 100)) / 100.0));
	}

	@CheckedChange({ R.id.chance_reroll_to_hit, R.id.chance_reroll_to_wound })
	protected void onCheckedChange() {
		calculate();
	}

	@Click(R.id.chance_add_entry)
	protected void onAddSettings() {
		settings.get(settingsIndex).save();
		settings.add(new Settings());
		settingsIndex = settings.size() - 1;
		page.setText("" + (settingsIndex + 1) + " / " + settings.size());
	}

	@Click(R.id.chance_next)
	protected void onNextSettings() {
		settings.get(settingsIndex).save();
		settingsIndex = Math.min(settings.size() - 1, settingsIndex + 1);
		settings.get(settingsIndex).restore();
		page.setText("" + (settingsIndex + 1) + " / " + settings.size());
	}

	@Click(R.id.chance_prev)
	protected void onPreviousSettings() {
		settings.get(settingsIndex).save();
		settingsIndex = Math.max(0, settingsIndex - 1);
		settings.get(settingsIndex).restore();
		page.setText("" + (settingsIndex + 1) + " / " + settings.size());
	}

	private double calcChance(final int rolls) {
		final double hitChance = calcToHit(rolls);
		final double woundChance = calcToWound(hitChance * rolls);
		return hitChance * woundChance * calcToFailSave(hitChance * woundChance * rolls);
	}

	private double calcToFailSave(final double rolls) {
		final int save = get(fieldSave);
		double saveChance;
		double rendSaveChance;
		double rendChance = 0;
		final int ap = Math.abs(get(fieldAp));
		final int rending = get(fieldRend);
		if (rending > 0) {
			final double w = calcBasicWoundChance();
			// Chance to rend is based on chance to wound. Simple example: 6+ to
			// wound automatically also always rends for each wound and not just
			// with 1/6
			rendChance = (1 / w) / 6;
			if (reRollToWound.isChecked()) {
				rendChance = (2 - w) / 6;
			}
		}
		if (save <= 0 || save > 6) {
			saveChance = 1;
			rendSaveChance = 1;
			savedPercent.setText("-");
			saved.setText("-");
		} else {
			saveChance = (Math.max(save + ap, 2) - 1) / 6.0;
			rendSaveChance = (Math.min(7, Math.max(save + rending, 2)) - 1) / 6.0;
			savedPercent.setText(Double.toString(((int) ((1 - saveChance) * 1000)) / 10.0) + "%");
			saved.setText(
				Double.toString(((int) (rolls * (1 - (rendChance + (1 - rendChance) * saveChance)) * 10)) / 10.0));
		}
		final int fnp = get(fieldFnp);
		double fnp_chance = fnp / 6.0;
		if (fnp <= 0 || fnp > 6) {
			fnp_chance = 1;
			fnpSavedPercent.setText("-");
			fnpSaved.setText("-");
		} else {
			fnp_chance = (Math.max(fnp, 2) - 1) / 6.0;
			fnpSavedPercent.setText(Double.toString(((int) ((1 - fnp_chance) * 1000)) / 10.0) + "%");
			fnpSaved.setText(
				Double.toString(((int) (rolls * (1 - (rendChance + (1 - rendChance) * fnp_chance)) * 10)) / 10.0));
		}
		return fnp_chance * (rendChance * rendSaveChance + (1 - rendChance) * saveChance);
	}

	private double calcToWound(final double rolls) {
		double chance = calcBasicWoundChance();
		if (reRollToWound.isChecked()) {
			chance += (1 - chance) * chance;
		}
		woundsPercent.setText(Double.toString(((int) (chance * 1000)) / 10.0) + "%");
		wounds.setText(Double.toString(((int) (rolls * chance * 10)) / 10.0));
		return chance;
	}

	private double calcBasicWoundChance() {
		final int t = get(fieldToughness);
		final int s = get(fieldStrength);
		final float c = s / (float) t;
		int x = 4;
		if (c < 1) {
			x = 5;
		}
		if (c <= 0.5) {
			x = 6;
		}
		if (c > 1) {
			x = 3;
		}
		if (c >= 2) {
			x = 2;
		}
		final double chance = (7 - x) / 6.0;
		return chance;
	}

	private double calcToHit(final int rolls) {
		double chance = (7 - get(fieldBs)) / 6.0;
		if (reRollToHit.isChecked()) {
			chance += (1 - chance) * chance;
		}
		hits.setText(Double.toString(((int) (rolls * chance * 10)) / 10.0));
		hitsPercent.setText(Double.toString(((int) (chance * 1000)) / 10.0) + "%");
		return chance;
	}

	private int get(final EditText field) {
		try {
			return field != null ? Integer.parseInt(field.getText().toString()) : 0;
		} catch (final NumberFormatException ex) {
			return 0;
		}
	}

	private void updateChance(final int rolls, final int atLeast, final double p) {
		if (rolls < atLeast || p <= 0 || p >= 1) {
			resultView.setText("-");
			return;
		}
		final int n = atLeast;
		final int k = rolls;
		final double q = 1 - p;
		double sum = 0;
		for (int i = 0; i < n; i++) {
			sum += choose(k, i) * Math.pow(p, i) * Math.pow(q, k - i);
		}
		final double result = 1 - sum;
		resultView.setText(Double.toString(((int) (result * 100000)) / 1000.0) + "%");
	}

	private double choose(final double n, final double k) {
		if (k == 0) {
			return 1;
		}
		return (n * choose(n - 1, k - 1)) / k;
	}
}
