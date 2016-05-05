package de.game_coding.armypicker.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.ViewGroup;
import de.game_coding.armypicker.listener.DeleteHandler;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.BattalionRequirement;
import de.game_coding.armypicker.model.IValueChangedNotifier;
import de.game_coding.armypicker.model.Unit;
import de.game_coding.armypicker.model.UnitRequirement;
import de.game_coding.armypicker.viewgroups.BattalionRequirementListItem;
import de.game_coding.armypicker.viewgroups.BattalionRequirementListItem_;
import de.game_coding.armypicker.viewmodel.BattalionRequirementDetails;

public class BattalionRequirementListAdapter extends BaseAdapter<BattalionRequirement, BattalionRequirementListItem> {

	private final boolean readOnly;

	private ItemClickedListener<BattalionRequirement> addHandler;

	private DeleteHandler<BattalionRequirement> deleteHandler;

	private ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>> addUnitHandler;

	private ItemClickedListener<Collection<Unit>> editUnitHandler;

	private IValueChangedNotifier notifier;

	private final BattalionRequirementDetails details;

	public BattalionRequirementListAdapter(final Context context, final List<BattalionRequirement> objects,
		final boolean readOnly, final BattalionRequirementDetails details) {
		super(context, readOnly ? filterMeta(objects, !readOnly) : getFlat(objects));
		this.readOnly = readOnly;
		this.details = details;
	}

	public BattalionRequirementListAdapter(final Context context, final BattalionRequirement[] objects,
		final boolean readOnly, final boolean useAssigned, final BattalionRequirementDetails details) {
		super(context, readOnly ? filterMeta(Arrays.asList(objects), !readOnly) : getFlat(Arrays.asList(objects)));
		this.readOnly = readOnly;
		this.details = details;
	}

	public BattalionRequirementListAdapter(final Context context, final Collection<BattalionRequirement> objects,
		final boolean readOnly, final BattalionRequirementDetails details) {
		super(context, readOnly ? filterMeta(objects, !readOnly) : getFlat(objects));
		this.readOnly = readOnly;
		this.details = details;
	}

	private static List<BattalionRequirement> getFlat(final Collection<BattalionRequirement> objects) {
		return getFlat(objects, 0);
	}

	private static List<BattalionRequirement> getFlat(final Collection<BattalionRequirement> objects, final int depth) {
		final List<BattalionRequirement> flat = new ArrayList<BattalionRequirement>();
		for (final BattalionRequirement req : filterMeta(objects, true)) {
			req.setTag(new Integer(depth));
			flat.add(req);
			flat.addAll(getFlat(req.getAssignedSubBattalions(), depth + 1));
		}
		return flat;
	}

	private static List<BattalionRequirement> filterMeta(final Collection<BattalionRequirement> objects,
		final boolean useAssigned) {
		final List<BattalionRequirement> nonMetas = new ArrayList<BattalionRequirement>();
		for (final BattalionRequirement req : objects) {
			if (!req.isMeta()) {
				nonMetas.add(req);
			} else {
				if (useAssigned) {
					final List<BattalionRequirement> subs = filterMeta(req.getAssignedSubBattalions(), useAssigned);
					nonMetas.addAll(subs);
				} else {
					final Collection<BattalionRequirement> subs = filterMeta(req.getRequiredSubBattalions(),
						useAssigned);
					nonMetas.addAll(subs);
				}
			}
		}
		return nonMetas;
	}

	@Override
	protected BattalionRequirementListItem buildNewView() {
		return BattalionRequirementListItem_.build(getContext());
	}

	@Override
	protected void fillView(final BattalionRequirementListItem view, final BattalionRequirement item,
		final int position, final ViewGroup parent) {
		view.setAddHandler(addHandler);
		view.setDeleteHandler(deleteHandler);
		view.bind(item, readOnly, details);
		view.setAddUnitHandler(addUnitHandler);
		view.setEditUnitHandler(editUnitHandler);
		view.setChangeNotifier(notifier);
		if (!readOnly && item.getTag() instanceof Integer) {
			view.setBackgroundColor(Math.min(255, 22 * ((Integer) item.getTag()).intValue()) << 24);
		}
	}

	public void setAddHandler(final ItemClickedListener<BattalionRequirement> handler) {
		addHandler = handler;
	}

	public void setDeleteHandler(final DeleteHandler<BattalionRequirement> handler) {
		deleteHandler = handler;
	}

	public void setAddUnitHandler(final ItemClickedListener<Pair<UnitRequirement, BattalionRequirement>> handler) {
		addUnitHandler = handler;
	}

	public void setChangeNotifier(final IValueChangedNotifier notifier) {
		this.notifier = notifier;
	}

	public void setEditUnitHandler(final ItemClickedListener<Collection<Unit>> handler) {
		this.editUnitHandler = handler;
	}
}
