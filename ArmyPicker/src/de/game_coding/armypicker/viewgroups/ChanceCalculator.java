package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
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

	@ViewById(R.id.chance_rending)
	protected CheckBox rending;

	@ViewById(R.id.chance_reroll_to_wound)
	protected CheckBox reRollToWound;

	@ViewById(R.id.chance_edit_cover)
	protected EditText fieldCover;

	@ViewById(R.id.chance_cover_label)
	protected TextView coverLabel;

	@ViewById(R.id.chance_reroll_to_hit)
	protected CheckBox reRollToHit;

	@ViewById(R.id.chance_reroll_all_ones)
	protected CheckBox reRollAllOnes;

	public ChanceCalculator(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@CheckedChange({ R.id.chance_rending, R.id.chance_reroll_to_wound, R.id.chance_reroll_to_hit,
		R.id.chance_reroll_all_ones })
	protected void onCheckedChanged() {
		coverLabel.setVisibility(rending.isChecked() ? View.VISIBLE : View.GONE);
		fieldCover.setVisibility(rending.isChecked() ? View.VISIBLE : View.GONE);
		calculate();
	}

	@AfterViews
	@TextChange({ R.id.chance_edit_bs, R.id.chance_edit_cover, R.id.chance_edit_fpn, R.id.chance_edit_rolls,
		R.id.chance_edit_s, R.id.chance_edit_save, R.id.chance_edit_t, R.id.chance_edit_wounds })
	protected void calculate() {
		final int rolls = get(fieldRolls);
		final int wounds = get(fieldWounds);
		final double chance = calcChance(rolls);
		updateChance(rolls, wounds, chance);
		chancePer.setText(Double.toString(((int) (chance * 1000)) / 10.0) + "%");
		average.setText(Double.toString(((int) (rolls * chance * 10)) / 10.0));
	}

	private double calcChance(final int rolls) {
		final double hitChance = calcToHit(rolls);
		final double woundChance = calcToWound(hitChance * rolls);
		return hitChance * woundChance * calcToFailSave(hitChance * woundChance * rolls);
	}

	private double calcToFailSave(final double rolls) {
		final int cover = rending.isChecked() ? get(fieldCover) : 0;
		final int save = get(fieldSave);
		if (cover > 0 || save == 0) {
			Math.min(save, cover);
		}
		double save_chance;
		double rendChance = 0;
		if (rending.isChecked()) {
			final double w = calcBasicWoundChance();
			// Chance to rend is based on chance to wound. Simple example: 6+ to
			// wound automatically also always rends for each wound and not just
			// with 1/6
			rendChance = (1 / w) / 6;
			if (reRollToWound.isChecked()) {
				rendChance = (2 - w) / 6;
			} else if (reRollAllOnes.isChecked()) {
				rendChance = 7 / 36.0;
			}
		}
		if (save <= 0 || save > 6) {
			save_chance = 1;
			savedPercent.setText("-");
			saved.setText("-");
		} else {
			save_chance = (Math.max(save, 2) - 1) / 6.0;
			savedPercent.setText(Double.toString(((int) ((1 - save_chance) * 1000)) / 10.0) + "%");
			saved.setText(
				Double.toString(((int) (rolls * (1 - (rendChance + (1 - rendChance) * save_chance)) * 10)) / 10.0));
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
		double cover_chance = cover / 6.0;
		if (cover <= 0 || cover > 6) {
			cover_chance = 1;
		} else {
			cover_chance = (Math.max(cover, 2) - 1) / 6.0;
		}
		return fnp_chance * (rendChance * cover_chance + (1 - rendChance) * save_chance);
	}

	private double calcToWound(final double rolls) {
		double chance = calcBasicWoundChance();
		if (reRollToWound.isChecked()) {
			chance += (1 - chance) * chance;
		} else if (reRollAllOnes.isChecked()) {
			chance += 1 / 6.0 * chance;
		}
		woundsPercent.setText(Double.toString(((int) (chance * 1000)) / 10.0) + "%");
		wounds.setText(Double.toString(((int) (rolls * chance * 10)) / 10.0));
		return chance;
	}

	private double calcBasicWoundChance() {
		final int t = get(fieldToughness);
		final int s = get(fieldStrength);
		int x = 4 + t - s;
		if (x == 7) {
			x = 6;
		}
		if (x > 6) {
			return 0;
		}
		if (x < 2) {
			x = 2;
		}
		final double chance = (7 - x) / 6.0;
		return chance;
	}

	private double calcToHit(final int rolls) {
		final double[] chances = { 0, //
			1 / 6.0, //
			2 / 6.0, //
			3 / 6.0, //
			4 / 6.0, //
			5 / 6.0, //
			5 / 6.0, //
			5 / 6.0, //
			5 / 6.0, //
			5 / 6.0, //
			5 / 6.0 };
		double chance = chances[get(fieldBs)];
		if (reRollToHit.isChecked()) {
			chance += (1 - chance) * chance;
		} else if (reRollAllOnes.isChecked()) {
			chance += 1 / 6.0 * chance;
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
