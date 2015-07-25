package com.hello1987.mediapicker;

public class MediaPicker {

	/**
	 * Set folder Name
	 * 
	 * @param albumName
	 *            default folder name is Camera.
	 */
	public static void setAlbumName(String albumName) {
		if (albumName != null) {
			MediaPickerConstants.ALBUM_NAME = albumName;
		}
	}

	/**
	 * To set visibility of camera/video button.
	 * 
	 * @param showCamera
	 *            boolean value.To set visibility of camera/video button.
	 *            default its visible.
	 */
	public static void showCamera(boolean showCamera) {
		MediaPickerConstants.SHOW_CAMERA = showCamera;
	}

	/**
	 * To set file size limit for image selection.
	 * 
	 * @param size
	 *            int file size in mb. default is set to 20 mb.
	 */
	public static void setImageSize(int size) {
		MediaPickerConstants.SELECTED_IMAGE_SIZE_IN_MB = size;
	}

	/**
	 * To set file size limit for video selection.
	 * 
	 * @param size
	 *            int file size in mb. default is set to 20 mb.
	 */
	public static void setVideoSize(int size) {
		MediaPickerConstants.SELECTED_VIDEO_SIZE_IN_MB = size;
	}

	/**
	 * To set number of items that can be selected.
	 * 
	 * @param limit
	 *            int value. Default is 9.
	 */
	public static void setSelectionLimit(int limit) {
		MediaPickerConstants.MAX_MEDIA_LIMIT = limit;
	}

	/**
	 * To set already selected file count.
	 * 
	 * @param count
	 *            int value.
	 */
	public static void setSelectedMediaCount(int count) {
		MediaPickerConstants.SELECTED_MEDIA_COUNT = count;
	}

	/**
	 * Get selected media file count.
	 * 
	 * @return count.
	 */
	public static int getSelectedMediaCount() {
		return MediaPickerConstants.SELECTED_MEDIA_COUNT;
	}

	/**
	 * To display images only.
	 */
	public static void showImage() {
		MediaPickerConstants.SHOW_IMAGE = true;
		MediaPickerConstants.SHOW_VIDEO = false;
	}

	/**
	 * To display videos only.
	 */
	public static void showVideo() {
		MediaPickerConstants.SHOW_IMAGE = false;
		MediaPickerConstants.SHOW_VIDEO = true;
	}

	/**
	 * To display video and images.
	 */
	public static void showAll() {
		MediaPickerConstants.SHOW_IMAGE = true;
		MediaPickerConstants.SHOW_VIDEO = true;
	}

}
