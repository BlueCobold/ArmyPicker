package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import de.game_coding.armypicker.model.UnitOptionGroup.GroupType;

public class OptionRule extends Model implements IRule {
	private enum ActionType {
		CHANGE_GROUP_TYPE, ENABLE_GROUP, ENABLE_OPTION,
	}

	private enum ConditionType {
		ON_SELECTED, ON_UNSELECTED
	}

	private int targetId;
	private GroupType targetType = GroupType.ONE_PER_MODEL;
	private ConditionType conditionType = ConditionType.ON_SELECTED;
	private ActionType actionType = ActionType.CHANGE_GROUP_TYPE;
	private List<UnitOptionGroup> groups = new ArrayList<UnitOptionGroup>();
	public int sourceId;

	public OptionRule() {
	}

	public OptionRule(final Parcel source) {
		readFromParcel(source);
	}

	@Override
	public void check() {
		final UnitOptionGroup owner = getOwnerGroup();
		for (final UnitOptionGroup group : groups) {
			if (actionType == ActionType.CHANGE_GROUP_TYPE) {
				if (targetId != group.getId()) {
					continue;
				}
				final boolean oneSelected = atLeastOneSelected(owner);
				if (conditionType == ConditionType.ON_SELECTED && oneSelected
					|| conditionType == ConditionType.ON_UNSELECTED && !oneSelected) {
					group.setType(targetType);
					break;
				}
			} else if (actionType == ActionType.ENABLE_GROUP) {
				if (targetId != group.getId()) {
					continue;
				}
				boolean enable = false;
				if (conditionType == ConditionType.ON_SELECTED) {
					enable = atLeastOneSelected(owner);
				} else if (conditionType == ConditionType.ON_UNSELECTED) {
					enable = !atLeastOneSelected(owner);
				}
				group.setEnabled(enable);
				break;
			} else if (actionType == ActionType.ENABLE_OPTION) {
				final UnitOption target = findOptionById(targetId);
				final UnitOption source = findOptionById(sourceId);
				if (target != null) {
					target.setEnabled(source != null && source.getAmountSelected() > 0);
					if (!target.isEnabled()) {
						target.setAmountSelected(0);
					}
				}
			}
		}
		return;
	}

	private UnitOption findOptionById(final int id) {
		for (final UnitOptionGroup group : groups) {
			for (final UnitOption option : group.getOptions()) {
				if (option.getId() == id) {
					return option;
				}
			}
		}
		return null;
	}

	private UnitOptionGroup getOwnerGroup() {
		for (final UnitOptionGroup group : groups) {
			if (group.getRules().contains(this)) {
				return group;
			}
		}
		return null;
	}

	private boolean atLeastOneSelected(final UnitOptionGroup group) {
		if (group == null) {
			return false;
		}
		for (final UnitOption option : group.getOptions()) {
			if (option.getAmountSelected() > 0) {
				return true;
			}
		}
		return false;
	}

	public void setTargets(final List<UnitOptionGroup> groups) {
		this.groups = groups;
	}

	public ChangeGroup changeTypeOfGroup(final int groupId) {
		targetId = groupId;
		actionType = ActionType.CHANGE_GROUP_TYPE;
		return new ChangeGroup();
	}

	public GroupEnabler enableGroup(final int groupId) {
		targetId = groupId;
		actionType = ActionType.ENABLE_GROUP;
		return new GroupEnabler();
	}

	public OptionEnabler enableOption(final int optionId) {
		targetId = optionId;
		actionType = ActionType.ENABLE_OPTION;
		return new OptionEnabler();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(targetId);
		dest.writeInt(sourceId);
		dest.writeInt(targetType.ordinal());
		dest.writeInt(conditionType.ordinal());
		dest.writeInt(actionType.ordinal());
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		targetId = source.readInt();
		sourceId = source.readInt();
		targetType = GroupType.values()[source.readInt()];
		conditionType = ConditionType.values()[source.readInt()];
		actionType = ActionType.values()[source.readInt()];
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public static final Parcelable.Creator<OptionRule> CREATOR = new Creator<OptionRule>() {

		@Override
		public OptionRule[] newArray(final int size) {
			return new OptionRule[size];
		}

		@Override
		public OptionRule createFromParcel(final Parcel source) {
			return new OptionRule(source);
		}
	};

	public class SelectionChanged {

		public OptionRule whenHasSelectedOptions() {
			conditionType = ConditionType.ON_SELECTED;
			return OptionRule.this;
		}

		public OptionRule whenHasNoSelectedOptions() {
			conditionType = ConditionType.ON_UNSELECTED;
			return OptionRule.this;
		}
	}

	public class ChangeGroup {

		public SelectionChanged to(final GroupType type) {
			targetType = type;
			return new SelectionChanged();
		}
	}

	public class GroupEnabler {
		public OptionRule basedOnGroup() {
			conditionType = ConditionType.ON_SELECTED;
			return OptionRule.this;
		}
	}

	public class OptionEnabler {
		public OptionRule basedOnOption(final int sourceId) {
			OptionRule.this.sourceId = sourceId;
			conditionType = ConditionType.ON_SELECTED;
			return OptionRule.this;
		}
	}
}
