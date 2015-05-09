package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Unit;

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
}
