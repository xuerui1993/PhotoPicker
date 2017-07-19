package com.alex.photopicker.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.alex.photopicker.R;
import com.alex.photopicker.adapter.PhotoEditAdapter;
import com.alex.photopicker.constants.Constant;
import java.util.List;

/*
 *  @项目名：  PhotoPicker
 *  @包名：    com.alex.photopicker.widget
 *  @文件名:   PhotoEditView
 *  @创建者:   xuerui
 *  @创建时间:  2017/7/19 2:22
 *  @描述：    TODO
 */

public class PhotoEditView extends LinearLayout {
	private static final int REQUEST_CAMERA = 101;
	private static final int REQUEST_CODE_CAMERA = 100;
	private Context mContext;
	private PhotoEditAdapter mPhotoEditAdapter;
	private RecyclerView mRecyclerView;

	public PhotoEditView(Context context) {
		this(context, null);
	}

	public PhotoEditView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		mContext  = context;
		View view = View.inflate(context, R.layout.view_photo_edit, this);
		initView(view);
	}

	private void initView(View view) {
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

	}

	public List<String> getPhotoList(){
		return mPhotoEditAdapter.mUrlList;
	}

	public void setMaxCount(int maxCount , int cloumNumber) {
		mPhotoEditAdapter = new PhotoEditAdapter(mContext, maxCount);
		mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, cloumNumber));
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mPhotoEditAdapter);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CAMERA) {
			mPhotoEditAdapter.setTakePhotoData();
			return;
		}
		switch (resultCode) {
			case Constant.ADD_PHOTO:
				mPhotoEditAdapter.setAddPhotoData(data);
				break;
			case Constant.PREVIEW_PHOTO:
				mPhotoEditAdapter.setPreviewPhotoData(data);
				break;
		}
	}

	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_CAMERA:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					mPhotoEditAdapter.takePhoto();
				} else {
					toast("权限被拒绝");
				}
				break;
		}
	}

	public void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
}
