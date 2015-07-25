package com.hello1987.mediapicker;

import java.io.File;

public class MediaPickerConstants {

	public static boolean SHOW_IMAGE = true;
	public static boolean SHOW_VIDEO = false;
	public static boolean SHOW_CAMERA = false;

	public static String ALBUM_NAME = "Camera";

	public static int MAX_MEDIA_LIMIT = 9;
	public static int SELECTED_MEDIA_COUNT = 0;
	public static int SELECTED_IMAGE_SIZE_IN_MB = 20;
	public static int SELECTED_VIDEO_SIZE_IN_MB = 20;

	public static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int CAPTURE_VIDEO_REQUEST_CODE = 200;

	public static final int PREVIEW_ALBUM_MEDIAS_REQUEST_CODE = 300;
	public static final int PREVIEW_SELECTED_MEDIAS_REQUEST_CODE = 400;
	public static final int PREVIEW_MEDIAS_BACK_RESULT_CODE = 500;

	public static long chekcMediaFileSize(File mediaFile, boolean isVideo) {
		long fileSizeInBytes = mediaFile.length();

		long fileSizeInKB = fileSizeInBytes / 1024;

		long fileSizeInMB = fileSizeInKB / 1024;

		int requireSize = 0;
		if (isVideo) {
			requireSize = MediaPickerConstants.SELECTED_VIDEO_SIZE_IN_MB;
		} else {
			requireSize = MediaPickerConstants.SELECTED_IMAGE_SIZE_IN_MB;
		}
		if (fileSizeInMB <= requireSize) {
			return 0;
		}

		return fileSizeInMB;
	}

}
