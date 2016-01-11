package de.game_coding.armypicker.viewgroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.CharacterOption;
import de.game_coding.armypicker.model.DataSource;

@EViewGroup(R.layout.character_option_add)
public class CharacterOptionPicker extends RelativeLayout {

	@ViewById(R.id.character_base_option)
	protected Spinner baseOption;

	@ViewById(R.id.character_sub_option)
	protected Spinner subOption;

	@ViewById(R.id.character_subsub_option)
	protected Spinner subsubOption;

	private CharacterOption availableOptions;

	private ItemClickedListener<CharacterOption> onAbortListener;

	private ItemClickedListener<CharacterOption> onAcceptListener;

	private ArrayAdapter<CharacterOption> rootAdapter;

	private ArrayAdapter<CharacterOption> subAdapter;

	private ArrayAdapter<CharacterOption> subsubAdapter;

	public CharacterOptionPicker(final Context context) {
		super(context);
	}

	public CharacterOptionPicker(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public CharacterOptionPicker(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@AfterViews
	protected void init() {
		availableOptions = DataSource.getAvailableOptions(getContext());

		rootAdapter = fillAdapter(availableOptions.getSubOptions(), baseOption);
		subAdapter = fillAdapter(availableOptions.getSubOptions().get(0).getSubOptions(), subOption);
		subsubAdapter = fillAdapter(availableOptions.getSubOptions().get(0).getSubOptions().get(0).getSubOptions(),
			subsubOption);

		final List<CharacterOption> subOptions = new ArrayList<CharacterOption>(availableOptions.getSubOptions().get(0)
			.getSubOptions());
		subAdapter = new ArrayAdapter<CharacterOption>(getContext(), R.layout.spinner_item, subOptions);
		subAdapter.setDropDownViewResource(R.layout.spinner_item);
		subOption.setAdapter(subAdapter);

		final List<CharacterOption> subsubOptions = new ArrayList<CharacterOption>(availableOptions.getSubOptions()
			.get(0).getSubOptions().get(0).getSubOptions());
		subsubAdapter = new ArrayAdapter<CharacterOption>(getContext(), R.layout.spinner_item, subsubOptions);
		subsubAdapter.setDropDownViewResource(R.layout.spinner_item);
		subsubOption.setAdapter(subsubAdapter);
	}

	private ArrayAdapter<CharacterOption> fillAdapter(final Collection<CharacterOption> options, final Spinner target) {
		final List<CharacterOption> rootOptions = new ArrayList<CharacterOption>(options);
		final ArrayAdapter<CharacterOption> adapter = new ArrayAdapter<CharacterOption>(getContext(),
			R.layout.spinner_item, rootOptions);
		adapter.setDropDownViewResource(R.layout.spinner_item);
		target.setAdapter(adapter);
		return adapter;
	}

	@ItemSelect(R.id.character_base_option)
	public void onBaseOptionSelected(final boolean selected, final CharacterOption selectedItem) {
		if (!selected) {
			return;
		}
		subAdapter.clear();
		subAdapter.addAll(rootAdapter.getItem(baseOption.getSelectedItemPosition()).getSubOptions());
		subAdapter.notifyDataSetChanged();
		subOption.setSelection(0);
		onSubOptionSelected(selected, null);
	}

	@ItemSelect(R.id.character_sub_option)
	public void onSubOptionSelected(final boolean selected, final CharacterOption selectedItem) {
		if (!selected) {
			return;
		}
		subsubAdapter.clear();
		subsubAdapter.addAll(subAdapter.getItem(subOption.getSelectedItemPosition()).getSubOptions());
		subsubAdapter.notifyDataSetChanged();
		subsubOption.setSelection(0);
	}

	@Click(R.id.option_select_accept)
	protected void onOptionSelected() {
		if (onAcceptListener == null || baseOption.getSelectedItemPosition() < 0) {
			return;
		}

		CharacterOption option = null;
		CharacterOption selected = (CharacterOption) baseOption.getSelectedItem();
		if (selected != null) {
			option = new CharacterOption();
			option.setName(selected.getName());
		}

		CharacterOption childOption = null;
		selected = (CharacterOption) subOption.getSelectedItem();
		if (option != null && selected != null) {
			childOption = new CharacterOption();
			childOption.setName(selected.getName());
			option.getSubOptions().add(childOption);
		}

		CharacterOption subChildOption = null;
		selected = (CharacterOption) subsubOption.getSelectedItem();
		if (childOption != null && selected != null) {
			subChildOption = new CharacterOption();
			subChildOption.setName(selected.getName());
			childOption.getSubOptions().add(subChildOption);
		}

		onAcceptListener.onItemClicked(option);
	}

	@Click(R.id.option_select_abort)
	protected void onAbortSelection() {
		if (onAbortListener == null) {
			return;
		}
		onAbortListener.onItemClicked(null);
	}

	public void setAbortHandler(final ItemClickedListener<CharacterOption> onAbortListener) {
		this.onAbortListener = onAbortListener;
	}

	public void setAcceptHandler(final ItemClickedListener<CharacterOption> onAcceptListener) {
		this.onAcceptListener = onAcceptListener;
	}
}
