package de.game_coding.armypicker.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;

public final class WeaponUtils {

	private WeaponUtils() {
	}

	public static Collection<StatsEntry> getWeapons(final UnitStats units, final UnitStats weapons) {
		final List<StatsEntry> gear = new ArrayList<StatsEntry>();
		for (final StatsEntry unit : units.getEntries()) {
			for (final int weaponRef : unit.getGearReferences()) {
				gear.add(getWeapon(weaponRef, weapons));
			}
		}
		return gear;
	}

	private static StatsEntry getWeapon(final int weaponRef, final UnitStats weapons) {
		for (final StatsEntry weapon : weapons.getEntries()) {
			if (weapon.getId() == weaponRef) {
				return weapon;
			}
		}
		return null;
	}

	public static Collection<GameRule> getGearRules(final Collection<StatsEntry> weapons) {
		final List<GameRule> rules = new ArrayList<GameRule>();
		for (final StatsEntry stat : weapons) {
			for (final GameRule rule : stat.getGameRules()) {
				boolean in = false;
				for (final GameRule alreadyIn : rules) {
					if (rule.getTitle().equals(alreadyIn.getTitle())
						&& rule.getDescription().equals(alreadyIn.getDescription())) {
						in = true;
						break;
					}
				}
				if (!in) {
					rules.add(rule);
				}
			}
		}
		return rules;
	}
}
