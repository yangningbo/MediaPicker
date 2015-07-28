package com.hello1987.mediapicker.data;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.hello1987.mediapicker.model.Album;
import com.hello1987.mediapicker.model.Media;

public class AsyncMediaHelper {

	private Context mContext;
	private MediaHelper mMediaHelper;

	public AsyncMediaHelper(Context context) {
		mContext = context;
		mMediaHelper = new MediaHelper(context);
	}

	/**
	 * 异步获取相册列表
	 * 
	 * @param listener
	 */
	public void loadAlbums(final int type, final OnAlbumsLoadedListener listener) {
		final Handler handler = new MediaHandler(mContext) {
			@SuppressWarnings("unchecked")
			@Override
			public void onHandleMessage(Message msg) {
				super.onHandleMessage(msg);
				if (listener != null) {
					listener.onAlbumsLoaded((List<Album>) msg.obj);
				}
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Album> albums = mMediaHelper.loadAlbums(type);
				Message msg = Message.obtain();
				msg.obj = albums;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 异步获取最近media列表
	 * 
	 * @return
	 */
	public void loadMedias(final int type, final OnMediasLoadedListener listener) {
		final Handler handler = new MediaHandler(mContext) {
			@SuppressWarnings("unchecked")
			@Override
			public void onHandleMessage(Message msg) {
				if (listener != null) {
					listener.onMediasLoaded((List<Media>) msg.obj);
				}
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Media> medias = mMediaHelper.loadMedias(type);
				Message msg = Message.obtain();
				msg.obj = medias;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 异步获取某专辑下的media列表
	 * 
	 * @param bucketName
	 * @return
	 */
	public void loadMedias(final int bucketId, final int type,
			final OnMediasLoadedListener listener) {
		final Handler handler = new MediaHandler(mContext) {
			@SuppressWarnings("unchecked")
			@Override
			public void onHandleMessage(Message msg) {
				if (listener != null) {
					listener.onMediasLoaded((List<Media>) msg.obj);
				}
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Media> medias = mMediaHelper.loadMedias(bucketId, type);
				Message msg = Message.obtain();
				msg.obj = medias;
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取专辑列表回调
	 */
	public interface OnAlbumsLoadedListener {
		void onAlbumsLoaded(List<Album> albums);
	}

	/**
	 * 获取media列表回调
	 */
	public interface OnMediasLoadedListener {
		void onMediasLoaded(List<Media> medias);
	}

}
