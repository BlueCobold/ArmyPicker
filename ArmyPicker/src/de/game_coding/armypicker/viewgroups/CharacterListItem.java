package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.adapter.CharacterOptionListAdapter;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Character;
import de.game_coding.armypicker.model.CharacterOption;

@EViewGroup(R.layout.item_character_list)
public class CharacterListItem extends RelativeLayout {

	@ViewById(R.id.character_item_title)
	protected EditText title;

	@ViewById(R.id.character_item_image)
	protected ImageView image;

	@ViewById(R.id.character_item_properties)
	protected LinearLayout items;

	private Character character;

	private ItemClickedListener<Character> imageRequestHandler;

	private ItemClickedListener<Character> optionRequestHandler;

	public CharacterListItem(final Context context) {
		super(context);
	}

	public void bind(final Character character) {
		this.character = character;
		title.setText(character.getName());
		image.setImageURI(character.getImageUri());
		refreshOptions();
	}

	private void refreshOptions() {
		items.removeAllViews();
		final CharacterOptionListAdapter adapter = new CharacterOptionListAdapter(getContext(), character.getOptions());
		adapter.setOnDeleteHandler(new DeleteHandler<CharacterOption>() {

			@Override
			public void onDelete(final CharacterOption item) {
				character.getOptions().remove(item);
				refreshOptions();
			}
		});
		for (int i = 0; i < adapter.getCount(); i++) {
			items.addView(adapter.getView(i, null, this));
		}
	}

	public void setOnImageRequestListener(final ItemClickedListener<Character> imageRequestHandler) {
		this.imageRequestHandler = imageRequestHandler;
	}

	public void setOnOptionRequestListener(final ItemClickedListener<Character> optionRequestHandler) {
		this.optionRequestHandler = optionRequestHandler;
	}

	@Click(R.id.character_item_add_property)
	protected void onAddProperty() {
		if (optionRequestHandler != null) {
			optionRequestHandler.onItemClicked(character);
		}
	}

	@Click(R.id.character_item_image)
	protected void onChangeImage() {
		if (imageRequestHandler != null) {
			imageRequestHandler.onItemClicked(character);
		}
	}
}
