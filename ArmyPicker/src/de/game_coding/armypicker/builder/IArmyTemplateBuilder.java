package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.Unit;

public interface IArmyTemplateBuilder {

	public abstract Unit[] getTemplates();

	public abstract String getName();

}