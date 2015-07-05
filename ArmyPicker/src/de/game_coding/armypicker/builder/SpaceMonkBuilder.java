package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;

public class SpaceMonkBuilder implements IArmyTemplateBuilder {

	private static final Unit[] UNITS = new Unit[] {};

	@Override
	public Unit[] getTemplates() {
		return UNITS;
	}

	@Override
	public String getName() {
		return "Space Monks";
	}

	@Override
	public UnitStats[] getStats() {
		return new UnitStats[0];
	}

	@Override
	public UnitStats getWeapons() {
		return new UnitStats();
	}

	@Override
	public String getVersion() {
		return "2.7.0";
	}
}
