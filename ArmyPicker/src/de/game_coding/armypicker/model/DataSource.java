package de.game_coding.armypicker.model;

import android.content.Context;
import de.game_coding.armypicker.builder.CharacterOptionBuilder;
import de.game_coding.armypicker.util.FileUtil;

public final class DataSource {

	private static final String CHARACTER_OPTIONS_FILE = "characterOptions";

	private DataSource() {
	}

	public static CharacterOption getAvailableOptions(final Context context) {
		CharacterOption result = FileUtil.readFromFile(CHARACTER_OPTIONS_FILE, context, CharacterOption.CREATOR);
		if (result == null) {
			result = new CharacterOptionBuilder().build();
			storeAvailableOptions(result, context);
		}
		return result;
	}

	public static void storeAvailableOptions(final CharacterOption options, final Context context) {
		FileUtil.storeToFile(options, CHARACTER_OPTIONS_FILE, context);
	}
}
