package de.game_coding.armypicker;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class ChanceCalculator {

	private final EditText fieldBs;
	private final EditText fieldStrength;
	private final EditText fieldToughness;
	private final EditText fieldSave;
	private final EditText fieldFnp;
	private final EditText fieldRolls;
	private final TextView resultView;
	private final EditText fieldWounds;
	private final TextView average;
	private final TextView chancePer;
	private final TextView hits;
	private final TextView hitsPercent;
	private final TextView wounds;
	private final TextView woundsPercent;
	private final TextView saved;
	private final TextView savedPercent;
	private final TextView fnpSaved;
	private final TextView fnpSavedPercent;
	private final CheckBox rending;
	private final CheckBox reRollToWound;
	private final EditText fieldCover;
	private final TextView coverLabel;
	private final CheckBox reRollToHit;
	private final CheckBox reRollAllOnes;

	public ChanceCalculator(final View rootView) {
		fieldWounds = (EditText) rootView.findViewById(R.id.chance_edit_wounds);
		register(fieldWounds);
		fieldRolls = (EditText) rootView.findViewById(R.id.chance_edit_rolls);
		register(fieldRolls);
		fieldBs = (EditText) rootView.findViewById(R.id.chance_edit_bs);
		register(fieldBs);
		fieldStrength = (EditText) rootView.findViewById(R.id.chance_edit_s);
		register(fieldStrength);
		fieldToughness = (EditText) rootView.findViewById(R.id.chance_edit_t);
		register(fieldToughness);
		fieldSave = (EditText) rootView.findViewById(R.id.chance_edit_save);
		register(fieldSave);
		fieldFnp = (EditText) rootView.findViewById(R.id.chance_edit_fpn);
		register(fieldFnp);
		fieldCover = (EditText) rootView.findViewById(R.id.chance_edit_cover);
		register(fieldCover);
		coverLabel = (TextView) rootView.findViewById(R.id.chance_cover_label);
		resultView = (TextView) rootView.findViewById(R.id.chance_result);
		average = (TextView) rootView.findViewById(R.id.chance_average);
		chancePer = (TextView) rootView.findViewById(R.id.chance_per);
		hits = (TextView) rootView.findViewById(R.id.chance_hits);
		hitsPercent = (TextView) rootView.findViewById(R.id.chance_hit_percent);
		wounds = (TextView) rootView.findViewById(R.id.chance_wounds);
		woundsPercent = (TextView) rootView.findViewById(R.id.chance_wounds_percent);
		saved = (TextView) rootView.findViewById(R.id.chance_saved_armours);
		savedPercent = (TextView) rootView.findViewById(R.id.chance_saved_percent);
		fnpSaved = (TextView) rootView.findViewById(R.id.chance_saved_fnp);
		fnpSavedPercent = (TextView) rootView.findViewById(R.id.chance_fnp_percent);
		rending = (CheckBox) rootView.findViewById(R.id.chance_rending);
		register(rending);
		reRollToWound = (CheckBox) rootView.findViewById(R.id.chance_reroll_to_wound);
		register(reRollToWound);
		reRollToHit = (CheckBox) rootView.findViewById(R.id.chance_reroll_to_hit);
		register(reRollToHit);
		reRollAllOnes = (CheckBox) rootView.findViewById(R.id.chance_reroll_all_ones);
		register(reRollAllOnes);
		calculate();
	}

	private void register(final CheckBox box) {
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				coverLabel.setVisibility(rending.isChecked() ? View.VISIBLE : View.GONE);
				fieldCover.setVisibility(rending.isChecked() ? View.VISIBLE : View.GONE);
				calculate();
			}
		});
	}

	private void register(final EditText field) {
		field.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
				calculate();
			}
		});
	}

	private void calculate() {
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
		final int cover = get(fieldCover);
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
			saved.setText(Double
				.toString(((int) (rolls * (1 - (rendChance + (1 - rendChance) * save_chance)) * 10)) / 10.0));
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
			fnpSaved.setText(Double
				.toString(((int) (rolls * (1 - (rendChance + (1 - rendChance) * fnp_chance)) * 10)) / 10.0));
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
		final double[] chances = { 0,//
			1 / 6.0,//
			2 / 6.0,//
			3 / 6.0,//
			4 / 6.0,//
			5 / 6.0,//
			5 / 6.0,//
			5 / 6.0,//
			5 / 6.0,//
			5 / 6.0,//
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
			return Integer.parseInt(field.getText().toString());
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
