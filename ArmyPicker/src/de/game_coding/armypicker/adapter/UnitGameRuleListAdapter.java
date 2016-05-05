package de.game_coding.armypicker.adapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import de.game_coding.armypicker.model.GameRule;
import de.game_coding.armypicker.viewgroups.GameRuleListItem;
import de.game_coding.armypicker.viewgroups.GameRuleListItem_;

public class UnitGameRuleListAdapter extends BaseAdapter<GameRule, GameRuleListItem> {

	public UnitGameRuleListAdapter(final Context context, final List<GameRule> rules) {
		super(context, rules);
	}

	public UnitGameRuleListAdapter(final Context context, final Collection<GameRule> rules) {
		super(context, Arrays.asList(rules.toArray(new GameRule[rules.size()])));
	}

	@Override
	protected GameRuleListItem buildNewView() {
		return GameRuleListItem_.build(getContext());
	}

	@Override
	protected void fillView(final GameRuleListItem view, final GameRule item, final int position,
		final ViewGroup parent) {
		view.bind(item);
	}
}
