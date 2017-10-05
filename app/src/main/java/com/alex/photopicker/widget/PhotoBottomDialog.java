package com.alex.photopicker.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.alex.photopicker.R;
import com.alex.photopicker.activity.ImagePickerActivity;
import com.alex.photopicker.constants.Constant;

/*
 *  @项目名：  PhotoPicker
 *  @包名：    com.alex.photopicker.widget
 *  @文件名:   PhotoBottomDialog
 *  @创建者:   xuerui
 *  @创建时间:  2017/7/19 1:09
 *  @描述：    TODO
 */
public class PhotoBottomDialog extends AlertDialog.Builder implements View.OnClickListener {
	private static final int REQUEST_CODE_CAMERA = 100;
	private static final int REQUEST_CAMERA = 101;
	private static final String TAG = "PhotoBottomDialog";
	private int mSurplusCount;
	private Intent mIntent;
	private Activity mActivity;
	private Context mContext;
	private Button mBtnGallery;
	private Button mBtnTakePicture;
	private Button mBtnCancel;
	private AlertDialog mAlertDialog;

	public PhotoBottomDialog(@NonNull Context context, Activity activity, Intent intent, int surplusCount) {
		this(context, R.style.ActionSheetDialogStyle);
		mActivity = activity;
		mIntent = intent;
		mSurplusCount = surplusCount;
	}

	public PhotoBottomDialog(@NonNull Context context, @StyleRes int themeResId) {
		super(context, themeResId);
		mContext = context;
		View view = View.inflate(context, R.layout.dialog_photo_bottom, null);
		setView(view);
		mAlertDialog = create();
		Window window = mAlertDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = window.getAttributes();
		WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = m.getDefaultDisplay();
		lp.width = (int) (d.getWidth() * 0.95);
		lp.y = 30;
		window.setAttributes(lp);
		initView(view);
		initListener();
	}

	public AlertDialog getAlertDialog() {
		return mAlertDialog;
	}

	private void initListener() {
		mBtnCancel.setOnClickListener(this);
		mBtnGallery.setOnClickListener(this);
		mBtnTakePicture.setOnClickListener(this);
	}

	private void initView(View view) {
		mBtnGallery = (Button) view.findViewById(R.id.btn_gallery);
		mBtnTakePicture = (Button) view.findViewById(R.id.btn_take_picture);
		mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
	}

	/**
	 * 检查相机权限是否开启
	 * @return true为开启, false为未开启
	 */
	private boolean checkHasCameraPermission() {
		int result = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
		int storage_result = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
		Log.e(TAG, "checkHasCameraPermission: result = " + result +",storage_result = " +storage_result);
		return result == PackageManager.PERMISSION_GRANTED && storage_result == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_cancel:
				mAlertDialog.dismiss();
				break;
			case R.id.btn_take_picture:
				mAlertDialog.dismiss();
				if (checkHasCameraPermission() && checkHasReadExternalStorage()) {
					mActivity.startActivityForResult(mIntent, REQUEST_CAMERA);
				} else {
					//申请相机权限
					ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA);
				}
				break;
			case R.id.btn_gallery:
				mAlertDialog.dismiss();
				addPhoto();
				break;
		}
	}

	public void addPhoto() {
		Intent intent = new Intent(mContext, ImagePickerActivity.class);
		intent.putExtra(Constant.MAX_COUNT, mSurplusCount);
		mActivity.startActivityForResult(intent, Constant.ADD_PHOTO);
	}

	private boolean checkHasReadExternalStorage() {
		int result = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
		return result == PackageManager.PERMISSION_GRANTED;
	}

}
