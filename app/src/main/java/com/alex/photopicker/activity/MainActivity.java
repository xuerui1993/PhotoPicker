package com.alex.photopicker.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.alex.photopicker.R;
import com.alex.photopicker.adapter.PhotoEditAdapter;
import com.alex.photopicker.bens.MediaPhoto;
import com.alex.photopicker.constants.Constant;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PhotoEditAdapter.OnPhotoClickListener {

	private static final String TAG = "MainActivity";
	private static final int REQUEST_CODE_CAMERA = 100;
	private static final int REQUEST_CAMERA = 101;
	private RecyclerView mRecyclerView;
	private PhotoEditAdapter mPhotoEditAdapter;
	private int mMaxImageCount = 5; //最多选择
	private int mSurplusCount = 5;  //剩余选择
	public ArrayList<String> mUrlList = new ArrayList<>();
	public ArrayList<MediaPhoto> mPhotoList = new ArrayList<>();
	private File mFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		initRecyclerView();
	}

	private void initRecyclerView() {
		mPhotoEditAdapter = new PhotoEditAdapter(this, mUrlList, mMaxImageCount);
		mPhotoEditAdapter.setOnPhotoClickListener(this);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mPhotoEditAdapter);
	}

	@Override
	public void addPhoto() {
		showAddPhotoDialog();
	}

	private void showAddPhotoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ActionSheetDialogStyle);
		View dialogView = View.inflate(this, R.layout.dialog_register_manager, null);
		Button btnGallery = (Button) dialogView.findViewById(R.id.btn_gallery);
		Button btnTakePicture = (Button) dialogView.findViewById(R.id.btn_take_picture);
		Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
		builder.setView(dialogView);
		final AlertDialog dialog = builder.create();
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = window.getAttributes();
		WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay();
		lp.width = (int) (d.getWidth() * 0.95);
		lp.y = 30;
		window.setAttributes(lp);
		dialog.show();
		btnGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
				intent.putExtra(Constant.MAX_COUNT, mSurplusCount);
				startActivityForResult(intent, Constant.ADD_PHOTO);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btnTakePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (checkHasCameraPermission()) {
					takePicture();
				} else {
					applyCameraPermission();//申请权限
				}
			}
		});
	}

	/**
	 * 申请相机权限
	 */
	private void applyCameraPermission() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
	}

	private void takePicture() {
		//启动手机中的  camera app , 帮组去实现 拍照 , 那么需要发意图
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//写到 sd卡上 . 需要申请权限.
		mFile = new File(Environment.getExternalStorageDirectory(), SystemClock.elapsedRealtime()+".jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	/**
	 * 申请相机权限回调
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_CAMERA:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					takePicture();
				} else {
					toast("权限被拒绝");
				}
				break;
		}
	}

	/**
	 * 检查相机权限是否开启
	 * @return
	 */
	private boolean checkHasCameraPermission() {
		int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
		return result == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 预览图片
	 * @param position  点击图片时的位置
	 */
	@Override
	public void previewPhoto(int position) {
		Intent intent = new Intent(this, PhotoPreviewActivity.class);
		intent.putExtra(Constant.PHOTO_PREVIEW_LIST, mPhotoList);
		intent.putExtra(Constant.PHOTO_POSITION,position);
		intent.putExtra(Constant.TITLE_IS_GONE,false);
		intent.putExtra(Constant.MAX_COUNT,mSurplusCount);
		startActivityForResult(intent, Constant.PREVIEW_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(TAG, "onActivityResult: requestCode = " + requestCode + ",resultCode = " + resultCode );
		if (requestCode == REQUEST_CAMERA){
			//添加拍照获取的图片
			mUrlList.add(mFile.getAbsolutePath());
			mPhotoEditAdapter.setList(mUrlList);
			mPhotoList.add(new MediaPhoto(mFile.getAbsolutePath(),true));
			mSurplusCount = mMaxImageCount - mUrlList.size();
			//发送一个广播,刷新相册
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(Uri.fromFile(mFile));
			sendBroadcast(intent);
			return;
		}

		switch (resultCode) {
			case Constant.ADD_PHOTO:
				if (data != null) {
					//添加从相册选择的图片
					ArrayList<MediaPhoto> list =  (ArrayList<MediaPhoto>) data.getSerializableExtra(Constant.PHOTO_LIST);
					for (int i = 0; i < list.size(); i++) {
						mUrlList.add(list.get(i).getUrl());
						mPhotoList.add(list.get(i));
					}
					mPhotoEditAdapter.setList(mUrlList);
					mSurplusCount = mMaxImageCount - mUrlList.size();
					Log.e(TAG, "onActivityResult: maxCount = " + mMaxImageCount);
				}
				break;
			case Constant.PREVIEW_PHOTO:
				if (data != null) {
					//修改经过预览后的图片
					mPhotoList = (ArrayList<MediaPhoto>) data.getSerializableExtra(Constant.PHOTO_CHECK_LIST);
					mUrlList.clear();
					for (int i = 0; i < mPhotoList.size(); i++) {
						if (mPhotoList.get(i).isCheck()) {
							mUrlList.add(mPhotoList.get(i).getUrl());
						}
					}
					mPhotoEditAdapter.setList(mUrlList);
					mSurplusCount = mMaxImageCount - mUrlList.size();
					Log.e(TAG, "onActivityResult: maxCount = " + mMaxImageCount);
				}
				break;
		}
	}

	public void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
