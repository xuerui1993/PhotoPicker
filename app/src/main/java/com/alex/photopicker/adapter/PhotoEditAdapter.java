package com.alex.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alex.photopicker.R;
import com.alex.photopicker.activity.PhotoPreviewActivity;
import com.alex.photopicker.bens.MediaPhoto;
import com.alex.photopicker.constants.Constant;
import com.alex.photopicker.utils.ProviderUtil;
import com.alex.photopicker.widget.PhotoBottomDialog;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 *  @项目名：  PhotoPicker
 *  @包名：    com.alex.photopicker.adapter
 *  @文件名:   PhotoEditAdapter
 *  @创建者:   xuerui
 *  @创建时间:  2017/7/18 2:22
 *  @描述：    TODO
 */

public class PhotoEditAdapter extends RecyclerView.Adapter {

	private static final String TAG = "PhotoEditAdapter";
	private static final int REQUEST_CAMERA = 101;
	private ArrayList<MediaPhoto> mPhotoList;
	private Context mContext;
	public List<String> mUrlList = new ArrayList<>();
	public File mFile;
	private Activity mActivity;
	private int mMaxImageCount;
	private int mSurplusCount;

	public void setList(List<String> list) {
		mUrlList = list;
		notifyDataSetChanged();
	}

	public PhotoEditAdapter(Context context, int maxImgCount) {
		mMaxImageCount = maxImgCount;
		mSurplusCount = maxImgCount;
		mContext = context;
		mUrlList = new ArrayList<>();
		mActivity = (Activity) context;
		mPhotoList = new ArrayList<>();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new SelectedPicViewHolder(View.inflate(mContext, R.layout.list_item_image, null));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		SelectedPicViewHolder viewHolder = (SelectedPicViewHolder) holder;
		viewHolder.setData(position);
	}


	@Override
	public int getItemCount() {
		if (getIsAdded()) {
			return mUrlList.size() + 1;
		} else {
			return mUrlList.size();
		}
	}

	private boolean getIsAdded() {
		if (mUrlList.size() < mMaxImageCount) {
			return true;
		}
		return false;
	}

	public void takePhoto() {
		Intent intent = getTakePhotoIntent();
		mActivity.startActivityForResult(intent, REQUEST_CAMERA);
	}

	public void setTakePhotoData() {
		//发送一个广播,刷新相册
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(Uri.fromFile(mFile));
		mActivity.sendBroadcast(intent);
		Log.e(TAG, "setTakePhotoData: 11111 ");
		Uri uri;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			if (mFile.exists() && mFile.getAbsolutePath().length() > 0) {
				mUrlList.add(mFile.getAbsolutePath());
				setList(mUrlList);
				mPhotoList.add(new MediaPhoto(mFile.getAbsolutePath(), true));
				Log.e(TAG, "setTakePhotoData:,mFile.length() = " +mFile.getAbsolutePath().length());
			}

		} else {
			uri = Uri.fromFile(mFile);
			if (mFile.exists() && mFile.getAbsolutePath().length() > 0) {
				//添加拍照获取的图片
				mUrlList.add(mFile.getAbsolutePath());
				setList(mUrlList);
				mPhotoList.add(new MediaPhoto(mFile.getAbsolutePath(), true));
			} else {
				Log.e(TAG, "setTakePhotoData: file为空了");
			}
		}
		mSurplusCount = mMaxImageCount - mUrlList.size();
		return;
	}

	@NonNull
	private Intent getTakePhotoIntent() {//启动手机中的  camera app , 帮组去实现 拍照 , 那么需要发意图
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		//写到 sd卡上 . 需要申请权限.
		mFile = new File(Environment.getExternalStorageDirectory(), SystemClock.elapsedRealtime() + ".jpg");
		Log.e(TAG, "getTakePhotoIntent: file = " + mFile);
		Uri uri;
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
			uri = Uri.fromFile(mFile);
		} else {
			/**
			 * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
			 * 并且这样可以解决MIUI系统上拍照返回size为0的情况
			 */
			Log.e(TAG, "provider: " + ProviderUtil.getFileProviderName(mContext) );
			uri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mContext), mFile);
			//加入uri权限 要不三星手机不能拍照
			List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			for (ResolveInfo resolveInfo : resInfoList) {
				String packageName = resolveInfo.activityInfo.packageName;
				mContext.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		return intent;
	}

	public void setAddPhotoData(Intent data) {
		if (data != null) {
			//添加从相册选择的图片
			ArrayList<MediaPhoto> list = (ArrayList<MediaPhoto>) data.getSerializableExtra(Constant.PHOTO_LIST);
			for (int i = 0; i < list.size(); i++) {
				mUrlList.add(list.get(i).getUrl());
				mPhotoList.add(list.get(i));
			}
			setList(mUrlList);
			mSurplusCount = mMaxImageCount - mUrlList.size();
			Log.e(TAG, "onActivityResult: maxCount = " + mMaxImageCount);
		}
	}

	public void setPreviewPhotoData(Intent data) {
		if (data != null) {
			//修改经过预览后的图片
			mPhotoList = (ArrayList<MediaPhoto>) data.getSerializableExtra(Constant.PHOTO_CHECK_LIST);
			mUrlList.clear();
			for (int i = 0; i < mPhotoList.size(); i++) {
				if (mPhotoList.get(i).isCheck()) {
					mUrlList.add(mPhotoList.get(i).getUrl());
				}
			}
			setList(mUrlList);
			mSurplusCount = mMaxImageCount - mUrlList.size();
			Log.e(TAG, "onActivityResult: maxCount = " + mMaxImageCount);
		}
	}

	public class SelectedPicViewHolder extends RecyclerView.ViewHolder {
		private int mPosition;
		private ImageView mIvPhoto;

		public SelectedPicViewHolder(View itemView) {
			super(itemView);
			mIvPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
			mIvPhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (getIsAdded() && mPosition == getItemCount() - 1) {
						//能够添加
						PhotoBottomDialog builder = new PhotoBottomDialog(mContext, mActivity, getTakePhotoIntent(), mSurplusCount);
						builder.getAlertDialog().show();
					} else {
						//预览
						previewPhoto(mPosition);
					}
				}
			});
		}

		/**
		 * 预览图片
		 * @param position  点击图片时的位置
		 */
		public void previewPhoto(int position) {
			Intent intent = new Intent(mContext, PhotoPreviewActivity.class);
			intent.putExtra(Constant.PHOTO_PREVIEW_LIST, mPhotoList);
			intent.putExtra(Constant.PHOTO_POSITION, position);
			intent.putExtra(Constant.TITLE_IS_GONE, false);
			intent.putExtra(Constant.MAX_COUNT, 5);
			mActivity.startActivityForResult(intent, Constant.PREVIEW_PHOTO);
		}

		public void setData(int position) {
			mPosition = position;
			if (getIsAdded() && position == getItemCount() - 1) {
				//能添加
				mIvPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
				mIvPhoto.setImageResource(R.mipmap.content_add_img_icon);
			} else {
				mIvPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
				String url = mUrlList.get(position);
				if (!TextUtils.isEmpty(url) && url != null) {
					Log.e(TAG, "setData: url = " + url );
					Glide.with(mContext).load(url).into(mIvPhoto);
				}
			}
		}
	}
}