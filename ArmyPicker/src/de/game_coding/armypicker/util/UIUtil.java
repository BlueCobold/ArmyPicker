package de.game_coding.armypicker.util;

import android.view.View;

public final class UIUtil {

	private UIUtil() {
	}

	public static void hide(final View view) {
		view.setEnabled(false);
		view.setVisibility(View.INVISIBLE);
	}

	public static void show(final View view) {
		view.setEnabled(true);
		view.setVisibility(View.VISIBLE);
	}

	public static void show(final View view, final boolean show) {
		if (show) {
			show(view);
		} else {
			hide(view);
		}
	}
}
