package com.hello1987.mediapicker.ui;

import java.io.File;

import android.os.Environment;

public final class BaseAlbumDirFactory implements AlbumStorageDirFactory {

	private static final String CAMERA_DIR = "/dcim/";

	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR
				+ albumName);
	}

}
