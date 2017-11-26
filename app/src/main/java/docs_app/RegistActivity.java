package docs_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.lovelyhq.android.lovelydocs.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import docs_app.Utils.CheckInput;
import docs_app.Utils.FileProvider7;
import docs_app.Utils.ShowToastTip;
import docs_app.bean.User;


public class RegistActivity extends ActionBarActivity {
    private TextView regist_name;
    private TextView regist_email;
    private TextView regist_password;
    private TextView regist_second_password;
    private Button regist_btn_rest;
    private Button regist_btn_ok;
    private CircleImageView icon;

    private File mFile;

    private Bitmap mBitmap;

    String path = "";
    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    public static final int CUT_PHOTO = 3;
    final User regist_user = new User();
    //String JTpath="";
    /*private OnBooleanListener onPermissionListener;

    public void onPermissionRequests(String permission, OnBooleanListener onBooleanListener) {
        onPermissionListener = onBooleanListener;
        Log.d("MainActivity", "0");
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            Log.d("MainActivity", "1");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                //权限已有
                onPermissionListener.onClick(true);
            } else {
                //没有权限，申请一下
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        1);
            }
        }else{
            onPermissionListener.onClick(true);
            Log.d("MainActivity", "2"+ContextCompat.checkSelfPermission(this,
                    permission));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限通过
                if (onPermissionListener != null) {
                    onPermissionListener.onClick(true);
                }
            } else {
                //权限拒绝
                if (onPermissionListener != null) {
                    onPermissionListener.onClick(false);
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        Bmob.initialize(this, "8afbcaae22fc8ee6739956da9cbc2d7a");
        Init();
        regist_btn_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {

                    int REQUEST_CODE_CONTACT = 101;
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //验证是否许可权限
                    for (String str : permissions) {
                        if (RegistActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            RegistActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                            return;
                        }
                    }
                }
                String title = "选择获取图片方式";
                String[] items = new String[]{"拍照", "相册"};

                new AlertDialog.Builder(RegistActivity.this)
                        .setTitle(title)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case 0:
                                        //选择拍照
                                        pickImageFromCamera();
                                        break;
                                    case 1:
                                        //选择相册
                                        pickImageFromAlbum();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
            }
        });
        regist_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_Input_format()) {
                    UploadIcon();
                    regist_user.setUsername(regist_name.getText().toString());
                    regist_user.setPassword(regist_password.getText().toString());
                    regist_user.setEmail(regist_email.getText().toString());
                    regist_user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e == null) {
                                ShowToastTip.showTips(RegistActivity.this, "注册成功");
                                RegistActivity.this.finish();
                            } else {
                                ShowToastTip.showTips(RegistActivity.this, "注册失败" + e.getMessage());
                            }
                        }
                    });
                } else {
                    return;
                }
            }
        });
        regist_btn_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist_name.setText("");
                regist_password.setText("");
                regist_email.setText("");
                regist_second_password.setText("");
                regist_name.requestFocus();

            }
        });
    }

    public void Init() {
        regist_name = (TextView) findViewById(R.id.regist_name);
        regist_password = (TextView) findViewById(R.id.regist_password);
        regist_email = (TextView) findViewById(R.id.regist_email);
        regist_second_password = (TextView) findViewById(R.id.second_password);
        regist_btn_rest = (Button) findViewById(R.id.regist_reset);
        regist_btn_ok = (Button) findViewById(R.id.regist_ok);
        icon = (CircleImageView) findViewById(R.id.regist_icon);
    }

    public boolean check_Input_format() {
        if (CheckInput.check_textview(regist_name) && CheckInput.isEmail(regist_email) && CheckInput.check_password(regist_password)) {
            if (regist_second_password.getText().toString().equals(regist_password.getText().toString())) {
                return true;
            } else {
                regist_second_password.setError("前后密码不一致");
                regist_second_password.requestFocus();
                return false;
            }
        }
        return false;
    }

    //拍照
    public void pickImageFromCamera() {
       /* String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //赋予权限
                //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(RegistActivity.this, RegistActivity.this.getPackageName() + ".fileprovider", mFile);
                //举个栗子
                List<ResolveInfo> resInfoList = getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri*//*Uri.fromFile(mFile)*//*);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            }*/
                takePhotoNoCompress(icon);



            /*mFile = new File(file, System.currentTimeMillis() + ".jpg");
            Uri iconUri= FileProvider.getUriForFile(RegistActivity.this,RegistActivity.this.getPackageName()+ ".fileprovider",mFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, iconUri*//*Uri.fromFile(mFile)*//*);*/
           // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
           // startActivityForResult(intent, TAKE_PHOTO);
       /* } else {
            Toast.makeText(this, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }*/
    }

    //从相册获取图片
    public void pickImageFromAlbum() {
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:

                    /*startPhotoZoom(*//*Uri.fromFile(mFile)*//*FileProvider.getUriForFile(RegistActivity.this, RegistActivity.this.getPackageName() + ".fileprovider", mFile));*/
                    startPhotoZoom(FileProvider7.getUriForFile(RegistActivity.this,mFile));
                    break;
                case CHOOSE_PHOTO:

                    if (data == null || data.getData() == null) {
                        return;
                    }
                    try {
                        Bitmap bm = null;
                        Uri originalUri = data.getData();        //获得图片的uri


                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);        //显得到bitmap图片


                        //这里开始的第二部分，获取图片的路径：


                        String[] proj = {MediaStore.Images.Media.DATA};


                        //好像是android多媒体数据库的封装接口，具体的看Android文档

                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);

                        //按我个人理解 这个是获得用户选择的图片的索引值

                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        //将光标移至开头 ，这个很重要，不小心很容易引起越界

                        cursor.moveToFirst();

                        //最后根据索引值获取图片路径

                        path = cursor.getString(column_index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    startPhotoZoom(data.getData());

                    break;
                case CUT_PHOTO:

                    if (data != null) {
                        setPicToView(data);
                    }
                    break;


            }
        }

    }

    /**
     * 打开系统图片裁剪功能
     *
     * @param uri uri
     */
    private void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); //黑边
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CUT_PHOTO);
    }

    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {

            //
            //            Uri selectedImage = data.getData();
            //
            //            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //
            //            Cursor cursor = getContentResolver().query(selectedImage,
            //                    filePathColumn, null, null, null);
            //            cursor.moveToFirst();
            //
            //            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //            String picturePath = cursor.getString(columnIndex);
            //

            //这里也可以做文件上传
            mBitmap = bundle.getParcelable("data");
            // ivHead.setImageBitmap(mBitmap);
            mFile=saveBitmapFile(mBitmap,path);//保存裁剪之后的图片
            icon.setImageBitmap(mBitmap);
            //
            //            if (picturePath!=null){
            //                path = picturePath;
            //            }

            if (mFile != null) {
                path = mFile.getPath();
            }

            Toast.makeText(RegistActivity.this, "path:" + path, Toast.LENGTH_SHORT).show();

            final BmobFile bmobFile = new BmobFile(new File(path));
            //Bmob这个上传文件的貌似不成功..........................
/*
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(RegistActivity.this, "pic is success", Toast.LENGTH_SHORT).show();
                        // MyUser myUser =MyUser.getCurrentUser(MyUser.class);
                        //得到上传的图片地址
                        String fileUrl = bmobFile.getFileUrl();
                        regist_user.setIcon(bmobFile);
                        //更新图片地址
                        regist_user.update(regist_user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(RegistActivity.this, "update", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                }
            });
*/

        }
    }
    /**
     * 把bitmap 转file
     * @param bitmap
     * @param filepath
     */
    public  File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    /* public static void grantUriPermission(Context context, Intent intent, Uri saveUri) {
        *//* if (!isNeedAdapt()) {
            return;
        }*//*
        if (context == null || intent == null || saveUri == null) {
            return;
        }
        //网络路径的特殊处理，不需要权限
        if (saveUri.getScheme() != null && saveUri.getScheme().startsWith("http")) {
            //不需要授权
            return;
        }
        //1、授权(系统相册、相机、裁剪时需要)  -- 这种写法待分析
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (TextUtils.isEmpty(packageName)) {
                continue;
            }
            context.grantUriPermission(packageName, saveUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        //2、授权(安装apk时需要)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }*/
    private void UploadIcon() {
        if (mFile != null) {
            path = mFile.getPath();
        }
        Toast.makeText(RegistActivity.this, "path:" + path, Toast.LENGTH_SHORT).show();

        final BmobFile bmobFile = new BmobFile(new File(path));
        //Bmob这个上传文件的貌似不成功..........................
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(RegistActivity.this, "pic is success", Toast.LENGTH_SHORT).show();
                    // MyUser myUser =MyUser.getCurrentUser(MyUser.class);
                    //得到上传的图片地址
                    //String fileUrl = bmobFile.getFileUrl();
                    regist_user.setIcon(bmobFile);
                    //更新图片地址
                    regist_user.update(regist_user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(RegistActivity.this, "update", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==TAKE_PHOTO){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(RegistActivity.this, "Permission Allowed", Toast.LENGTH_SHORT).show();
                takePhotoNoCompress();
            }
            else {
                // Permission Denied
                Toast.makeText(RegistActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void takePhotoNoCompress() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");



            path = mFile.getAbsolutePath();

            Uri fileUri = FileProvider7.getUriForFile(this, mFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, TAKE_PHOTO);
        }
    }

    public void takePhotoNoCompress(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    TAKE_PHOTO);

        } else {
            takePhotoNoCompress();
        }
    }
}
