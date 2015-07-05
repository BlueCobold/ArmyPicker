package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;

public class SpaceClownBuilder implements IArmyTemplateBuilder {

	@Override
	public Unit[] getTemplates() {
		return new Unit[0];
	}

	@Override
	public String getName() {
		return "Space Clowns";
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
		return "3.7.0";
	}
}
