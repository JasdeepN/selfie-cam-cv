package uoit.cv.selfiecam;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Vector;

import uoit.cv.selfiecam.data.DataContent;
import uoit.cv.selfiecam.util.PermissionChecker;


public class Main extends AppCompatActivity implements snapshotFragment.OnListFragmentInteractionListener {
    static public Vector<Bitmap> mySDCardImages;
    static public int fileCount;
    static public Hashtable<Integer, String> paths;
    static protected File folder = new File(Environment.getExternalStorageDirectory() +
            File.separator + "selfie-cam");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        new PermissionChecker(getBaseContext(), this).getPermissions();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        loadImages();
    }

    @Override
    protected void onStart() {
        Intent cam = new Intent(this, Camera.class);
        startActivityForResult(cam, 0);
        super.onStart();
    }

    private boolean loadImages() {
        Log.d("imageloader", "start");
        try {
            mySDCardImages = new Vector<Bitmap>();
            paths = new Hashtable<Integer, String>();

            fileCount = 0;

            File sdDir = new File(folder + File.separator);
//            sdDir.mkdir();

            if (sdDir.listFiles() != null) {
                File[] sdDirFiles = sdDir.listFiles();
                if (sdDirFiles.length > 0) {
                    for (File singleFile : sdDirFiles) {
                        Bitmap bmap = BitmapFactory.decodeFile(singleFile.getAbsolutePath());

                        paths.put(fileCount, singleFile.getAbsolutePath());

                        mySDCardImages.add(bmap);

                        fileCount++;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("LoadImageFiles", ex.getMessage());
        }
        Log.d("imageloader", "done");
        DataContent.load();
        return (fileCount > 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
//            loadImages();
            finish();
//            Log.d("result", "ok");
//            mySDCardImages.removeAllElements();
//            paths.clear();
//            loadImages();
//            snapshotFragment.recycler.notifyDataSetChanged();
        }
    }


    public void run(View v) {
        Intent cam = new Intent(this, Camera.class);
        startActivityForResult(cam, 0);
    }

    @Override
    public void onListFragmentInteraction(DataContent.SnapshotItem item) {
        Log.d("main", "Fragment interaction");
        Log.d("delete", item.id + "");
        File file = new File(paths.get(item.id));
        boolean deleted = file.delete();
        DataContent.removeItem(item);
//        snapshotFragment.recycler.notifyDataSetChanged();
    }


}
