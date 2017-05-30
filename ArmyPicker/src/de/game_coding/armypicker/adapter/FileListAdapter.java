package de.game_coding.armypicker.adapter;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.viewgroups.FileListItem;
import de.game_coding.armypicker.viewgroups.FileListItem_;

public class FileListAdapter extends BaseAdapter<Army, FileListItem> {

	private ItemClickedListener<Army> deleteHandler;

	public FileListAdapter(final Context context, final List<Army> objects) {
		super(context, objects);
	}

	@Override
	protected FileListItem buildNewView() {
		return FileListItem_.build(getContext());
	}

	@Override
	protected void fillView(final FileListItem view, final Army item, final int position, final ViewGroup parent) {
		view.bind(item);
		view.setDeleteHandler(deleteHandler);
	}

	public void bindDeleteHandler(final ItemClickedListener<Army> handler) {
		deleteHandler = handler;
	}
}
