package com.hello1987.mediapicker.util;

public class ByteUtil {

	public static final int KB = 1024;
	public static final int MB = 1024 * KB;
	public static final int GB = 1024 * MB;

	public static String format(long size) {
		if (size < KB) {// <1k
			return String.format("%d B", size);
		} else if (size < MB) {// <1m
			return String.format("%.1f KB", size / KB / 1.0f);
		} else if (size < GB) {// <1g
			return String.format("%.1f MB", size / MB / 1.0f);
		} else {
			return String.format("%.1f TB", size / GB / 1.0f);
		}
	}
}
