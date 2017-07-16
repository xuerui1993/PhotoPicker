package com.alex.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alex.photopicker.R;
import com.alex.photopicker.activity.MainActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PhotoEditAdapter extends RecyclerView.Adapter {

	private static final String TAG = "PhotoEditAdapter";
	private int maxImgCount;
	private Context mContext;
	private List<String> mData = new ArrayList<>();
	private final MainActivity mActivity;

	public interface OnPhotoClickListener {
		void addPhoto();

		void previewPhoto(int position);
	}

	private OnPhotoClickListener mListener;

	public void setOnPhotoClickListener(OnPhotoClickListener listener) {
		mListener = listener;
	}

	public void setList(List<String> list) {
		mData = list;
		notifyDataSetChanged();
	}

	public PhotoEditAdapter(Context mContext, List<String> data, int maxImgCount) {
		this.mContext = mContext;
		this.maxImgCount = maxImgCount;
		mData = data;
		mActivity = (MainActivity) mContext;
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
			return mData.size() + 1;
		} else {
			return mData.size();
		}
	}

	private boolean getIsAdded() {
		if (mData.size() < maxImgCount) {
			return true;
		}
		return false;
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
						if (mListener != null) {
							mListener.addPhoto();
						}
					} else {
						//预览
						if (mListener != null) {
							mListener.previewPhoto(mPosition);
						}
					}
				}
			});
		}

		public void setData(int position) {
			mPosition = position;
			if (getIsAdded() && position == getItemCount() - 1) {
				//能添加
				mIvPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
				mIvPhoto.setImageResource(R.mipmap.content_add_img_icon);
			} else {
				mIvPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
				String url = mData.get(position);
				if (!TextUtils.isEmpty(url) && url != null) {
					Glide.with(mContext).load(url).into(mIvPhoto);
				}
			}
		}
	}
}