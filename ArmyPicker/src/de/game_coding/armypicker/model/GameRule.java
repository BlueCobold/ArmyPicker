package de.game_coding.armypicker.model;

import android.os.Parcel;
import android.os.Parcelable;
import de.game_coding.armypicker.model.creators.GameRuleCreator;

public class GameRule extends Model {

	public static final Parcelable.Creator<GameRule> CREATOR = new GameRuleCreator();

	private String title;

	private String description;

	public GameRule(Parcel source) {
		readFromParcel(source);
	}

	public GameRule(String title, String description) {
		this.title = title;
		this.description = description;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(title);
		dest.writeString(description);
	}

	@Override
	protected void readFromParcel(Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		title = source.readString();
		description = source.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 1;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GameRule other = (GameRule) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}

}
