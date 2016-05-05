package de.game_coding.armypicker.util;

import java.util.Collection;

import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;

public final class UnitUtils {

	private UnitUtils() {
	}

	public static UnitStats getStats(final Collection<Integer> references, final Collection<UnitStats> list) {
		UnitStats result = null;
		for (final UnitStats statsType : list) {
			for (final Integer id : references) {
				final StatsEntry stats = statsType.find(id);
				if (stats != null) {
					if (result == null) {
						result = new UnitStats(statsType.getHeaders());
					}
					result.appendEntry(stats);
				}
			}
			if (result != null) {
				return result;
			}
		}
		return new UnitStats();
	}

	public static String getRulesSummaries(final Collection<GameRule> rules) {
		final StringBuilder sb = new StringBuilder();
		for (final GameRule rule : rules) {
			if (sb.length() != 0) {
				sb.append(", ").append(rule.getTitle());
			} else {
				sb.append(rule.getTitle());
			}
		}
		return sb.toString();
	}
}
