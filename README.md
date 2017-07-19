# PhotoPicker
照片选择器,相机拍照选取图片
<br>1.在项目build.gradle目录下添加
<br>
allprojects {
    <br>repositories {
	<br>jcenter()
       <br> maven { url 'https://jitpack.io' }
    <br>}
<br>}
<br><br>
2.在app的build.gradle目录下添加
<br>dependencies {
    <br>compile 'com.github.xuerui1993:PhotoPicker:v1.0'
<br>}
<br><br>
3.在页面布局中
<br><com.alex.photopicker.widget.PhotoEditView
		<br>android:id="@+id/photo_edit"
		<br>android:layout_width="match_parent"
		<br>android:layout_height="200dp">
<br></com.alex.photopicker.widget.PhotoEditView>
<br><br>
4.在activity中找到该控件并初始化
<br>mPhotoEditView = (PhotoEditView) findViewById(R.id.photo_edit);
<br>mPhotoEditView.setMaxCount(9,4); //必须写这一行,第一个参数为最多选择几张照片,第二个参数为控件有几列
<br><br>
5.在activity中重写申请权限的方法和onActivityResult方法
<br>@Override
<br>public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
<br>mPhotoEditView.onRequestPermissionResult(requestCode,permissions,grantResults);
<br>}
<br> @Override
<br>protected void onActivityResult(int requestCode, int resultCode, Intent data) {
<br>super.onActivityResult(requestCode, resultCode, data);
<br>mPhotoEditView.onActivityResult(requestCode,resultCode,data);
<br>}
