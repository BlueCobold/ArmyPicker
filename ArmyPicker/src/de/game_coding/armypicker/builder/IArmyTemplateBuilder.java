package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;

public interface IArmyTemplateBuilder {

	public abstract Unit[] getTemplates();

	public abstract String getName();

	public abstract UnitStats getStats();
}