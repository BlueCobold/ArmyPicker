package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;

public interface IArmyTemplateBuilder {

	Unit[] getTemplates();

	String getName();

	String getVersion();

	UnitStats[] getStats();

	UnitStats getWeapons();
}