package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import de.game_coding.armypicker.model.UnitOptionGroup.GroupType;

public class OptionRule extends Model implements IRule {
	private enum ActionType {
		CHANGE_GROUP_TYPE, //
		ENABLE_GROUP, //
		ENABLE_OPTION, //
		DISABLE_OPTION, //
		REDUCE_GROUP_AMOUNT, //
		REDUCE_GROUP_AMOUNT_BY, //
		REDUCE_GROUP_AMOUNT_BY_OPTION, //
		ADD_WARNING, //
		LIMIT_OPTION_TO, //
		LIMIT_OPTIONS_TO_MEMBERS, //
		REDUCE_GROUP_AMOUNT_BY_GROUPS, //
	}

	private enum ConditionType {
		ALWAYS, //
		NEVER, //
		ON_OWNER_SELECTED, //
		ON_OPTION_SELECTED, //
		/**
		 * True if at least one of the given options is not selected
		 */
		ON_OPTION_NOT_SELECTED, //
		/**
		 * True if none of the given options is selected
		 */
		ON_NO_OPTION_SELECTED, //
		ON_UNSELECTED, //
		GROUP_SUMS_LESS_THAN, //
		GROUP_SUMS_MORE_THAN, //
		LIMIT_LESS_THAN, //
	}

	private int targetId;
	public int[] sourceIds = new int[0];
	private int[] targetIds = new int[0];
	public int value;
	public int conditionValue;
	private GroupType targetType = GroupType.ONE_PER_MODEL;
	private ConditionType conditionType = ConditionType.ALWAYS;
	private ActionType actionType = ActionType.CHANGE_GROUP_TYPE;
	private List<UnitOptionGroup> groups = new ArrayList<UnitOptionGroup>();
	private String text = "";

	public OptionRule() {
	}

	public OptionRule(final Parcel source) {
		readFromParcel(source);
	}

	@Override
	public void check() {
		final UnitOptionGroup target = getGroup(targetId);
		switch (actionType) {
			case CHANGE_GROUP_TYPE:
				if (checkCondition() && target != null) {
					target.setType(targetType);
				}
				break;
			case ENABLE_GROUP:
				if (target != null) {
					target.setEnabled(checkCondition());
				}
				break;
			case ENABLE_OPTION:
				handleEnableOption(true);
				break;
			case DISABLE_OPTION:
				handleEnableOption(false);
				break;
			case REDUCE_GROUP_AMOUNT:
				handleLimitOptionAmountBy(Integer.MIN_VALUE);
				break;
			case REDUCE_GROUP_AMOUNT_BY:
				handleLimitOptionAmountBy(value);
				break;
			case REDUCE_GROUP_AMOUNT_BY_OPTION:
				handleLimitOptionAmountBy(new Enabler().buildSourceOptionSums());
				break;
			case REDUCE_GROUP_AMOUNT_BY_GROUPS:
				handleLimitOptionAmountBy(new Enabler().buildSourceGroupsSums());
				break;
			case ADD_WARNING:
				handleAddWarning();
				break;
			case LIMIT_OPTION_TO:
				handleOptionLimit();
				break;
			case LIMIT_OPTIONS_TO_MEMBERS:
				handleOptionLimitsByMembers();
				break;
			default:
				break;
		}
	}

	private void handleOptionLimitsByMembers() {
		final UnitOptionGroup owner = getOwnerGroup();
		final int members = owner.getLimit() - value;
		int total = 0;
		for (final int id : targetIds) {
			final UnitOption option = getOption(id);
			final int delta = members - total - option.getAmountSelected();
			if (delta < 0) {
				option.setAmountSelected(option.getAmountSelected() + delta);
			}
			total += option.getAmountSelected();
		}
	}

	private void handleOptionLimit() {
		for (final int id : targetIds) {
			final UnitOption option = getOption(id);
			option.setAmountSelected(Math.max(option.getAmountSelected(), value));
		}
	}

	private boolean checkCondition() {
		final UnitOptionGroup owner = getOwnerGroup();
		switch (conditionType) {
			case ALWAYS:
				return true;

			case NEVER:
				return false;

			case LIMIT_LESS_THAN:
				return owner != null && owner.getLimit() < conditionValue;

			case ON_OWNER_SELECTED:
				return owner != null && owner.getAmountSelected() > 0;

			case ON_OPTION_SELECTED:
				for (final int id : sourceIds) {
					final UnitOption option = getOption(id);
					if (option != null && option.getAmountSelected() > 0) {
						return true;
					}
				}
				return false;
			case ON_OPTION_NOT_SELECTED:
				for (final int id : sourceIds) {
					final UnitOption option = getOption(id);
					if (option != null && option.getAmountSelected() == 0) {
						return true;
					}
				}
				return false;
			case ON_NO_OPTION_SELECTED:
				for (final int id : sourceIds) {
					final UnitOption option = getOption(id);
					if (option != null && option.getAmountSelected() != 0) {
						return false;
					}
				}
				return true;

			case ON_UNSELECTED:
				return owner != null && owner.getAmountSelected() == 0;

			case GROUP_SUMS_LESS_THAN:
				return new Enabler().buildSourceSelectionSums() < conditionValue;

			case GROUP_SUMS_MORE_THAN:
				return new Enabler().buildSourceSelectionSums() > conditionValue;

			default:
				break;
		}
		return false;
	}

	private UnitOption getOption(final int id) {
		for (final UnitOptionGroup group : groups) {
			for (final UnitOption option : group.getOptions()) {
				if (option.getId() == id) {
					return option;
				}
			}
		}
		return null;
	}

	private static int selected(final UnitOptionGroup source) {
		if (source != null) {
			return source.getAmountSelected();
		}
		return 0;
	}

	private static int selected(final UnitOption source) {
		if (source != null) {
			return source.getAmountSelected();
		}
		return 0;
	}

	private void handleAddWarning() {
		setWarning(getOwnerGroup().getId());
	}

	private void setWarning(final int groupId) {
		final UnitOptionGroup target = getGroup(groupId);
		if (target != null) {
			if (checkCondition() && target.isEnabled()) {
				target.setWarning(text);
			} else {
				target.clearWarning(text);
			}
		}
	}

	private void handleLimitOptionAmountBy(final int amount) {
		int diff = amount;
		if (diff == Integer.MIN_VALUE) {
			diff = getOwnerGroup().getAmountSelected();
		}
		for (final int targetId : targetIds) {
			final UnitOptionGroup target = getGroup(targetId);
			if (target != null) {
				if (checkCondition()) {
					target.reduceBy(diff);
					target.setOptionNumberPerGroup(target.getInitalOptionNumberPerGroup() - diff);
				} else {
					target.reduceBy(0);
					target.setOptionNumberPerGroup(target.getInitalOptionNumberPerGroup());
				}
			}
		}
	}

	private void handleEnableOption(final boolean enable) {
		for (final int id : targetIds) {
			final UnitOption target = findOptionById(id);
			if (target != null) {
				target.setEnabled(checkCondition() ? enable : !enable);
				if (!target.isEnabled()) {
					target.setAmountSelected(0);
				}
			}
		}
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

	private UnitOptionGroup getGroup(final int id) {
		for (final UnitOptionGroup group : groups) {
			if (group.getId() == id) {
				return group;
			}
		}
		return null;
	}

	public void setTargets(final List<UnitOptionGroup> groups) {
		this.groups = groups;
	}

	public ChangeGroup changeTypeOfGroup(final int groupId) {
		targetId = groupId;
		actionType = ActionType.CHANGE_GROUP_TYPE;
		return new ChangeGroup();
	}

	public Enabler enableGroup(final int groupId) {
		targetId = groupId;
		actionType = ActionType.ENABLE_GROUP;
		return new Enabler();
	}

	public Enabler enableOption(final int... optionIds) {
		targetIds = optionIds;
		actionType = ActionType.ENABLE_OPTION;
		return new Enabler();
	}

	public Enabler disableOption(final int... optionIds) {
		targetIds = optionIds;
		actionType = ActionType.DISABLE_OPTION;
		return new Enabler();
	}

	public GroupAmountReducer reduceAmountOfGroup(final int... groupIds) {
		targetIds = groupIds;
		return new GroupAmountReducer();
	}

	public OptionAmountReducer limitAmountOfOption(final int... optionIds) {
		targetIds = optionIds;
		return new OptionAmountReducer();
	}

	public Enabler addWarning(final String text) {
		actionType = ActionType.ADD_WARNING;
		this.text = text;
		return new Enabler();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(targetId);
		dest.writeInt(sourceIds.length);
		dest.writeIntArray(sourceIds);
		dest.writeInt(targetIds.length);
		dest.writeIntArray(targetIds);
		dest.writeInt(value);
		dest.writeInt(conditionValue);
		dest.writeString(text);
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
		sourceIds = new int[source.readInt()];
		source.readIntArray(sourceIds);
		targetIds = new int[source.readInt()];
		source.readIntArray(targetIds);
		value = source.readInt();
		if (getFileVersion() >= 1) {
			conditionValue = source.readInt();
		}
		text = source.readString();
		targetType = GroupType.values()[source.readInt()];
		conditionType = ConditionType.values()[source.readInt()];
		actionType = ActionType.values()[source.readInt()];
	}

	@Override
	protected int getFeatureVersion() {
		return 1;
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
			conditionType = ConditionType.ON_OWNER_SELECTED;
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

	public class Enabler {
		public OptionRule getRule() {
			return OptionRule.this;
		}

		public OptionRule basedOnGroup() {
			conditionType = ConditionType.ON_OWNER_SELECTED;
			return OptionRule.this;
		}

		/**
		 * Triggers to true if any of the options is selected
		 *
		 * @param optionIds
		 * @return
		 */
		public OptionRule basedOnOption(final int... optionIds) {
			OptionRule.this.sourceIds = optionIds;
			conditionType = ConditionType.ON_OPTION_SELECTED;
			return OptionRule.this;
		}

		public OptionRule basedOnNotOption(final int... optionIds) {
			OptionRule.this.sourceIds = optionIds;
			conditionType = ConditionType.ON_OPTION_NOT_SELECTED;
			return OptionRule.this;
		}

		public OptionRule basedOnNotAnyOption(final int... optionIds) {
			OptionRule.this.sourceIds = optionIds;
			conditionType = ConditionType.ON_NO_OPTION_SELECTED;
			return OptionRule.this;
		}

		public OptionRule always() {
			conditionType = ConditionType.ALWAYS;
			return OptionRule.this;
		}

		public OptionRule never() {
			conditionType = ConditionType.NEVER;
			return OptionRule.this;
		}

		public OptionRule ifMembersLessThan(final int number) {
			conditionValue = number;
			conditionType = ConditionType.LIMIT_LESS_THAN;
			return OptionRule.this;
		}

		public SumsComparator whenSumsOfGroups(final int... groupId) {
			OptionRule.this.sourceIds = groupId;
			return new SumsComparator();
		}

		public OptionRule minus(final int value) {
			OptionRule.this.value = value;
			return OptionRule.this;
		}

		protected int buildSourceSelectionSums() {
			int sum = 0;
			for (final int sourceId : sourceIds) {
				sum += selected(getGroup(sourceId));
			}
			return sum;
		}

		protected int buildSourceOptionSums() {
			int sum = 0;
			for (final int sourceId : sourceIds) {
				sum += selected(getOption(sourceId));
			}
			return sum;
		}

		protected int buildSourceGroupsSums() {
			int sum = 0;
			for (final int sourceId : sourceIds) {
				final UnitOptionGroup group = getGroup(sourceId);
				for (final UnitOption option : group.getOptions()) {
					sum += option.getAmountSelected();
				}
			}
			return sum;
		}
	}

	public class GroupAmountReducer {
		public Enabler byAmountOfGroup() {
			actionType = ActionType.REDUCE_GROUP_AMOUNT;
			return new Enabler();
		}

		public Enabler by(final int number) {
			OptionRule.this.value = number;
			actionType = ActionType.REDUCE_GROUP_AMOUNT_BY;
			return new Enabler();
		}

		public Enabler byAmountOfOption(final int... optionIds) {
			sourceIds = optionIds;
			actionType = ActionType.REDUCE_GROUP_AMOUNT_BY_OPTION;
			return new Enabler();
		}

		public Enabler byAmountOfGroups(final int... groupIds) {
			sourceIds = groupIds;
			actionType = ActionType.REDUCE_GROUP_AMOUNT_BY_GROUPS;
			return new Enabler();
		}
	}

	public class OptionAmountReducer {

		public Enabler to(final int number) {
			OptionRule.this.value = number;
			actionType = ActionType.LIMIT_OPTION_TO;
			return new Enabler();
		}

		public Enabler byNumberOfMembers() {
			actionType = ActionType.LIMIT_OPTIONS_TO_MEMBERS;
			value = 0;
			return new Enabler();
		}
	}

	public class SumsComparator {
		public OptionRule lessThan(final int value) {
			OptionRule.this.conditionValue = value;
			OptionRule.this.conditionType = ConditionType.GROUP_SUMS_LESS_THAN;
			return OptionRule.this;
		}

		public OptionRule moreThan(final int value) {
			OptionRule.this.conditionValue = value;
			OptionRule.this.conditionType = ConditionType.GROUP_SUMS_MORE_THAN;
			return OptionRule.this;
		}
	}
}
