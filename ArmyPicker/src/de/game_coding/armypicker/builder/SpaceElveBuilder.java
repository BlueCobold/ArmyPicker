package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.OptionRule;
import de.game_coding.armypicker.model.Type;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitOption;
import de.game_coding.armypicker.model.UnitOptionGroup;
import de.game_coding.armypicker.model.UnitOptionGroup.GroupType;
import de.game_coding.armypicker.model.UnitStats;

public class SpaceElveBuilder implements IArmyTemplateBuilder {

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
		return "Space Elves";
	}

	@Override
	public UnitStats[] getStats() {
		return new UnitStats[0];
	}
}
