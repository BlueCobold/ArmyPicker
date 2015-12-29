package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Parcel;
import de.game_coding.armypicker.model.creators.CharacterCreator;

public class Character extends Model {

	public static final Creator<Character> CREATOR = new CharacterCreator();

	private int id;

	private String name;

	private Uri imageUri;

	private List<CharacterOption> options = new ArrayList<CharacterOption>();

	public Character() {
	}

	public Character(final Parcel source) {
		readFromParcel(source);
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		id = source.readInt();
		name = source.readString();
		final String uri = source.readString();
		if (!uri.isEmpty()) {
			imageUri = Uri.parse(uri);
		}
		options = readList(source, CharacterOption.CREATOR);
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(imageUri != null ? imageUri.toString() : "");
		writeList(dest, options);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public Uri getImageUri() {
		return imageUri;
	}

	public void setImageUri(final Uri imageUri) {
		this.imageUri = imageUri;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<CharacterOption> getOptions() {
		return options;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void addOption(final CharacterOption option) {
		options.add(option);
	}

}
