package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Battalion;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;

public class UndeadSpaceElveBuilder implements IArmyTemplateBuilder {

	private static final Unit[] UNITS = new Unit[0];

	@Override
	public Unit[] getTemplates() {
		return UNITS;
	}

	@Override
	public String getName() {
		return "Undead Space Elves";
	}

	@Override
	public String getVersion() {
		return "8.0.0";
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
	public Battalion[] getBattalions() {
		return new Battalion[0];
	}

}
