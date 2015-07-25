package com.hello1987.mediapicker.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hello1987.mediapicker.MediaPicker;
import com.hello1987.mediapicker.MediaPickerConstants;
import com.hello1987.mediapicker.R;
import com.hello1987.mediapicker.model.Album;
import com.hello1987.mediapicker.model.Media;
import com.hello1987.mediapicker.ui.fragment.AlbumFragment.OnAlbumSelectedListener;
import com.hello1987.mediapicker.ui.fragment.MediaFragment;
import com.hello1987.mediapicker.ui.fragment.MediaFragment.OnMediaSelectedListener;
import com.hello1987.mediapicker.util.AnimationUtil;

public class MediaPickerActivity extends FragmentActivity implements
		OnAlbumSelectedListener, OnMediaSelectedListener, OnClickListener {

	private ActionBar mActionBar;
	private MediaFragment mMediaFragment;

	private FrameLayout mAlbumLayout;
	private TextView mAlbumTv;
	private TextView mPreviewTv;

	private Album mAlbum = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_picker);

		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(true);

		mMediaFragment = (MediaFragment) getSupportFragmentManager()
				.findFragmentById(R.id.media_fragment);

		mAlbumLayout = (FrameLayout) findViewById(R.id.album_layout);
		mAlbumTv = (TextView) findViewById(R.id.album_tv);
		mPreviewTv = (TextView) findViewById(R.id.preview_tv);

		mAlbumLayout.setOnClickListener(this);
		mAlbumTv.setOnClickListener(this);
		mPreviewTv.setOnClickListener(this);

		if (MediaPickerConstants.SHOW_IMAGE && MediaPickerConstants.SHOW_VIDEO) {
			mActionBar.setTitle("图片和视频");
			mAlbumTv.setText("图片和视频");
			mMediaFragment.updateMediaView();
		} else {
			if (MediaPickerConstants.SHOW_VIDEO) {
				mActionBar.setTitle("视频");
				mAlbumTv.setText("所有视频");
				mMediaFragment.updateMediaView();
			} else {
				mActionBar.setTitle("图片");
				mAlbumTv.setText("所有图片");
				mMediaFragment.updateMediaView();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_media_picker, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem yesItem = menu.findItem(R.id.yes_item);
		updateMenu(yesItem);
		return true;
	}

	private void updateMenu(MenuItem item) {
		List<Media> selectedMedias = mMediaFragment.getSelectedMedias();

		if (selectedMedias.size() == 0) {
			item.setTitle(getColoredText("确定", "#bebebe"));
			item.setEnabled(false);
			return;
		}

		String title = String.format("确定(%1$d/%2$d)", selectedMedias.size(),
				MediaPickerConstants.MAX_MEDIA_LIMIT);
		item.setTitle(getColoredText(title, "#ec5d0f"));
		item.setEnabled(true);
	}

	private Spanned getColoredText(String text, String color) {
		return Html.fromHtml(String.format("<font color='%1$s'>%2$s</font>",
				color, text));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.yes_item:
			setCallback(RESULT_OK);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setCallback(int resultCode) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("selectedMedias",
				mMediaFragment.getSelectedMedias());
		intent.putExtras(bundle);
		setResult(resultCode, intent);
	}

	@Override
	public void onAlbumSelected(Album album) {
		hideAlbumSet();

		if (album.equals(mAlbum))
			return;

		String bucketName = album.getBucketName();
		mAlbumTv.setText(bucketName);

		if (bucketName == null || "图片和视频".equals(bucketName)
				|| "所有视频".equals(bucketName) || "所有图片".equals(bucketName)) {
			MediaPicker.showCamera(true);
			mMediaFragment.updateMediaView();
		} else {
			MediaPicker.showCamera(false);
			mMediaFragment.updateMediaView(album);
		}

		mAlbum = album;
	}

	@Override
	public void onMediaSelected(List<Media> selectedMedias) {
		int size = selectedMedias.size();
		if (size > 0) {
			mPreviewTv.setText(String.format("预览(%1$d)", size));
			mPreviewTv.setEnabled(true);
		} else {
			mPreviewTv.setText("预览");
			mPreviewTv.setEnabled(false);
		}

		invalidateOptionsMenu();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.album_layout:
			hideAlbumSet();
			break;
		case R.id.album_tv:
			if (isAlbumSetShowing()) {
				hideAlbumSet();
			} else {
				showAlbumSet();
			}
			break;
		case R.id.preview_tv:
			if (isAlbumSetShowing()) {
				hideAlbumSet();
			}
			launchPreviewActivity(mMediaFragment.getSelectedMedias(),
					mMediaFragment.getSelectedMedias(), 0);
			break;
		default:
			break;
		}
	}

	private void launchPreviewActivity(ArrayList<Media> medias,
			ArrayList<Media> selectedMedias, int position) {
		Intent intent = new Intent(this, MediaPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("medias", medias);
		bundle.putSerializable("selectedMedias", selectedMedias);
		bundle.putInt("position", position);
		intent.putExtras(bundle);
		startActivityForResult(intent,
				MediaPickerConstants.PREVIEW_SELECTED_MEDIAS_REQUEST_CODE);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MediaPickerConstants.PREVIEW_SELECTED_MEDIAS_REQUEST_CODE) {
			if (resultCode == MediaPickerConstants.PREVIEW_MEDIAS_BACK_RESULT_CODE) {
				ArrayList<Media> selectedMedias = (ArrayList<Media>) data
						.getSerializableExtra("selectedMedias");
				onMediaSelected(selectedMedias);

				mMediaFragment.setSelectedMedias(selectedMedias);
			} else if (resultCode == RESULT_OK) {
				ArrayList<Media> selectedMedias = (ArrayList<Media>) data
						.getSerializableExtra("selectedMedias");
				onMediaSelected(selectedMedias);

				setCallback(RESULT_OK);
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (isAlbumSetShowing()) {
			hideAlbumSet();
		} else {
			super.onBackPressed();
		}
	}

	private void hideAlbumSet() {
		new AnimationUtil(this, R.anim.translate_down).setLinearInterpolator()
				.startAnimation(mAlbumLayout);
		mAlbumLayout.setVisibility(View.GONE);
	}

	private void showAlbumSet() {
		mAlbumLayout.setVisibility(View.VISIBLE);
		new AnimationUtil(this, R.anim.translate_up_current)
				.setLinearInterpolator().startAnimation(mAlbumLayout);
	}

	private boolean isAlbumSetShowing() {
		return mAlbumLayout.getVisibility() == View.VISIBLE;
	}

}
