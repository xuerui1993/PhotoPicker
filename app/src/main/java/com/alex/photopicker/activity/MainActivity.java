package com.alex.photopicker.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alex.photopicker.R;
import com.alex.photopicker.widget.PhotoEditView;


public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private PhotoEditView mPhotoEditView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		mPhotoEditView = (PhotoEditView) findViewById(R.id.photo_edit);
		mPhotoEditView.setMaxCount(9,4);
	}

	/**
	 * 申请相机权限回调
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		mPhotoEditView.onRequestPermissionResult(requestCode,permissions,grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(TAG, "onActivityResult: requestCode = " + requestCode + ",resultCode = " + resultCode);
		mPhotoEditView.onActivityResult(requestCode,resultCode,data);
	}

}
