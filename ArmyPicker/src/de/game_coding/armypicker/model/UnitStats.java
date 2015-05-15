package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;

public class UnitStats extends Model {

	public static UnitStatsCreator CREATOR = new UnitStatsCreator();

	public static class StatsEntry {
		private final String name;
		private final String[] values;
		private final List<StatsEntry> secondaries = new ArrayList<StatsEntry>();

		public StatsEntry(final String name, final String... values) {
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
	}

	private String[] headers;

	private final List<StatsEntry> entries = new ArrayList<UnitStats.StatsEntry>();

	public UnitStats(final String... headers) {
		this.headers = headers;
	}

	public UnitStats(final Parcel source) {
		readFromParcel(source);
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

		dest.writeInt(entries.size());
		for (final StatsEntry entry : entries) {
			dest.writeString(entry.getName());
			dest.writeInt(entry.getValues().length);
			dest.writeStringArray(entry.getValues());
			dest.writeInt(entry.getSecondaries().size());
			for (final StatsEntry subentry : entry.getSecondaries()) {
				dest.writeString(subentry.getName());
				dest.writeInt(subentry.getValues().length);
				dest.writeStringArray(subentry.getValues());
			}
		}
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);

		headers = new String[source.readInt()];
		source.readStringArray(headers);

		entries.clear();
		final int entryCount = source.readInt();
		for (int i = 0; i < entryCount; i++) {
			String name = source.readString();
			final String[] values = new String[source.readInt()];
			source.readStringArray(values);
			final StatsEntry entry = new StatsEntry(name, values);
			appendEntry(entry);
			final int subentryCount = source.readInt();
			for (int j = 0; j < subentryCount; j++) {
				name = source.readString();
				final String[] subvalues = new String[source.readInt()];
				source.readStringArray(subvalues);
				entry.addSecondary(name, subvalues);
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
}
