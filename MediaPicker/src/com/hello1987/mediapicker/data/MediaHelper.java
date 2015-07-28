package com.hello1987.mediapicker.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.VideoColumns;
import android.util.Log;
import android.util.SparseArray;

import com.hello1987.mediapicker.model.Album;
import com.hello1987.mediapicker.model.Media;

public class MediaHelper {

	private static final String TAG = "MediaHelper";

	private ContentResolver mResolver;

	public MediaHelper(Context context) {
		mResolver = context.getContentResolver();
	}

	/**
	 * 获取专辑列表
	 * 
	 * @return
	 */
	public List<Album> loadAlbums(int type) {
		SparseArray<Album> albumSet = new SparseArray<Album>();

		if ((type & MediaType.MEDIA_TYPE_IMAGE) != 0) {
			updateAlbumsFromImagesTable(albumSet);
		}

		if ((type & MediaType.MEDIA_TYPE_VIDEO) != 0) {
			updateAlbumsFromVideoTable(albumSet);
		}

		int size = albumSet.size();
		List<Album> albums = new ArrayList<Album>(size);
		for (int i = 0; i < size; i++) {
			albums.add(albumSet.valueAt(i));
		}

		Collections.sort(albums, new Comparator<Album>() {
			@Override
			public int compare(Album a, Album b) {
				long d1 = a.getDateTaken();
				long d2 = b.getDateTaken();
				if (d2 > d1) {
					return 1;
				} else if (d2 == d1) {
					return 0;
				} else {
					return -1;
				}
			}
		});

		return albums;
	}

	private void updateAlbumsFromImagesTable(SparseArray<Album> albums) {
		Cursor cursor = mResolver.query(//
				Images.Media.EXTERNAL_CONTENT_URI,// uri
				new String[] { ImageColumns.BUCKET_ID,
						ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.DATA,
						ImageColumns.DATE_TAKEN },// projection[]
				ImageColumns.SIZE + " >= ?",// selection
				new String[] { String.valueOf(1024 * 10) },// selectionArgs[]
				ImageColumns.DATE_TAKEN + " DESC"// sortOrder
		);
		if (cursor == null) {
			Log.w(TAG, "cannot open media database: "
					+ Images.Media.EXTERNAL_CONTENT_URI);
			return;
		}

		try {
			while (cursor.moveToNext()) {
				int bucketId = cursor.getInt(cursor
						.getColumnIndex(ImageColumns.BUCKET_ID));
				Album album = albums.get(bucketId);
				if (album == null) {
					String bucketName = cursor.getString(cursor
							.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));//
					String data = cursor.getString(cursor
							.getColumnIndex(ImageColumns.DATA));//
					int dateTaken = cursor.getInt(cursor
							.getColumnIndex(ImageColumns.DATE_TAKEN));//
					album = new Album(bucketName, data, dateTaken);

					albums.put(bucketId, album);
				} else {
					album.autoPlus();
				}
			}
		} finally {
			closeSilently(cursor);
		}
	}

	private void updateAlbumsFromVideoTable(SparseArray<Album> albumSet) {
		Cursor cursor = mResolver.query(
				Video.Media.EXTERNAL_CONTENT_URI,// uri
				new String[] { VideoColumns.BUCKET_ID,
						VideoColumns.BUCKET_DISPLAY_NAME, VideoColumns.DATA,
						VideoColumns.DATE_TAKEN },// projection[]
				VideoColumns.SIZE + " >= ? and " + VideoColumns.SIZE + " <= ?",// selection
				new String[] { String.valueOf(1024 * 10),
						String.valueOf(1024 * 1024 * 200) },// selectionArgs[]
				VideoColumns.DATE_TAKEN + " DESC"// sortOrder
		);
		if (cursor == null) {
			Log.w(TAG, "cannot open media database: "
					+ Video.Media.EXTERNAL_CONTENT_URI);
			return;
		}

		try {
			while (cursor.moveToNext()) {
				int bucketId = cursor.getInt(cursor
						.getColumnIndex(VideoColumns.BUCKET_ID));
				Album album = albumSet.get(bucketId);
				if (album == null) {
					String bucketName = cursor.getString(cursor
							.getColumnIndex(VideoColumns.BUCKET_DISPLAY_NAME));//
					String data = cursor.getString(cursor
							.getColumnIndex(VideoColumns.DATA));//
					int dateTaken = cursor.getInt(cursor
							.getColumnIndex(VideoColumns.DATE_TAKEN));//
					album = new Album(bucketName, data, dateTaken);

					albumSet.put(bucketId, album);
				} else {
					album.autoPlus();
				}
			}
		} finally {
			closeSilently(cursor);
		}
	}

	/**
	 * 获取最近media列表
	 * 
	 * @return
	 */
	public List<Media> loadMedias(int type) {
		return loadMedias(null, type);
	}

	/**
	 * 获取某专辑下的media列表
	 * 
	 * @param bucketName
	 * @return
	 */
	public List<Media> loadMedias(String bucketName, int type) {
		List<Media> medias = new ArrayList<Media>();

		if ((type & MediaType.MEDIA_TYPE_IMAGE) != 0) {
			updateMediasFromImagesTable(bucketName, medias);
		}

		if ((type & MediaType.MEDIA_TYPE_VIDEO) != 0) {
			updateMediasFromVideoTable(bucketName, medias);
		}

		Collections.sort(medias, new Comparator<Media>() {
			@Override
			public int compare(Media a, Media b) {
				long d1 = a.getDateTaken();
				long d2 = b.getDateTaken();
				if (d2 > d1) {
					return 1;
				} else if (d2 == d1) {
					return 0;
				} else {
					return -1;
				}
			}
		});

		return medias;
	}

	private void updateMediasFromImagesTable(String bucketName,
			List<Media> medias) {
		String selection = ImageColumns.SIZE + " >= ?";
		String[] selectionArgs = new String[] { String.valueOf(1024 * 10) };

		if (bucketName != null) {
			selection = ImageColumns.SIZE + " >= ? and "
					+ ImageColumns.BUCKET_DISPLAY_NAME + " = ?";
			selectionArgs = new String[] { String.valueOf(1024 * 10),
					bucketName };
		}

		Cursor cursor = mResolver.query(//
				Images.Media.EXTERNAL_CONTENT_URI,// uri
				new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
						ImageColumns.DATA, ImageColumns.SIZE,
						ImageColumns.DATE_TAKEN },// projection[]
				selection,// selection
				selectionArgs,// selectionArgs[]
				ImageColumns.DATE_TAKEN + " DESC"// sortOrder
		);
		if (cursor == null) {
			Log.w(TAG, "cannot open media database: "
					+ Images.Media.EXTERNAL_CONTENT_URI);
			return;
		}

		try {
			while (cursor.moveToNext()) {
				String data = cursor.getString(cursor
						.getColumnIndex(ImageColumns.DATA));//
				int dateTaken = cursor.getInt(cursor
						.getColumnIndex(ImageColumns.DATE_TAKEN));//
				int size = cursor.getInt(cursor
						.getColumnIndex(ImageColumns.SIZE));//

				Media media = new Media(data, MediaType.MEDIA_TYPE_IMAGE,
						dateTaken, size);

				medias.add(media);
			}
		} finally {
			closeSilently(cursor);
		}
	}

	private void updateMediasFromVideoTable(String bucketName,
			List<Media> medias) {
		String selection = VideoColumns.SIZE + " >= ? and " + VideoColumns.SIZE
				+ " <= ?";
		String[] selectionArgs = new String[] { String.valueOf(1024 * 10),
				String.valueOf(1024 * 1024 * 200) };

		if (bucketName != null) {
			selection = VideoColumns.SIZE + " >= ? and " + VideoColumns.SIZE
					+ " <= ? and " + VideoColumns.BUCKET_DISPLAY_NAME + " = ?";
			selectionArgs = new String[] { String.valueOf(1024 * 10),
					String.valueOf(1024 * 1024 * 200), bucketName };
		}

		Cursor cursor = mResolver.query(Video.Media.EXTERNAL_CONTENT_URI,// uri
				new String[] { VideoColumns.BUCKET_DISPLAY_NAME,
						VideoColumns.DATA, VideoColumns.SIZE,
						VideoColumns.DATE_TAKEN },// projection[]
				selection,// selection
				selectionArgs,// selectionArgs[]
				VideoColumns.DATE_TAKEN + " DESC"// sortOrder
		);
		if (cursor == null) {
			Log.w(TAG, "cannot open media database: "
					+ Video.Media.EXTERNAL_CONTENT_URI);
			return;
		}

		try {
			while (cursor.moveToNext()) {
				String data = cursor.getString(cursor
						.getColumnIndex(VideoColumns.DATA));//
				int dateTaken = cursor.getInt(cursor
						.getColumnIndex(VideoColumns.DATE_TAKEN));//
				int size = cursor.getInt(cursor
						.getColumnIndex(VideoColumns.SIZE));//

				Media media = new Media(data, MediaType.MEDIA_TYPE_VIDEO,
						dateTaken, size);

				medias.add(media);
			}
		} finally {
			closeSilently(cursor);
		}
	}

	private static void closeSilently(Cursor cursor) {
		try {
			if (cursor != null)
				cursor.close();
		} catch (Throwable t) {
			Log.w(TAG, "fail to close", t);
		}
	}

}
