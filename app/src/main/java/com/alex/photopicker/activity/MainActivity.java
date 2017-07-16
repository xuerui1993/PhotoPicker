package com.alex.photopicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alex.photopicker.Constants.Constant;
import com.alex.photopicker.R;
import com.alex.photopicker.adapter.PhotoEditAdapter;
import com.alex.photopicker.bens.MediaPhoto;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PhotoEditAdapter.OnPhotoClickListener {

	private static final String TAG = "MainActivity";
	private RecyclerView mRecyclerView;
	private PhotoEditAdapter mPhotoEditAdapter;
	private int mMaxImageCount = 5; //最多选择
	private int mSurplusCount = 5;  //剩余选择
	public ArrayList<String> mUrlList = new ArrayList<>();
	public ArrayList<MediaPhoto> mPhotoList = new ArrayList<>();

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
		Intent intent = new Intent(this, ImagePickerActivity.class);
		intent.putExtra(Constant.MAX_COUNT, mSurplusCount);
		startActivityForResult(intent, Constant.ADD_PHOTO);
	}

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
		switch (resultCode) {
			case Constant.ADD_PHOTO:
				if (data != null) {
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
}
