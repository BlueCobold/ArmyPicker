package de.game_coding.armypicker.adapter;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.CompletionHandler;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.listener.ProgressClickListener;
import de.game_coding.armypicker.model.Battalion;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitRequirement;
import de.game_coding.armypicker.viewgroups.BattalionListItem;
import de.game_coding.armypicker.viewgroups.BattalionListItem_;
import de.game_coding.armypicker.viewmodel.BattalionRequirementDetails;

public class BattalionListAdapter extends BaseAdapter<Battalion, BattalionListItem> {

	private final boolean deletable;

	private DeleteHandler<Battalion> deleteHandler;

	private ProgressClickListener<BattalionRequirement> addSubHandler;

	private IValueChangedNotifier notifier;

	private ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>> addUnitHandler;

	private ItemClickedListener<Collection<Unit>> editUnitHandler;

	private final BattalionRequirementDetails details;

	public BattalionListAdapter(final Context context, final List<Battalion> objects, final boolean deletable,
		final BattalionRequirementDetails details) {
		super(context, objects);
		this.deletable = deletable;
		this.details = details;
	}

	public BattalionListAdapter(final Context context, final Battalion[] objects, final boolean deletable,
		final BattalionRequirementDetails details) {
		super(context, objects);
		this.deletable = deletable;
		this.details = details;
	}

	@Override
	protected BattalionListItem buildNewView() {
		return BattalionListItem_.build(getContext());
	}

	@Override
	protected void fillView(final BattalionListItem view, final Battalion item, final int position,
		final ViewGroup parent) {
		view.setDeleteHandler(deleteHandler);
		view.setAddSubHandler(new ItemClickedListener<BattalionRequirement>() {
			@Override
			public void onItemClicked(final BattalionRequirement subItem) {
				addSubHandler.onItemClicked(subItem, new CompletionHandler() {
					@Override
					public void onCompleted() {
						view.bind(item, deletable, details);
					}
				});
			}
		});
		view.setDeleteSubHandler(new DeleteHandler<BattalionRequirement>() {
			@Override
			public void onDelete(final BattalionRequirement subItem) {
				if (delete(item.getRequirement(), subItem) && notifier != null) {
					notifier.onValueChanged();
				}
				view.bind(item, deletable, details);
			}
		});
		view.setAddUnitHandler(addUnitHandler);
		view.setChangeNotifier(notifier);
		view.setEditUnitHandler(editUnitHandler);
		view.bind(item, deletable, details);
	}

	private boolean delete(final BattalionRequirement parent, final BattalionRequirement subItem) {
		if (parent.getAssignedSubBattalions().remove(subItem)) {
			return true;
		}
		for (final BattalionRequirement sub : parent.getAssignedSubBattalions()) {
			if (delete(sub, subItem)) {
				return true;
			}
		}
		return false;
	}

	public void setDeleteHandler(final DeleteHandler<Battalion> handler) {
		deleteHandler = handler;
	}

	public void setAddSubHandler(final ProgressClickListener<BattalionRequirement> handler) {
		addSubHandler = handler;
	}

	public void setChangeNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}

	public void setAddUnitHandler(
		final ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>> itemClickedListener) {
		addUnitHandler = itemClickedListener;
	}

	public void setEditUnitHandler(final ItemClickedListener<Collection<Unit>> handler) {
		this.editUnitHandler = handler;
	}
}
