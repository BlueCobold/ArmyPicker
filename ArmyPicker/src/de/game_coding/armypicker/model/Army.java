package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;
import de.game_coding.armypicker.model.creators.ArmyCreator;

public class Army extends Model {

	public static final Parcelable.Creator<Army> CREATOR = new ArmyCreator();

	private enum FileVersions {
		CHARACTER_OPTIONS(1);

		private int value;

		private FileVersions(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private String name = "";

	private String templateName = "";

	private Unit[] unitTemplates = new Unit[0];

	private List<Unit> units = new ArrayList<Unit>();

	private Battalion[] battalionTemplates = new Battalion[0];

	private List<Battalion> battalions = new ArrayList<Battalion>();

	private int id;

	private List<UnitStats> stats = new ArrayList<UnitStats>();

	private UnitStats weapons = new UnitStats();

	private String templateVersion;

	private List<Character> characters = new ArrayList<Character>();

	private final Map<Unit, Character> autoCharacters = new HashMap<Unit, Character>();

	public Army(final String name, final Unit[] unitTemplates, final String templateVersion) {
		this.name = name;
		this.templateName = name;
		this.unitTemplates = unitTemplates;
		this.templateVersion = templateVersion;
	}

	public Army(final Parcel source) {
		readFromParcel(source);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Unit[] getUnitTemplates() {
		return unitTemplates;
	}

	public void setUnitTemplates(final Unit[] entries) {
		if (entries != null) {
			unitTemplates = entries.clone();
		}
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void addUnit(final Unit unit) {
		int max = 0;
		for (final Unit u : units) {
			max = Math.max(max, u.getId());
		}
		unit.setId(max + 1);
		units.add(unit);
		if (unit.getSuppliedOptions().size() == 0 && !unit.isCharacter()) {
			return;
		}
		final Character character = new Character();
		character.setName(unit.getName());
		character.getOptions().addAll(unit.getSuppliedOptions());
		autoCharacters.put(unit, character);
	}

	public void withBattalionTemplates(final Battalion[] templates) {
		battalionTemplates = templates.clone();
	}

	public void addBattalion(final Battalion battalion) {
		battalions.add(battalion);
	}

	public void removeBattalion(final Battalion battalion) {
		battalions.remove(battalion);
	}

	public List<Battalion> getBattalions() {
		return battalions;
	}

	public void removeUnit(final Unit unit) {
		units.remove(unit);
		autoCharacters.remove(unit);
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(name);
		dest.writeString(templateName);
		dest.writeString(templateVersion);
		dest.writeInt(id);

		dest.writeInt(unitTemplates.length);
		dest.writeTypedArray(unitTemplates, flags);

		writeList(dest, units);
		writeList(dest, stats);
		weapons.writeToParcel(dest, flags);
		writeList(dest, characters);
		dest.writeInt(autoCharacters.size());
		for (final Entry<Unit, Character> entry : autoCharacters.entrySet()) {
			dest.writeInt(units.indexOf(entry.getKey()));
			entry.getValue().writeToParcel(dest, flags);
		}
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		name = source.readString();
		templateName = source.readString();
		templateVersion = source.readString();
		id = source.readInt();

		final int size = source.readInt();
		unitTemplates = new Unit[size];
		source.readTypedArray(unitTemplates, Unit.CREATOR);

		final int bsize = source.readInt();
		battalionTemplates = new Battalion[bsize];
		source.readTypedArray(battalionTemplates, Battalion.CREATOR);

		units = readList(source, Unit.CREATOR);
		stats = readList(source, UnitStats.CREATOR);
		weapons = new UnitStats(source);
		battalions = readList(source, Battalion.CREATOR);

		if (getFileVersion() >= FileVersions.CHARACTER_OPTIONS.getValue()) {
			characters = readList(source, Character.CREATOR);
			final int entries = source.readInt();
			for (int i = 0; i < entries; i++) {
				final int index = source.readInt();
				autoCharacters.put(units.get(index), new Character(source));
			}
		}
	}

	@Override
	protected int getFeatureVersion() {
		return FileVersions.CHARACTER_OPTIONS.getValue();
	}

	public int getTotalCosts() {
		int total = 0;
		for (final Unit unit : units) {
			total += unit.getTotalCosts();
		}
		return total;
	}

	public Army attachStats(final UnitStats... stats) {
		this.stats.clear();
		for (final UnitStats stat : stats) {
			this.stats.add(stat);
		}
		return this;
	}

	public List<UnitStats> getStats() {
		return stats;
	}

	public void setStats(final List<UnitStats> stats) {
		this.stats.clear();
		this.stats.addAll(stats);
	}

	public Army attachWeapons(final UnitStats weapons) {
		this.weapons = weapons;
		return this;
	}

	public UnitStats getWeapons() {
		return weapons;
	}

	public void setWeapons(final UnitStats weapons) {
		this.weapons = weapons;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getTemplateVersion() {
		return templateVersion;
	}

	public void setTemplateVersion(final String templateVersion) {
		this.templateVersion = templateVersion;
	}

	public Collection<Character> getCharacters() {
		return characters;
	}

	public Collection<Character> getAutoCharacters() {
		final List<Character> sortedChars = new ArrayList<Character>(autoCharacters.values());
		Collections.sort(sortedChars, new Comparator<Character>() {

			@Override
			public int compare(final Character lhs, final Character rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
		});
		return Collections.unmodifiableCollection(sortedChars);
	}
}
