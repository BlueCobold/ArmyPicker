package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.model.CharacterOption;

@EViewGroup(R.layout.item_character_option_list)
public class CharacterOptionItem extends RelativeLayout {

	private CharacterOption option;

	@ViewById(R.id.character_option_base)
	protected TextView baseOption;

	@ViewById(R.id.character_option_sub)
	protected TextView subOption;

	@ViewById(R.id.character_option_subsub)
	protected TextView subsubOption;

	private DeleteHandler<CharacterOption> deleteHandler;

	public CharacterOptionItem(final Context context) {
		super(context);
	}

	@Click(R.id.character_option_delete)
	protected void onDeleteItem() {
		if (deleteHandler != null) {
			deleteHandler.onDelete(option);
		}
	}

	public void bind(final CharacterOption option) {
		this.option = option;
		refresh();
	}

	public void refresh() {
		CharacterOption opt = option;

		baseOption.setText(opt.getName());

		if (opt.getSubOptions().size() == 0) {
			subOption.setText("");
			subsubOption.setText("");
			return;
		}
		opt = opt.getSubOptions().get(0);
		subOption.setText(opt.getName());

		if (opt.getSubOptions().size() == 0) {
			subsubOption.setText("");
			return;
		}
		opt = opt.getSubOptions().get(0);
		subsubOption.setText(opt.getName());
	}

	public void setOnDeleteHandler(final DeleteHandler<CharacterOption> handler) {
		deleteHandler = handler;
	}
}
