package de.game_coding.armypicker.builder;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitStats;
import de.game_coding.armypicker.model.UnitStats.StatsEntry;
import junit.framework.TestCase;

public class StatsTest extends TestCase {

	@Test
	public void testWeaponReferences() {
		testWeaponReferences(new SpaceElveBuilder());
		testWeaponReferences(new SpaceMonkBuilder());
		testWeaponReferences(new SpaceClownBuilder());
		testWeaponReferences(new GreenSpaceMonkBuilder());
		testWeaponReferences(new EvilSpaceElveBuilder());
	}

	@Test
	public void testStatsReferences() {
		testStatsReferences(new SpaceElveBuilder());
		// testStatsReferences(new SpaceMonkBuilder());
		testStatsReferences(new SpaceClownBuilder());
		// testStatsReferences(new GreenSpaceMonkBuilder());
		testStatsReferences(new EvilSpaceElveBuilder());
	}

	private void testWeaponReferences(final IArmyTemplateBuilder builder) {
		for (final UnitStats unit : builder.getStats()) {
			for (final StatsEntry entry : unit.getEntries()) {
				testReferences(entry, builder.getWeapons());
			}
		}
	}

	private void testStatsReferences(final IArmyTemplateBuilder builder) {
		for (final Unit unit : builder.getTemplates()) {
			testReferences(unit.getStatsReferences(), builder.getStats());
		}
	}

	private void testReferences(final List<Integer> statsReferences, final UnitStats[] stats) {
		for (final int statsRef : statsReferences) {
			boolean found = false;
			for (final UnitStats units : stats) {
				for (final StatsEntry unitStat : units.getEntries()) {
					found |= unitStat.getId() == statsRef;
				}
			}
			Assert.assertTrue("StatsRef not found: " + statsRef, found);
		}
	}

	private void testReferences(final StatsEntry entry, final UnitStats weapons) {
		for (final int gearRef : entry.getGearReferences()) {
			boolean found = false;
			for (final StatsEntry weapon : weapons.getEntries()) {
				found |= weapon.getId() == gearRef;
			}
			Assert.assertTrue("WeaponRef not found: " + gearRef, found);
		}
	}
}
