package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import de.game_coding.armypicker.model.creators.UnitCreator;
import de.game_coding.armypicker.model.creators.UnitOptionGroupCreator;

public class Unit extends Model {

	public static final Parcelable.Creator<Unit> CREATOR = new UnitCreator();

	private int id;
	private String name = "";
	private UnitType type = UnitType.STANDARD;
	private int points;
	private int amount = 1;
	private int initialAmount = 1;
	private int maxAmount = 1;
	private String subtitle = "";
	private List<UnitOptionGroup> options = new ArrayList<UnitOptionGroup>();
	private final List<Integer> statsReferences = new ArrayList<Integer>();
	private final List<Integer> weaponReferences = new ArrayList<Integer>();
	private List<CharacterOption> suppliedOptions = new ArrayList<CharacterOption>();

	private boolean isCharacter = false;

	public Unit(final String name, final UnitType type, final int points, final int amount, final int maxAmount,
		final UnitOptionGroup... options) {
		this.name = name;
		this.type = type;
		this.points = points;
		this.amount = amount;
		initialAmount = amount;
		this.maxAmount = maxAmount;
		this.options = Arrays.asList(options);
		setOptionAmounts();
	}

	public Unit(final String name, final UnitType type, final int points, final UnitOptionGroup... options) {
		this.name = name;
		this.type = type;
		this.points = points;
		this.options = Arrays.asList(options);
		setOptionAmounts();
	}

	public Unit(final String name, final UnitType type, final int points) {
		this.name = name;
		this.type = type;
		this.points = points;
		this.amount = 1;
		setOptionAmounts();
	}

	public Unit(final Parcel source) {
		readFromParcel(source);
		setOptionAmounts();
		forceRuleCheck();
	}

	private void forceRuleCheck() {
		for (final UnitOptionGroup group : options) {
			group.validateAmounts();
		}
	}

	private void setOptionAmounts() {
		for (final UnitOptionGroup group : options) {
			group.setLimit(amount);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public UnitType getType() {
		return type;
	}

	public int getPoints() {
		return points;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
		setOptionAmounts();
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public int getInitialAmount() {
		return initialAmount;
	}

	public List<UnitOptionGroup> getOptions() {
		return options;
	}

	public Unit withStatsRef(final int... ids) {
		for (final int id : ids) {
			statsReferences.add(id);
		}
		return this;
	}

	public List<Integer> getStatsReferences() {
		return statsReferences;
	}

	public Unit withWeaponRef(final int... ids) {
		for (final int id : ids) {
			weaponReferences.add(id);
		}
		return this;
	}

	public Unit withCharacterOption(final CharacterOption option) {
		suppliedOptions.add(option);
		return this;
	}

	public List<Integer> getWeaponReferences() {
		return weaponReferences;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public Unit withSubtitle(final String subtitle) {
		this.subtitle = subtitle;
		return this;
	}

	public Unit asCharacter(final boolean value) {
		isCharacter = value;
		return this;
	}

	public boolean isCharacter() {
		return isCharacter;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getTotalCosts() {
		return amount * points + getTotalOptionCosts();
	}

	public int getTotalOptionCosts() {
		int total = 0;
		for (final UnitOptionGroup group : options) {
			total += group.getTotalCosts();
		}
		return total;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(name);
		dest.writeString(subtitle);
		dest.writeInt(id);
		dest.writeInt(points);
		dest.writeInt(type.ordinal());
		dest.writeInt(amount);
		dest.writeInt(maxAmount);
		dest.writeInt(initialAmount);

		dest.writeTypedList(options);
		dest.writeInt(statsReferences.size());
		for (final Integer i : statsReferences) {
			dest.writeInt(i);
		}
		dest.writeInt(weaponReferences.size());
		for (final Integer i : weaponReferences) {
			dest.writeInt(i);
		}
		writeList(dest, suppliedOptions);
		dest.writeInt(isCharacter ? 1 : 0);
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		name = source.readString();
		subtitle = source.readString();
		id = source.readInt();
		points = source.readInt();
		type = UnitType.values()[source.readInt()];
		amount = source.readInt();
		maxAmount = source.readInt();
		initialAmount = source.readInt();

		options = new ArrayList<UnitOptionGroup>();
		source.readTypedList(options, new UnitOptionGroupCreator(OptionRule.CREATOR));
		for (final UnitOptionGroup group : options) {
			for (final IRule rule : group.getRules()) {
				((OptionRule) rule).setTargets(options);
			}
		}

		statsReferences.clear();
		int refCount = source.readInt();
		for (int i = 0; i < refCount; i++) {
			statsReferences.add(source.readInt());
		}
		weaponReferences.clear();
		refCount = source.readInt();
		for (int i = 0; i < refCount; i++) {
			weaponReferences.add(source.readInt());
		}
		suppliedOptions = readList(source, CharacterOption.CREATOR);
		isCharacter = source.readInt() > 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public Unit addRule(final int targetGroupId, final OptionRule rule) {
		rule.setTargets(options);
		for (final UnitOptionGroup group : options) {
			if (group.getId() == targetGroupId) {
				group.getRules().add(rule);
				return this;
			}
		}
		throw new IllegalArgumentException("No group with id=" + targetGroupId + " defined");
	}

	public Collection<CharacterOption> getSuppliedOptions() {
		return Collections.unmodifiableCollection(suppliedOptions);
	}

	@Override
	public String toString() {
		return name;
	}
}
