package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;

public class UnitStats extends Model {

	public static UnitStatsCreator CREATOR = new UnitStatsCreator();

	public class UnitEntry {
		private final String name;
		private final String[] values;

		public UnitEntry(final String name, final String[] values) {
			this.name = name;
			this.values = values;
		}

		public String getName() {
			return name;
		}

		public String[] getValues() {
			return values;
		}
	}

	private String[] headers;

	private final List<UnitEntry> entries = new ArrayList<UnitStats.UnitEntry>();

	public UnitStats(final String... headers) {
		this.headers = headers;
	}

	public UnitStats(final Parcel source) {
		readFromParcel(source);
	}

	public UnitStats appendEntry(final String name, final String... values) {
		entries.add(new UnitEntry(name, values));
		return this;
	}

	public String[] getHeaders() {
		return headers;
	}

	public List<UnitEntry> getEntries() {
		return entries;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);

		dest.writeInt(headers.length);
		dest.writeStringArray(headers);

		dest.writeInt(entries.size());
		for (final UnitEntry entry : entries) {
			dest.writeString(entry.getName());
			dest.writeInt(entry.getValues().length);
			dest.writeStringArray(entry.getValues());
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
			final String name = source.readString();
			final String[] values = new String[source.readInt()];
			source.readStringArray(values);
			appendEntry(name, values);
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
