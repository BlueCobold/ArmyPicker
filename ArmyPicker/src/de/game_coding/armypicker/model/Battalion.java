package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;
import de.game_coding.armypicker.model.creators.BattalionCreator;

public class Battalion extends Model {

	public static final Creator<Battalion> CREATOR = new BattalionCreator();

	private String typeName;

	private BattalionRequirement requirement;

	private String description;

	private List<GameRule> rules = new ArrayList<GameRule>();

	public Battalion(final String typeName, final BattalionRequirement requirement) {
		this.typeName = typeName;
		this.requirement = requirement;
	}

	public Battalion(final Parcel source) {
		readFromParcel(source);
	}

	public BattalionRequirement getRequirement() {
		return requirement;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getDescription() {
		return description;
	}

	public Battalion withDescription(final String description) {
		this.description = description;
		return this;
	}

	public Battalion withRule(final String title, final String description) {
		rules.add(new GameRule(title, description));
		return this;
	}

	public Collection<GameRule> getRules() {
		return rules;
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		typeName = source.readString();
		requirement = new BattalionRequirement(source);
		description = source.readString();
		rules = readList(source, GameRule.CREATOR);
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(typeName);
		requirement.writeToParcel(dest, flags);
		dest.writeString(description != null ? description : "");
		writeList(dest, rules);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public int getUnitCosts() {
		return getUnitCosts(requirement);
	}

	private int getUnitCosts(final BattalionRequirement requirement) {
		int total = 0;
		for (final BattalionRequirement sub : requirement.getAssignedSubBattalions()) {
			total += sub.getUnitCosts();
		}
		return total;
	}
}
