# PhotoPicker
- 照片选择器,相机拍照选取图片
![](https://github.com/xuerui1993/PhotoPicker/blob/master/504568939774866246.jpg)
![](https://github.com/xuerui1993/PhotoPicker/blob/master/603781355456128155.jpg)
![](https://github.com/xuerui1993/PhotoPicker/blob/master/645511239786389592.jpg)

- 1.在项目build.gradle目录下添加
	
		allprojects {
		    repositories {
			jcenter()
			maven {
			    url 'https://jitpack.io'
			}
		    }
		}

- 2.在app的build.gradle目录下添加
		
		dependencies {
			compile 'com.github.xuerui1993:PhotoPicker:v1.0'
		}

- 3.在页面布局中
		
		<com.alex.photopicker.widget.PhotoEditView
			android:id="@+id/photo_edit"
			android:layout_width="match_parent"
			android:layout_height="200dp">
		</com.alex.photopicker.widget.PhotoEditView>

- 4.在activity中找到该控件并初始化

		mPhotoEditView = (PhotoEditView) findViewById(R.id.photo_edit);
		mPhotoEditView.setMaxCount(9,4); //必须写这一行,第一个参数为最多选择几张照片,第二个参数为控件有几列

- 5.在activity中重写申请权限的方法和onActivityResult方法

		@Override
		public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[grantResults{
			mPhotoEditView.onRequestPermissionResult(requestCode,permissions,grantResults); 
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			mPhotoEditView.onActivityResult(requestCode,resultCode,data);
		}

- 6.获取照片路径
		
		List<String> urlList = mPhotoEditView.getPhotoList();

