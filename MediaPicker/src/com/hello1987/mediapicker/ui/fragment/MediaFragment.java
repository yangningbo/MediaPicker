package com.hello1987.mediapicker.ui.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.hello1987.mediapicker.MediaPickerConstants;
import com.hello1987.mediapicker.R;
import com.hello1987.mediapicker.data.AsyncMediaHelper;
import com.hello1987.mediapicker.data.AsyncMediaHelper.OnMediasLoadedListener;
import com.hello1987.mediapicker.data.MediaType;
import com.hello1987.mediapicker.model.Album;
import com.hello1987.mediapicker.model.Media;
import com.hello1987.mediapicker.ui.AlbumStorageDirFactory;
import com.hello1987.mediapicker.ui.BaseAlbumDirFactory;
import com.hello1987.mediapicker.ui.FroyoAlbumDirFactory;
import com.hello1987.mediapicker.ui.MediaPreviewActivity;
import com.hello1987.mediapicker.ui.adapter.MediaAdapter;
import com.hello1987.mediapicker.ui.adapter.MediaAdapter.OnItemCheckedListener;
import com.hello1987.mediapicker.util.ByteUtil;

public class MediaFragment extends Fragment implements OnMediasLoadedListener,
		OnItemClickListener, OnItemCheckedListener {

	private GridView mMediaGridView;
	private MediaAdapter mMediaAdapter;

	private AsyncMediaHelper mAsyncMediaHelper;
	private OnMediaSelectedListener mCallback;

	private int mMediaType = MediaType.MEDIA_TYPE_IMAGE;

	private ArrayList<Media> mSelectedMedias = new ArrayList<Media>();

	public MediaFragment() {
	}

	public void setSelectedMedias(ArrayList<Media> selectedMedias) {
		if (selectedMedias == null)
			return;
		mSelectedMedias = selectedMedias;

		if (mMediaAdapter != null)
			mMediaAdapter.setSelectedMedias(selectedMedias);
	}

	public ArrayList<Media> getSelectedMedias() {
		return mSelectedMedias;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnMediaSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnMediaSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAsyncMediaHelper = new AsyncMediaHelper(getActivity());
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_media, container, false);
		mMediaGridView = (GridView) view.findViewById(R.id.media_grid_view);
		mMediaAdapter = new MediaAdapter(getActivity());
		mMediaAdapter.setSelectedMedias(mSelectedMedias);
		mMediaAdapter.setOnItemCheckedListener(this);
		mMediaGridView.setAdapter(mMediaAdapter);
		mMediaGridView.setOnItemClickListener(this);

		if (MediaPickerConstants.SHOW_IMAGE && MediaPickerConstants.SHOW_VIDEO) {
			mMediaType = MediaType.MEDIA_TYPE_ALL;
		} else {
			if (MediaPickerConstants.SHOW_VIDEO) {
				mMediaType = MediaType.MEDIA_TYPE_VIDEO;
			} else {
				mMediaType = MediaType.MEDIA_TYPE_IMAGE;
			}
		}

		return view;
	};

	private void loadMedias() {
		mAsyncMediaHelper.loadMedias(mMediaType, this);
	}

	private void loadMedias(int bucketId) {
		mAsyncMediaHelper.loadMedias(bucketId, mMediaType, this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}

	public void updateMediaView() {
		updateMediaView(null);
	}

	public void updateMediaView(Album album) {
		if (album == null) {
			loadMedias();
		} else {
			loadMedias(album.getBucketId());
		}
	}

	@Override
	public void onMediasLoaded(List<Media> medias) {
		if (medias.size() > 0) {
			List<Media> mediaSet = new ArrayList<Media>();

			if (MediaPickerConstants.SHOW_CAMERA) {
				mediaSet.add(new Media("camera://camera"));
			}
			mediaSet.addAll(medias);

			mMediaAdapter.setMedias(mediaSet);
			mMediaGridView.smoothScrollToPosition(0); // 滚动到顶端
		} else {
			Toast.makeText(getActivity(), "没有找到文件", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Media media = mMediaAdapter.getItem(position);

		if (media.getData().startsWith("camera://camera")) {
			openCamera();
		} else {
			ArrayList<Media> medias = new ArrayList<Media>();
			medias.addAll(mMediaAdapter.getMedias());
			if (MediaPickerConstants.SHOW_CAMERA) {
				medias.remove(0);
				launchPreviewActivity(medias, mSelectedMedias, position - 1);
			} else {
				launchPreviewActivity(medias, mSelectedMedias, position);
			}
		}
	}

	private Uri fileUri;

	private void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MediaType.MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent,
				MediaPickerConstants.CAPTURE_IMAGE_REQUEST_CODE);
	}

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	@SuppressLint("SimpleDateFormat")
	private File getOutputMediaFile(int type) {
		File mediaStorageDir = getMediaStorageDir();

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MediaType.MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MediaType.MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	private File getMediaStorageDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			storageDir = getAlbumStorageDirFactory().getAlbumStorageDir(
					getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						return null;
					}
				}
			}
		}

		return storageDir;
	}

	private AlbumStorageDirFactory getAlbumStorageDirFactory() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return new FroyoAlbumDirFactory();
		} else {
			return new BaseAlbumDirFactory();
		}
	}

	private String getAlbumName() {
		return MediaPickerConstants.ALBUM_NAME;
	}

	private void launchPreviewActivity(ArrayList<Media> medias,
			ArrayList<Media> selectedMedias, int position) {
		Intent intent = new Intent(getActivity(), MediaPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("medias", medias);
		bundle.putSerializable("selectedMedias", selectedMedias);
		bundle.putInt("position", position);
		intent.putExtras(bundle);
		startActivityForResult(intent,
				MediaPickerConstants.PREVIEW_ALBUM_MEDIAS_REQUEST_CODE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MediaPickerConstants.CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String fileUriString = fileUri.toString()
						.replaceFirst("file:///", "/").trim();
				Media media = new Media(fileUriString,
						MediaType.MEDIA_TYPE_IMAGE);
				mSelectedMedias.clear();
				mSelectedMedias.add(media);

				setCallback(Activity.RESULT_OK);
				getActivity().finish();
			}
		} else if (requestCode == MediaPickerConstants.PREVIEW_ALBUM_MEDIAS_REQUEST_CODE) {
			if (resultCode == MediaPickerConstants.PREVIEW_MEDIAS_BACK_RESULT_CODE) {
				mSelectedMedias = (ArrayList<Media>) data
						.getSerializableExtra("selectedMedias");
				mMediaAdapter.setSelectedMedias(mSelectedMedias);

				mCallback.onMediaSelected(mSelectedMedias);
			} else if (resultCode == Activity.RESULT_OK) {
				mSelectedMedias = (ArrayList<Media>) data
						.getSerializableExtra("selectedMedias");

				setCallback(Activity.RESULT_OK);
				getActivity().finish();
			}
		}
	}

	private void setCallback(int resultCode) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("selectedMedias", mSelectedMedias);
		intent.putExtras(bundle);
		getActivity().setResult(resultCode, intent);
	}

	public interface OnMediaSelectedListener {
		void onMediaSelected(List<Media> selectedMedias);
	}

	@Override
	public void onItemChecked(View parent, int position) {
		Media media = mMediaAdapter.getItem(position);

		if (!contains(media)) {
			if (media.getMediaType() == MediaType.MEDIA_TYPE_VIDEO) {
				if (media.getSize() > MediaPickerConstants.SELECTED_VIDEO_SIZE_IN_MB
						* ByteUtil.MB) {
					Toast.makeText(
							getActivity(),
							String.format(
									"视频大小超出%1$dMB",
									MediaPickerConstants.SELECTED_VIDEO_SIZE_IN_MB),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if (MediaPickerConstants.MAX_MEDIA_LIMIT == 1
					&& MediaPickerConstants.SELECTED_MEDIA_COUNT == 1) {
				mSelectedMedias.remove(0);
				MediaPickerConstants.SELECTED_MEDIA_COUNT--;

				mMediaAdapter.resetItemState(mMediaGridView, position);
			} else {
				if ((MediaPickerConstants.SELECTED_MEDIA_COUNT == MediaPickerConstants.MAX_MEDIA_LIMIT)) {
					Toast.makeText(
							getActivity(),
							String.format("最多只能选择%1$d个文件",
									MediaPickerConstants.SELECTED_MEDIA_COUNT),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			mSelectedMedias.add(media);
			MediaPickerConstants.SELECTED_MEDIA_COUNT++;
		} else {
			mSelectedMedias.remove(media);
			MediaPickerConstants.SELECTED_MEDIA_COUNT--;
		}

		mMediaAdapter.updateItemState(mMediaGridView, position);

		if (mCallback != null) {
			mCallback.onMediaSelected(mSelectedMedias);
		}
	}

	private boolean contains(Media media) {
		if (mSelectedMedias == null)
			return false;

		return mSelectedMedias.contains(media);
	}

}
