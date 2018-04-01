package uoit.cv.selfiecam;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import uoit.cv.selfiecam.util.ImageAdapter;


public class Main extends AppCompatActivity {
    static public Vector<Bitmap> mySDCardImages;
    static public Hashtable<Integer, String> paths;
    static public int fileCount;
    static protected File folder = new File(Environment.getExternalStorageDirectory() +
            File.separator + "selfie-cam");
    static protected Boolean toggle = false;
    static protected File mCascadeFile;
    public static ImageAdapter adapter;
    public static int min_neig;
    public static int scale_factor;
    public static int det_width;
    public static int det_height;
    public static long timer;


     static {
        min_neig = 0;
    }

    public boolean permissionsGranted() {

        boolean allPermissionsGranted = true;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            boolean hasWriteExternalPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            boolean hasReadExternalPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            boolean hasCameraPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
            if (!hasWriteExternalPermission || !hasReadExternalPermission || !hasCameraPermission) {
                allPermissionsGranted = false;
            }
        }
        return allPermissionsGranted;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        if (!permissionsGranted()) {
            String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            mySDCardImages = new Vector<Bitmap>();
            paths = new Hashtable<Integer, String>();
            adapter = new ImageAdapter(this, mySDCardImages, paths);
            loadImages();

            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                // Copy the resource into a temp file so OpenCV can load it
                InputStream is = getResources().openRawResource(R.raw.harr_smile);
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                mCascadeFile = new File(cascadeDir, "lbpcascade_file.xml");
                FileOutputStream os = new FileOutputStream(mCascadeFile);


                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                is.close();
                os.close();

            } catch (Exception e) {
                Log.e("OpenCVActivity", "Error loading cascade", e);
            }

        }
    }


    @Override
    protected void onStart() {
        Intent cam = new Intent(this, Camera.class);
        startActivity(cam);
        super.onStart();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    public static boolean loadImages() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Log.d("image_loader", "start");
        try {
            fileCount = 0;
            paths.clear();
            mySDCardImages.clear();

            File sdDir = new File(folder + File.separator);
//            sdDir.mkdir();

            if (sdDir.listFiles() != null) {
                File[] sdDirFiles = sdDir.listFiles();
                if (sdDirFiles.length > 0) {
                    for (File singleFile : sdDirFiles) {
                        Bitmap bmap = BitmapFactory.decodeFile(singleFile.getAbsolutePath(), options);

                        paths.put(fileCount, singleFile.getAbsolutePath());

                        mySDCardImages.add(bmap);

                        fileCount++;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("LoadImageFiles", ex.getMessage());
        }
        Log.d("Main", "done loading img thumbs");
//        DataContent.load();
        adapter.notifyDataSetChanged();

        return (fileCount > 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                boolean startActivity = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        String permission = permissions[i];
                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            Toast.makeText(this, "Enable required permissions", Toast.LENGTH_LONG).show();
                            startActivity = false;
                            break;
                        } else {
                            Toast.makeText(this, "Enable required permissions", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
                if (startActivity) {
                    this.finish();
                    Intent cam = new Intent(this, Camera.class);
                    startActivity(cam);
                }
            }
        }
    }
}
