package com.hello1987.mediapicker.model;

import java.io.Serializable;

public class Media implements Serializable {

	private static final long serialVersionUID = 1L;

	private String data;
	private int mediaType;
	private long dateTaken;
	private long size;

	public Media(String data) {
		this(data, 0);
	}

	public Media(String data, int mediaType) {
		this(data, mediaType, 0, 0);
	}

	public Media(String data, int mediaType, long dateTaken, long size) {
		this.data = data;
		this.mediaType = mediaType;
		this.dateTaken = dateTaken;
		this.size = size;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public long getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(int dateTaken) {
		this.dateTaken = dateTaken;
	}

	public long getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Media)) {
			return false;
		}

		Media media = (Media) obj;
		if (data == null) {
			if (media.data != null) {
				return false;
			}
		} else if (!data.equals(media.data)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "Media [data=" + data + ", mediaType=" + mediaType
				+ ", dateTaken=" + dateTaken + ", size=" + size + "]";
	}

}
