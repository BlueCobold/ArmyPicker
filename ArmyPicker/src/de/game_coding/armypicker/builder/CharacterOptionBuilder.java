package de.game_coding.armypicker.builder;

import de.game_coding.armypicker.model.CharacterOption;

public class CharacterOptionBuilder {

	public CharacterOption build() {
		return buildTypes();
	}

	private CharacterOption buildTypes() {
		final CharacterOption types = new CharacterOption("All");
		types.getSubOptions().add(buildPsyker());
		types.getSubOptions().add(buildWarlord());
		return types;
	}

	private CharacterOption buildWarlord() {
		final CharacterOption types = new CharacterOption("Warlord");
		types.getSubOptions().add(buildWarlordTable1());
		types.getSubOptions().add(buildWarlordTable2());
		types.getSubOptions().add(buildWarlordTable3());
		types.getSubOptions().add(buildWarlordTable4());
		return types;
	}

	private CharacterOption buildWarlordTable1() {
		final CharacterOption types = new CharacterOption("Warlord Table 1");
		types.addSubOption("Option 1");
		types.addSubOption("Option 2");
		types.addSubOption("Option 3");
		types.addSubOption("Option 4");
		types.addSubOption("Option 5");
		types.addSubOption("Option 6");
		return types;
	}

	private CharacterOption buildWarlordTable2() {
		final CharacterOption types = new CharacterOption("Warlord Table 2");
		types.addSubOption("Option 1");
		types.addSubOption("Option 2");
		types.addSubOption("Option 3");
		types.addSubOption("Option 4");
		types.addSubOption("Option 5");
		types.addSubOption("Option 6");
		return types;
	}

	private CharacterOption buildWarlordTable3() {
		final CharacterOption types = new CharacterOption("Warlord Table 3");
		types.addSubOption("Option 1");
		types.addSubOption("Option 2");
		types.addSubOption("Option 3");
		types.addSubOption("Option 4");
		types.addSubOption("Option 5");
		types.addSubOption("Option 6");
		return types;
	}

	private CharacterOption buildWarlordTable4() {
		final CharacterOption types = new CharacterOption("Warlord Table 4");
		types.addSubOption("Option 1");
		types.addSubOption("Option 2");
		types.addSubOption("Option 3");
		types.addSubOption("Option 4");
		types.addSubOption("Option 5");
		types.addSubOption("Option 6");
		return types;
	}

	private CharacterOption buildPsyker() {
		final CharacterOption types = new CharacterOption("Psyker");
		types.getSubOptions().add(buildSpellsTable1());
		types.getSubOptions().add(buildSpellsTable2());
		return types;
	}

	private CharacterOption buildSpellsTable1() {
		final CharacterOption types = new CharacterOption("Psyker Table 1");
		types.addSubOption("Spell 1");
		types.addSubOption("Spell 2");
		types.addSubOption("Spell 3");
		types.addSubOption("Spell 4");
		types.addSubOption("Spell 5");
		types.addSubOption("Spell 6");
		return types;
	}

	private CharacterOption buildSpellsTable2() {
		final CharacterOption types = new CharacterOption("Psyker Table 2");
		types.addSubOption("Spell 1");
		types.addSubOption("Spell 2");
		types.addSubOption("Spell 3");
		types.addSubOption("Spell 4");
		types.addSubOption("Spell 5");
		types.addSubOption("Spell 6");
		return types;
	}
}
