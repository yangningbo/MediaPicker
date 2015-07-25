package com.hello1987.mediapicker.ui.adapter;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hello1987.mediapicker.R;
import com.hello1987.mediapicker.data.MediaType;
import com.hello1987.mediapicker.model.Media;

public class MediaPagerAdapter extends PagerAdapter {

	private Context mContext;
	private List<Media> mMedias;
	private LayoutInflater mInflater;

	private PhotoViewAttacher mAttacher;
	private OnMediaTapListener mOnMediaTapListener;
	private OnVideoClickedListener mOnVideoClickedListener;

	public MediaPagerAdapter(Context context, List<Media> medias) {
		mContext = context;
		mMedias = medias;
		mInflater = LayoutInflater.from(context);
	}

	public void setOnMediaTapListener(OnMediaTapListener listener) {
		mOnMediaTapListener = listener;
	}

	public void setOnVideoClickedListener(OnVideoClickedListener listener) {
		mOnVideoClickedListener = listener;
	}

	@Override
	public int getCount() {
		if (mMedias == null)
			return 0;
		return mMedias.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = mInflater.inflate(R.layout.media_pager_item, container,
				false);

		PhotoView photoIv = (PhotoView) view.findViewById(R.id.photo_iv);
		mAttacher = new PhotoViewAttacher(photoIv);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				if (mOnMediaTapListener == null)
					return;

				mOnMediaTapListener.onMediaTap(view, x, y, position);
			}
		});

		ImageView playIv = (ImageView) view.findViewById(R.id.play_iv);
		playIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnVideoClickedListener == null)
					return;

				mOnVideoClickedListener.onVideoClicked(v, position);
			}
		});

		Media media = mMedias.get(position);

		setImage(photoIv, media.getData());

		if (media.getMediaType() == MediaType.MEDIA_TYPE_VIDEO) {
			playIv.setVisibility(View.VISIBLE);
		} else {
			playIv.setVisibility(View.GONE);
		}

		container.addView(view, 0);

		return view;
	}

	private void setImage(ImageView imageView, String url) {
		if (url.startsWith("file://")) {
			loadImage(imageView, url);
		} else {
			loadImage(imageView, "file://" + url);
		}
	}

	private void loadImage(ImageView imageView, String url) {
		Glide.with(mContext).load(url).error(R.drawable.ic_picture_loadfailed)
				.into(imageView);
	}

	public void cleanup() {
		if (mAttacher != null) {
			mAttacher.cleanup();
		}
	}

	public interface OnMediaTapListener {
		void onMediaTap(View view, float x, float y, int position);
	}

	public interface OnVideoClickedListener {
		void onVideoClicked(View view, int position);
	}

}
