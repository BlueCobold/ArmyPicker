package de.game_coding.armypicker.builder;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import de.game_coding.armypicker.model.Battalion;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitRequirement;

public class BattalionTest extends TestCase {

	@Test
	public void testBattalionUnitReferences() {
		testUnitReferences(new SpaceElveBuilder());
		testUnitReferences(new SpaceMonkBuilder());
		testUnitReferences(new SpaceClownBuilder());
		testUnitReferences(new GreenSpaceMonkBuilder());
	}

	private void testUnitReferences(final IArmyTemplateBuilder builder) {
		for (final Battalion battalion : builder.getBattalions()) {
			final List<BattalionRequirement> list = new ArrayList<BattalionRequirement>();
			list.add(battalion.getRequirement());
			testReferences(list, builder.getTemplates());
		}
	}

	private void testReferences(final List<BattalionRequirement> requirements, final Unit[] units) {
		for (final BattalionRequirement requirement : requirements) {
			for (final UnitRequirement requiredUnit : requirement.getRequiredUnits()) {
				boolean unitFound = false;
				for (final Unit unit : units) {
					if (unit.getName().equals(requiredUnit.getUnitName())) {
						unitFound = true;
						break;
					}
				}
				Assert.assertTrue("Unit not found: " + requiredUnit.getUnitName(), unitFound);
			}
			testReferences(requirement.getRequiredSubBattalions(), units);
		}
	}
}
