package de.game_coding.armypicker.viewgroups;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.adapter.FileListAdapter;
import de.game_coding.armypicker.listener.ItemClickedListener;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.util.FileUtil;
import de.game_coding.armypicker.web.Downloader;

@EViewGroup(R.layout.view_download)
public class DownloadView extends RelativeLayout {

	@ViewById(R.id.download_template_name)
	protected TextView templateName;

	@ViewById(R.id.download_progress)
	protected ProgressBar spinner;

	@ViewById(R.id.download_file_list)
	protected ListView fileList;

	@RestService
	protected Downloader myRestClient;

	private List<Army> armies;

	private List<Army> downloaded;

	public DownloadView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@AfterViews
	protected void onInit() {
		if (downloaded == null) {
			return;
		}
		final FileListAdapter adapter = new FileListAdapter(getContext(), downloaded);
		adapter.bindDeleteHandler(new ItemClickedListener<Army>() {

			@Override
			public void onItemClicked(final Army item) {
				downloaded.remove(item);
				FileUtil.deleteFile(item.getName() + "_" + item.getTemplateVersion() + "_template.army", getContext());
				final FileListAdapter a = new FileListAdapter(getContext(), downloaded);
				a.bindDeleteHandler(this);
				fileList.setAdapter(a);
			}
		});
		fileList.setAdapter(adapter);
	}

	@Click(R.id.download_btn_ok)
	protected void onDownload() {
		spinner.setVisibility(View.VISIBLE);
		download(templateName.getText().toString());
	}

	@Click(R.id.download_btn_abort)
	protected void onAbort() {
		((View) getParent()).setVisibility(View.GONE);
	}

	@Background
	protected void download(final String templateName) {
		final Army army = myRestClient.getTemplate(templateName);
		hideSpinner();
		if (armies != null && army != null) {
			FileUtil.storeToFile(army, army.getName() + "_" + army.getTemplateVersion() + "_template.army",
				getContext());
			armies.add(army);
			downloaded.add(army);
		}
		updateUi();
	}

	@UiThread
	protected void updateUi() {
		onInit();
	}

	@UiThread
	protected void hideSpinner() {
		spinner.setVisibility(View.INVISIBLE);
		((View) getParent()).setVisibility(View.GONE);
	}

	public void setArmies(final List<Army> armies) {
		this.armies = armies;
	}

	public void setDownloadedTemplates(final List<Army> templates) {
		downloaded = templates;
		onInit();
	}
}
