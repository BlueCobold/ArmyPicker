package de.game_coding.armypicker.viewgroups;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Army;

@EViewGroup(R.layout.item_file_list)
public class FileListItem extends RelativeLayout {

	@ViewById(R.id.file_name)
	TextView title;

	private Army item;

	private ItemClickedListener<Army> deleteHandler;

	public FileListItem(final Context context) {
		super(context);
	}

	public void bind(final Army item) {
		title.setText(item.getName() + " (" + item.getTemplateVersion() + ")");
		this.item = item;
	}

	@Click(R.id.file_delete)
	protected void onDeleteClicked() {
		if (deleteHandler != null) {
			deleteHandler.onItemClicked(item);
		}
	}

	public void setDeleteHandler(final ItemClickedListener<Army> deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}
