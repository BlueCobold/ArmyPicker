package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import de.game_coding.armypicker.model.creators.BattalionCreator;

public class Battalion extends Model {

	public static final Creator<Battalion> CREATOR = new BattalionCreator();

	private String typeName;

	private List<BattalionRequirement> requirements = new ArrayList<BattalionRequirement>();

	public Battalion(final String typeName) {
		this.typeName = typeName;
	}

	public Battalion(final Parcel source) {
		readFromParcel(source);
	}

	public Battalion withRequirement(final BattalionRequirement requirement) {
		requirements.add(requirement);
		return this;
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		typeName = source.readString();
		requirements = readList(source, BattalionRequirement.CREATOR);
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(typeName);
		writeList(dest, requirements);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public String getTypeName() {
		return typeName;
	}
}
