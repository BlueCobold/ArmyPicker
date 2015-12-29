package de.game_coding.armypicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;
import de.game_coding.armypicker.adapter.CharacterListAdapter;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.model.Character;
import de.game_coding.armypicker.model.CharacterOption;
import de.game_coding.armypicker.util.ImageUtil;
import de.game_coding.armypicker.viewgroups.CharacterOptionPicker;

@EActivity(R.layout.activity_character)
@OptionsMenu(R.menu.character_activity_menu)
public class CharacterActivity extends Activity {

	public static final String EXTRA_ARMY_ID = "CharacterActivity.EXTRA_ARMY_ID";

	private static final int IMAGE_CAPTURE_CODE = 10001;

	private static SparseArray<Army> _armies = new SparseArray<Army>();

	public static void shareArmy(final Army army) {
		_armies.append(army.getId(), army);
	}

	@ViewById(R.id.character_list)
	protected ListView characters;

	@ViewById(R.id.character_option_dialog)
	protected View optionDialog;

	@ViewById(R.id.character_option_picker)
	protected CharacterOptionPicker optionPicker;

	private Army army;

	private File resultFile;

	private CharacterListAdapter adapter;

	private List<Character> characterList;

	protected Character editedCharacter;

	@AfterViews
	protected void init() {
		army = _armies.get(getIntent().getIntExtra(EXTRA_ARMY_ID, -1));
		if (army == null) {
			finish();
			return;
		}

		characterList = new ArrayList<Character>();
		initAdapter();
	}

	private void initAdapter() {
		adapter = new CharacterListAdapter(this, characterList);
		adapter.setOnImageRequestListener(new ItemClickedListener<Character>() {

			@Override
			public void onItemClicked(final Character item) {
				selectImage(item);
			}
		});
		adapter.onOptionRequestListener(new ItemClickedListener<Character>() {
			@Override
			public void onItemClicked(final Character character) {
				editedCharacter = character;
				optionDialog.setVisibility(View.VISIBLE);
				optionPicker.setAbortHandler(new ItemClickedListener<CharacterOption>() {

					@Override
					public void onItemClicked(final CharacterOption option) {
						optionDialog.setVisibility(View.GONE);
						editedCharacter = null;
					}
				});
				optionPicker.setAcceptHandler(new ItemClickedListener<CharacterOption>() {

					@Override
					public void onItemClicked(final CharacterOption option) {
						if (editedCharacter == null) {
							return;
						}
						optionDialog.setVisibility(View.GONE);
						editedCharacter.addOption(option);
						editedCharacter = null;
					}
				});
			}
		});
		characters.setAdapter(adapter);
	}

	@Override
	public void finish() {
		_armies.clear();
		super.finish();
	}

	@OptionsItem(R.id.action_add)
	protected void addNewCharacter() {
		final Character character = new Character();
		adapter.add(character);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == IMAGE_CAPTURE_CODE) {
			final Bundle extras = data != null ? data.getExtras() : null;
			Bitmap bmp = null;

			final Character c = characterList.get(0);

			Uri uri = data != null ? data.getData() : null;
			if (uri != null) {
				loadImage(uri, c);
				return;
			}

			if (bmp == null && extras != null) {
				bmp = (Bitmap) extras.get("data");
			}

			if (bmp == null) {
				uri = resultFile != null ? Uri.fromFile(resultFile) : null;
				if (uri != null && new File(uri.getPath()).exists()) {
					loadImage(uri, c);
					return;
				}
			}
		}
	}

	@UiThread
	protected void finishImageLoading(final Character character) {
		adapter.notifyDataChanged(character);
	}

	@Background
	protected void loadImage(final Uri uri, final Character character) {
		Bitmap img = ImageUtil.decodeUri(this, uri, 100, 100);
		Uri result = null;
		if (img != null) {
			img = ImageUtil.toRect(img);
			result = ImageUtil.saveBitmap(this, "char_" + character.getId() + "_.jpg", img);
		}
		character.setImageUri(result);
		finishImageLoading(character);
	}

	private void selectImage(final Character character) {
		resultFile = null;
		resultFile = createImageFile();
		if (resultFile == null) {
			return;
		}
		if (resultFile.exists()) {
			resultFile.delete();
		}
		final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, resultFile.getAbsolutePath());
		captureIntent.putExtra("CHARACTER_ID", character.getId());
		startActivityForResult(captureIntent, IMAGE_CAPTURE_CODE);
	}

	private File createImageFile() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return null;
		}

		final File extDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
			getString(R.string.app_name));
		if (!extDir.exists()) {
			if (!extDir.mkdirs()) {
				return null;
			}
		}

		return new File(extDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg");
	}
}
