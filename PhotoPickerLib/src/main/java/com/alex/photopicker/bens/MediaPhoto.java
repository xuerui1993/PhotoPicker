package com.alex.photopicker.bens;

import java.io.Serializable;

/*
 *  @项目名：  PhotoPicker
 *  @包名：    com.alex.photopicker.bens
 *  @文件名:   MediaPhoto
 *  @创建者:   xuerui
 *  @创建时间:  2017/4/8 18:13
 *  @描述：    TODO
 */
public class MediaPhoto implements Serializable{
	private static final String TAG = "MediaPhoto";
	private String url;
	private boolean isCheck;

	public MediaPhoto(String url, boolean isCheck) {
		this.url = url;
		this.isCheck = isCheck;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCheck(boolean check) {
		isCheck = check;
	}

	public String getUrl() {
		return url;
	}

	public boolean isCheck() {
		return isCheck;
	}
}
