package de.game_coding.armypicker.viewgroups;

import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.game_coding.armypicker.R;
import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.web.Downloader;

@EViewGroup(R.layout.view_download)
public class DownloadView extends RelativeLayout {

	@ViewById(R.id.download_template_name)
	protected TextView templateName;

	@ViewById(R.id.download_progress)
	protected ProgressBar spinner;

	@RestService
	protected Downloader myRestClient;

	private List<Army> armies;

	public DownloadView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
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
			armies.add(army);
		}
	}

	@UiThread
	protected void hideSpinner() {
		spinner.setVisibility(View.GONE);
		((View) getParent()).setVisibility(View.GONE);
	}

	public void setArmies(final List<Army> armies) {
		this.armies = armies;
	}
}
