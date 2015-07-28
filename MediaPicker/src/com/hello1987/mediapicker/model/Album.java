package com.hello1987.mediapicker.model;

public class Album {

	private int bucketId;
	private String bucketName;
	private String data;
	private long dateTaken;
	private int size;
	private boolean isChecked;

	public Album(String bucketName, String data, long dateTaken) {
		this(-1, bucketName, data, dateTaken);
	}

	public Album(int bucketId, String bucketName, String data, long dateTaken) {
		this.bucketId = bucketId;
		this.bucketName = bucketName;
		this.data = data;
		this.dateTaken = dateTaken;
		this.size = 1;
		this.isChecked = false;
	}

	public int getBucketId() {
		return bucketId;
	}

	public void setBucketId(int bucketId) {
		this.bucketId = bucketId;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(int dateTaken) {
		this.dateTaken = dateTaken;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public void autoPlus() {
		size++;
	}

}
