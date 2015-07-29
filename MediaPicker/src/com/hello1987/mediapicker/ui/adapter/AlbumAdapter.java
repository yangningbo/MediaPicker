package com.hello1987.mediapicker.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hello1987.mediapicker.R;
import com.hello1987.mediapicker.model.Album;

public class AlbumAdapter extends BaseAdapter {

	private Context mContext;
	private List<Album> mAlbums;
	private LayoutInflater mInflater;

	public AlbumAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public void setAlbums(List<Album> albums) {
		if (albums == null)
			return;

		mAlbums = albums;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mAlbums == null)
			return 0;
		return mAlbums.size();
	}

	@Override
	public Album getItem(int position) {
		if (mAlbums == null)
			return null;
		return mAlbums.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.album_item, parent, false);

			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Album album = mAlbums.get(position);
		setImage(holder.coverIv, album.getData());
		setText(holder.nameTv, album.getBucketName());
		setText(holder.sizeTv, album.getSize() + "张");
		setIndex(holder.indexIv, album.isChecked());

		return convertView;
	}

	private void setImage(ImageView imageView, String url) {
		if (url.startsWith("file://")) {
			loadImage(imageView, url);
		} else {
			loadImage(imageView, "file://" + url);
		}
	}

	private void loadImage(ImageView imageView, String url) {
		Glide.with(mContext).load(url).error(R.drawable.ic_image_load_failed)
				.into(imageView);
	}

	private void setText(TextView textView, String text) {
		textView.setText(text);
	}

	private void setIndex(ImageView imageView, boolean isChecked) {
		if (isChecked) {
			imageView.setVisibility(View.VISIBLE);
		} else {
			imageView.setVisibility(View.GONE);
		}
	}

	public void updateItems(AdapterView<?> parent, int position) {
		for (int i = 0; i < parent.getCount(); i++) {
			Album album = (Album) parent.getItemAtPosition(i);
			if (i == position) {
				album.setChecked(true);
			} else {
				album.setChecked(false);
			}
		}
		notifyDataSetChanged();
	}

	static class ViewHolder {
		ImageView coverIv;
		TextView nameTv;
		TextView sizeTv;
		ImageView indexIv;

		public ViewHolder(View view) {
			coverIv = (ImageView) view.findViewById(R.id.cover_iv);
			nameTv = (TextView) view.findViewById(R.id.name_tv);
			sizeTv = (TextView) view.findViewById(R.id.size_tv);
			indexIv = (ImageView) view.findViewById(R.id.index_iv);
		}
	}

}
