package com.hello1987.mediapicker.sample;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.hello1987.mediapicker.MediaPicker;
import com.hello1987.mediapicker.R;
import com.hello1987.mediapicker.model.Media;
import com.hello1987.mediapicker.ui.MediaPickerActivity;

public class SampleActivity extends Activity {

	private static final String TAG = "SampleActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		findViewById(R.id.take_images).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						MediaPicker.showImage();
						MediaPicker.showCamera(true);
						MediaPicker.setSelectionLimit(1);
						MediaPicker.setSelectedMediaCount(0);

						Intent intent = new Intent(SampleActivity.this,
								MediaPickerActivity.class);
						startActivityForResult(intent, 0x100);
					}
				});

		findViewById(R.id.take_videos).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						MediaPicker.showVideo();
						MediaPicker.showCamera(true);
						MediaPicker.setSelectionLimit(9);
						MediaPicker.setSelectedMediaCount(0);

						Intent intent = new Intent(SampleActivity.this,
								MediaPickerActivity.class);
						startActivityForResult(intent, 0x101);
					}
				});

		findViewById(R.id.take_images_and_videos).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						MediaPicker.showAll();
						MediaPicker.showCamera(true);
						MediaPicker.setSelectionLimit(9);
						MediaPicker.setSelectedMediaCount(0);

						Intent intent = new Intent(SampleActivity.this,
								MediaPickerActivity.class);
						startActivityForResult(intent, 0x102);
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0x100) {
			if (resultCode == RESULT_OK) {
				ArrayList<Media> selectedMedias = (ArrayList<Media>) data
						.getSerializableExtra("selectedMedias");
				Log.i(TAG, "selectedMedias size: " + selectedMedias.size());
				Log.i(TAG, selectedMedias.toString());
			}
		} else if (requestCode == 0x101) {
			if (resultCode == RESULT_OK) {
				ArrayList<Media> selectedMedias = (ArrayList<Media>) data
						.getSerializableExtra("selectedMedias");
				Log.i(TAG, "selectedMedias size: " + selectedMedias.size());
				Log.i(TAG, selectedMedias.toString());
			}
		} else if (requestCode == 0x102) {
			if (resultCode == RESULT_OK) {
				ArrayList<Media> selectedMedias = (ArrayList<Media>) data
						.getSerializableExtra("selectedMedias");
				Log.i(TAG, "selectedMedias size: " + selectedMedias.size());
				Log.i(TAG, selectedMedias.toString());
			}
		}
	}

}
