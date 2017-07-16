package com.alex.photopicker.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.photopicker.constants.Constant;
import com.alex.photopicker.R;
import com.alex.photopicker.adapter.ImagePickerAdapter;
import com.alex.photopicker.bens.MediaPhoto;

import java.util.ArrayList;

public class ImagePickerActivity extends AppCompatActivity implements View.OnClickListener, ImagePickerAdapter.ChangerNumberListener, ImagePickerAdapter.RecyClerItemClickListener {
	private static final String TAG = "LoucaImagePickerActivity";

	private ArrayList<MediaPhoto> mList;

	private ImagePickerAdapter mAdapter;

	private GridLayoutManager mGridLayoutManager;
	private String mTopicName;
	private TextView mTvComplete;
	private RelativeLayout mRlBack;
	private RecyclerView mRecyclerView;
	private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 0;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			initRecyclerView();
			initListener();
			super.handleMessage(msg);
		}
	};
	private TextView mTvPreview;
	private TextView mTvCancel;
	private int mMaxCount;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_picker);
		Intent intent = getIntent();
		if (intent!=null){
			mMaxCount = intent.getIntExtra(Constant.MAX_COUNT, 0);
		}
		initView();
		initData();
	}

	private void initView() {
		mTvComplete = (TextView) findViewById(R.id.tv_complete);
		mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		mTvPreview = (TextView) findViewById(R.id.tv_preview);
		mTvCancel = (TextView) findViewById(R.id.tv_cancel);
	}

	private void initListener() {
		mTvComplete.setOnClickListener(this);
		mRlBack.setOnClickListener(this);
		mTvCancel.setOnClickListener(this);
		mTvPreview.setOnClickListener(this);
		mAdapter.setOnRecyclerItemListener(this);
	}

	private void initData() {
		//检查是否有读写磁盘的权限
		if (checkHasReadExternalStorage()) {
			readSystemPhotoMedia();
		} else {
			applyPermission();//申请权限
		}
	}

	private void applyPermission() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
	}

	private boolean checkHasReadExternalStorage() {
		int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		return result == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_READ_EXTERNAL_STORAGE:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					readSystemPhotoMedia();
				} else {
					toast("权限被拒绝");
				}
				break;
		}
	}

	private void readSystemPhotoMedia() {
		new Thread() {
			@Override
			public void run() {
				mList = new ArrayList<>();
				ContentResolver resolver = getContentResolver();
				Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, null, null, MediaStore.Images.Media.DATE_MODIFIED);
				cursor.moveToPosition(-1);
				while (cursor.moveToNext()) {
					MediaPhoto photo = new MediaPhoto(cursor.getString(0), false);
					mList.add(photo);
				}
				Message msg = Message.obtain();
				mHandler.sendMessage(msg);
			}
		}.start();
	}


	private void initRecyclerView() {
		mRecyclerView.setHasFixedSize(true);
		mGridLayoutManager = new GridLayoutManager(this, 4);
		mRecyclerView.setLayoutManager(mGridLayoutManager);
		mAdapter = new ImagePickerAdapter(this, mList,mMaxCount);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.scrollToPosition(mList.size() - 1);
		mAdapter.setChangerNumberListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_complete:
				if (mAdapter.mSelectList==null||mAdapter.mSelectList.size()==0){
					toast("至少选择一张图片");
					return;
				}
				ArrayList<MediaPhoto> checkList = getCheckPhotos();
				Intent intent = new Intent();
				intent.putExtra(Constant.PHOTO_LIST, checkList);
				setResult(Constant.ADD_PHOTO, intent);
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				break;
			case R.id.rl_back:
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			case R.id.tv_cancel:
				finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				break;
			case R.id.tv_preview:
				if (mAdapter.mSelectList==null||mAdapter.mSelectList.size()==0){
					toast("至少选择一张图片");
					return;
				}
				ArrayList<MediaPhoto> checkList2 = getCheckPhotos();
				Intent previewIntent = new Intent(this, PhotoPreviewActivity.class);
				previewIntent.putExtra(Constant.PHOTO_PREVIEW_LIST, checkList2);
				previewIntent.putExtra(Constant.TITLE_IS_GONE,false);
				previewIntent.putExtra(Constant.MAX_COUNT,mMaxCount);
				startActivityForResult(previewIntent, Constant.PREVIEW_PHOTO);
				break;
		}
	}

	@NonNull
	private ArrayList<MediaPhoto> getCheckPhotos() {
		ArrayList<MediaPhoto> checkList = new ArrayList<>();
		for (String s : mAdapter.mSelectList) {
			checkList.add(mList.get(Integer.parseInt(s)));
		}
		return checkList;
	}

	@Override
	public void onNumberChanged() {
		if (mAdapter.checkNumber == 0) {
			mTvComplete.setText("完成");
		} else {
			mTvComplete.setText("完成("+mAdapter.checkNumber+")");
		}
	}

	public void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constant.PREVIEW_PHOTO){
			ArrayList<MediaPhoto> checkList = (ArrayList<MediaPhoto>) data.getSerializableExtra(Constant.PHOTO_CHECK_LIST);
			Intent intent = new Intent();
			intent.putExtra(Constant.PHOTO_LIST, checkList);
			setResult(Constant.ADD_PHOTO, intent);
			finish();
		}
	}

	@Override
	public void onRecyclerItemClick(ArrayList<MediaPhoto> list, boolean titleIsGone, int position) {
		Intent previewIntent = new Intent(this,PhotoPreviewActivity.class);
		previewIntent.putExtra(Constant.PHOTO_PREVIEW_LIST,list);
		previewIntent.putExtra(Constant.TITLE_IS_GONE, titleIsGone);
		previewIntent.putExtra(Constant.PHOTO_POSITION,position);
		previewIntent.putExtra(Constant.MAX_COUNT,mMaxCount);
		startActivityForResult(previewIntent,Constant.PREVIEW_PHOTO);
	}
}
