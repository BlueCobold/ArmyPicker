package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import de.game_coding.armypicker.model.creators.UnitStatsCreator;
import android.os.Parcel;

public class UnitStats extends Model {

	public static UnitStatsCreator CREATOR = new UnitStatsCreator();

	public static class StatsEntry {
		private final String name;
		private final String[] values;
		private final List<StatsEntry> secondaries = new ArrayList<StatsEntry>();
		private final int id;
		private final List<Integer> gearReferences = new ArrayList<Integer>();

		public StatsEntry(final int id, final String name, final String... values) {
			this.id = id;
			this.name = name;
			this.values = values;
		}

		public StatsEntry(final String name, final String... values) {
			id = 0;
			this.name = name;
			this.values = values;
		}

		public String getName() {
			return name;
		}

		public String[] getValues() {
			return values;
		}

		public StatsEntry addSecondary(final String name, final String... values) {
			secondaries.add(new StatsEntry(name, values));
			return this;
		}

		public List<StatsEntry> getSecondaries() {
			return secondaries;
		}

		public int getId() {
			return id;
		}

		public List<Integer> getGearReferences() {
			return gearReferences;
		}
	}

	private String[] headers;
	private final List<StatsEntry> entries = new ArrayList<UnitStats.StatsEntry>();

	public UnitStats(final String... headers) {
		this.headers = headers;
	}

	public UnitStats(final Parcel source) {
		readFromParcel(source);
	}

	public UnitStats appendEntry(final int id, final String name, final String... values) {
		entries.add(new StatsEntry(id, name, values));
		return this;
	}

	public UnitStats appendEntry(final String name, final String... values) {
		entries.add(new StatsEntry(name, values));
		return this;
	}

	public UnitStats appendEntry(final StatsEntry entry) {
		entries.add(entry);
		return this;
	}

	public String[] getHeaders() {
		return headers;
	}

	public List<StatsEntry> getEntries() {
		return entries;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);

		dest.writeInt(headers.length);
		dest.writeStringArray(headers);

		writeSubEntries(entries, dest, flags);
	}

	private static void writeSubEntries(final List<StatsEntry> entries, final Parcel dest, final int flags) {
		dest.writeInt(entries.size());
		for (final StatsEntry entry : entries) {
			dest.writeInt(entry.getId());
			dest.writeString(entry.getName());
			dest.writeInt(entry.getValues().length);
			dest.writeStringArray(entry.getValues());
			writeSubEntries(entry.getSecondaries(), dest, flags);

			dest.writeInt(entry.gearReferences.size());
			for (final Integer i : entry.gearReferences) {
				dest.writeInt(i);
			}
		}
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);

		headers = new String[source.readInt()];
		source.readStringArray(headers);

		entries.clear();
		readSubEntries(entries, source);
	}

	private static void readSubEntries(final List<StatsEntry> result, final Parcel source) {
		final int entryCount = source.readInt();
		for (int i = 0; i < entryCount; i++) {
			final int id = source.readInt();
			final String name = source.readString();
			final String[] values = new String[source.readInt()];
			source.readStringArray(values);
			final StatsEntry entry = new StatsEntry(id, name, values);
			result.add(entry);
			readSubEntries(entry.getSecondaries(), source);
			entry.gearReferences.clear();
			final int refCount = source.readInt();
			for (int j = 0; j < refCount; j++) {
				entry.gearReferences.add(source.readInt());
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public StatsEntry find(final int id) {
		for (final StatsEntry entry : entries) {
			if (entry.getId() == id) {
				return entry;
			}
		}
		return null;
	}

	public UnitStats withGear(final int... ids) {
		if (entries.isEmpty()) {
			return this;
		}
		final StatsEntry stats = entries.get(entries.size() - 1);
		for (final Integer id : ids) {
			stats.getGearReferences().add(id);
		}
		return this;
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < entries.size(); i++) {
			result += entries.get(i).getName();
			if (i != entries.size() - 1) {
				result += ", ";
			}
		}
		return "[" + result + "]";
	}
}
