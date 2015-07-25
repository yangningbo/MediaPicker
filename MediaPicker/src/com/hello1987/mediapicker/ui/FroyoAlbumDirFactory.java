package com.hello1987.mediapicker.ui;

import java.io.File;

import android.os.Environment;

public final class FroyoAlbumDirFactory implements AlbumStorageDirFactory {

	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				albumName);
	}
}
