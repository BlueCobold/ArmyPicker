package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.view.View;
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
import de.game_coding.armypicker.viewmodel.CharacterViewModel;

@EViewGroup(R.layout.item_character_list)
public class CharacterListItem extends RelativeLayout {

	@ViewById(R.id.character_item_title)
	protected EditText title;

	@ViewById(R.id.character_item_image)
	protected ImageView image;

	@ViewById(R.id.character_item_properties)
	protected LinearLayout items;

	@ViewById(R.id.character_item_remove)
	protected View deleteButton;

	@ViewById(R.id.character_item_add_property)
	protected View addButton;

	private Character character;

	private ItemClickedListener<CharacterViewModel> imageRequestHandler;

	private ItemClickedListener<CharacterViewModel> optionRequestHandler;

	private DeleteHandler<CharacterViewModel> onDeleteHandler;

	private CharacterViewModel viewModel;

	public CharacterListItem(final Context context) {
		super(context);
	}

	public void bind(final CharacterViewModel viewModel) {
		this.character = viewModel.getCharacter();
		this.viewModel = viewModel;
		if (title.getText() == null || !title.getText().equals(character.getName())) {
			title.setText(character.getName());
		}
		image.setImageURI(character.getImageUri());
		image.setVisibility(!viewModel.isShowSummaries() ? View.VISIBLE : View.GONE);
		deleteButton.setVisibility(viewModel.canBeDeleted() && !viewModel.isShowSummaries() ? View.VISIBLE : View.GONE);
		addButton.setVisibility(!viewModel.isShowSummaries() ? View.VISIBLE : View.GONE);
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
		adapter.fillWithItems(items, this);
	}

	public void setOnImageRequestListener(final ItemClickedListener<CharacterViewModel> imageRequestHandler) {
		this.imageRequestHandler = imageRequestHandler;
	}

	public void setOnOptionRequestListener(final ItemClickedListener<CharacterViewModel> optionRequestHandler) {
		this.optionRequestHandler = optionRequestHandler;
	}

	public void setOnDeleteHandler(final DeleteHandler<CharacterViewModel> handler) {
		onDeleteHandler = handler;
	}

	@Click(R.id.character_item_add_property)
	protected void onAddProperty() {
		if (optionRequestHandler != null) {
			optionRequestHandler.onItemClicked(viewModel);
		}
	}

	@Click(R.id.character_item_image)
	protected void onChangeImage() {
		if (imageRequestHandler != null) {
			imageRequestHandler.onItemClicked(viewModel);
		}
	}

	@Click(R.id.character_item_remove)
	protected void onCharacterDeleted() {
		if (onDeleteHandler != null) {
			onDeleteHandler.onDelete(viewModel);
		}
	}
}
