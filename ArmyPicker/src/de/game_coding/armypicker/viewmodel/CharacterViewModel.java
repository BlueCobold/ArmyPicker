package de.game_coding.armypicker.viewmodel;

import de.game_coding.armypicker.model.Character;

public class CharacterViewModel {

	private final boolean canBeDeleted;
	private final Character character;
	private final boolean showSummaries;

	public CharacterViewModel(final Character c, final boolean deletable, final boolean showSummaries) {
		this.character = c;
		canBeDeleted = deletable;
		this.showSummaries = showSummaries;
	}

	public boolean canBeDeleted() {
		return canBeDeleted;
	}

	public Character getCharacter() {
		return character;
	}

	public boolean isShowSummaries() {
		return showSummaries;
	}
}
