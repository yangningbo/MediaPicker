package com.hello1987.mediapicker.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hello1987.mediapicker.MediaPickerConstants;
import com.hello1987.mediapicker.R;
import com.hello1987.mediapicker.data.AsyncMediaHelper;
import com.hello1987.mediapicker.data.AsyncMediaHelper.OnAlbumsLoadedListener;
import com.hello1987.mediapicker.data.MediaType;
import com.hello1987.mediapicker.model.Album;
import com.hello1987.mediapicker.ui.adapter.AlbumAdapter;

public class AlbumFragment extends Fragment implements OnAlbumsLoadedListener,
		OnItemClickListener {

	private ListView mAlbumListView;
	private AlbumAdapter mAlbumAdapter;

	private AsyncMediaHelper mAsyncMediaHelper;
	private OnAlbumSelectedListener mCallback;

	private int mMediaType = MediaType.MEDIA_TYPE_IMAGE;
	private int mPosition = -1;

	public AlbumFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnAlbumSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnAlbumSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAsyncMediaHelper = new AsyncMediaHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_album, container, false);
		mAlbumListView = (ListView) view.findViewById(R.id.ablum_list_view);
		mAlbumAdapter = new AlbumAdapter(getActivity());
		mAlbumListView.setAdapter(mAlbumAdapter);
		mAlbumListView.setOnItemClickListener(this);

		if (MediaPickerConstants.SHOW_IMAGE && MediaPickerConstants.SHOW_VIDEO) {
			mMediaType = MediaType.MEDIA_TYPE_ALL;
		} else {
			if (MediaPickerConstants.SHOW_VIDEO) {
				mMediaType = MediaType.MEDIA_TYPE_VIDEO;
			} else {
				mMediaType = MediaType.MEDIA_TYPE_IMAGE;
			}
		}

		loadAlbums();

		return view;
	}

	private void loadAlbums() {
		mAsyncMediaHelper.loadAlbums(mMediaType, this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}

	@Override
	public void onAlbumsLoaded(List<Album> albums) {
		if (albums.size() > 0) {
			Album firstAlbum = null;
			if (MediaPickerConstants.SHOW_IMAGE
					&& MediaPickerConstants.SHOW_VIDEO) {
				firstAlbum = new Album("图片和视频", albums.get(0).getData(), albums
						.get(0).getDateTaken());
			} else {
				if (MediaPickerConstants.SHOW_VIDEO) {
					firstAlbum = new Album("所有视频", albums.get(0).getData(),
							albums.get(0).getDateTaken());
				} else {
					firstAlbum = new Album("所有图片", albums.get(0).getData(),
							albums.get(0).getDateTaken());
				}
			}
			firstAlbum.setChecked(true);

			List<Album> albumSet = new ArrayList<Album>();
			albumSet.add(firstAlbum);
			// albumSet.addAll(albums);

			int totalSize = 0;
			for (Album album : albums) {
				totalSize = totalSize + album.getSize();
				albumSet.add(album);
			}
			firstAlbum.setSize(totalSize);

			mAlbumAdapter.setAlbums(albumSet);
		} else {
			Toast.makeText(getActivity(), "没有找到文件", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position != mPosition) {
			mAlbumAdapter.updateItems(parent, position);
			mPosition = position;
		}

		if (mCallback == null)
			return;

		Album album = (Album) parent.getItemAtPosition(position);
		mCallback.onAlbumSelected(album);
	}

	public interface OnAlbumSelectedListener {
		void onAlbumSelected(Album album);
	}

}
