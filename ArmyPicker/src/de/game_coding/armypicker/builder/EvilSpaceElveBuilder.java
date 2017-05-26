package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Battalion;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;

public class EvilSpaceElveBuilder implements IArmyTemplateBuilder {

	private static final Unit[] UNITS = new Unit[] {

	};

	/*
	 * (non-Javadoc)
	 *
	 * @see de.game_coding.armypicker.builder.UnitBuilder#getTemplates()
	 */
	@Override
	public Unit[] getTemplates() {
		return UNITS;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.game_coding.armypicker.builder.UnitBuilder#getName()
	 */
	@Override
	public String getName() {
		return "Evil Space Elves";
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

	@Override
	public String getVersion() {
		return "8.0.0";
	}
}
