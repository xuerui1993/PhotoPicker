package com.alex.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alex.photopicker.R;
import com.alex.photopicker.bens.MediaPhoto;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/*
 *  @项目名：  ImagePickerAdapter
 *  @包名：    com.alex.photopicker
 *  @文件名:   ImagePickerAdapter
 *  @创建者:   xuerui
 *  @创建时间:  2017/4/8 16:43
 *  @描述：    图片选择adapter
 */
public class ImagePickerAdapter extends RecyclerView.Adapter {
	private static final String TAG = "LoucaImagePickerAdapter";
	Context mContext;
	ArrayList<MediaPhoto> mList;
	public int checkNumber = 0;
	public List<String> mSelectList;
	int mMaxCount;

	public void setList(ArrayList<MediaPhoto> list) {
		mList = list;
		notifyDataSetChanged();
	}

	public ImagePickerAdapter(Context context, ArrayList<MediaPhoto> list,int maxCount) {
		mList = list;
		mContext = context;
		mSelectList = new ArrayList<>();
		mMaxCount = maxCount;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(mContext,R.layout.item_media_imagepicker,null);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		ViewHolder viewHolder = (ViewHolder) holder;
		viewHolder.setData(mList.get(position), position);
	}

	@Override
	public int getItemCount() {
		if (mList != null) {
			return mList.size();
		}
		return 0;
	}

	public interface ChangerNumberListener {
		void onNumberChanged();
	}
	ChangerNumberListener mListeners;

	public void setChangerNumberListener(ChangerNumberListener listener) {
		if (listener != null) {
			mListeners = listener;
		}
	}

	RecyClerItemClickListener mItemListeners;
	public interface RecyClerItemClickListener{
		void onRecyclerItemClick(ArrayList<MediaPhoto> list,boolean titleIsGone,int position);
	}

	public void setOnRecyclerItemListener(RecyClerItemClickListener listener) {
		if (listener != null) {
			mItemListeners = listener;
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ImageView mIvPhoto;
		CheckBox mCheckbox;
		int mPosition;
		RelativeLayout mRlCheck;

		public ViewHolder(View itemView) {
			super(itemView);
			mRlCheck = (RelativeLayout) itemView.findViewById(R.id.rl_check);
			mIvPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
			mCheckbox = (CheckBox) itemView.findViewById(R.id.checkbox);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mItemListeners!=null){
						mItemListeners.onRecyclerItemClick(mList,true,mPosition);
					}
				}
			});
			mRlCheck.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isCheck = mList.get(mPosition).isCheck();
					if (checkNumber >= mMaxCount && !isCheck) {
						toast("最多只能选择"+mMaxCount+"张图片");
						return;
					}
					mList.get(mPosition).setCheck(!isCheck);
					if (isCheck) {
						//减少了
						checkNumber--;
						mCheckbox.setChecked(false);
						mSelectList.remove(mPosition+"");
					} else {
						//增加了
						checkNumber++;
						mCheckbox.setChecked(true);
						mSelectList.add(mPosition+"");
					}
					if (mListeners != null) {
						mListeners.onNumberChanged();
					}
				}
			});
		}

		public void setData(MediaPhoto bean, int position) {
			mCheckbox.setButtonDrawable(R.drawable.imagepicker_checkbox_selector);
			mPosition = position;
			mCheckbox.setChecked(bean.isCheck());
			Glide.with(mContext).load(bean.getUrl()).placeholder(R.drawable.content_img).into(mIvPhoto);
		}
	}

	public void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

}
