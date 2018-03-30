package uoit.cv.selfiecam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import uoit.cv.selfiecam.util.PermissionChecker;


public class Main extends AppCompatActivity {
    static public Vector<Bitmap> mySDCardImages;
    static public Hashtable<Integer, String> paths;
    static public int fileCount;
    static protected File folder = new File(Environment.getExternalStorageDirectory() +
            File.separator + "selfie-cam");
    static protected Boolean toggle = false;
    static protected File mCascadeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        new PermissionChecker(getBaseContext(), this).getPermissions();
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

    @Override
    protected void onStart() {
        Intent cam = new Intent(this, Camera.class);
        mySDCardImages = new Vector<Bitmap>();
        paths = new Hashtable<Integer, String>();
        loadImages();
        super.onStart();
        startActivity(cam);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    private boolean loadImages() {
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
        Log.d("gallery", "done loading images");
//        DataContent.load();
        return (fileCount > 0);
    }

}
