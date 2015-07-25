package com.hello1987.mediapicker.model;

public class Album {

	private String bucketName;
	private String data;
	private int dateTaken;
	private int size;
	private boolean isChecked;

	public Album(String bucketName, String data, int dateTaken) {
		this.bucketName = bucketName;
		this.data = data;
		this.dateTaken = dateTaken;
		this.size = 1;
		this.isChecked = false;
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

	public int getDateTaken() {
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
