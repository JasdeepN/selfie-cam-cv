package uoit.cv.selfiecam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

public class GalleryView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String path= extras.getString("path to jpeg");
        setContentView(R.layout.gallery_view);
        ImageView im = findViewById(R.id.current_image);
        im.setImageBitmap(loadImage(path));
    }

    private Bitmap loadImage(String path) {
        Bitmap bmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
        Log.d("image_loader", "start");
        try {
            bmap = BitmapFactory.decodeFile(path, options);
        } catch (Exception ex) {
            Log.e("LoadImageFiles", ex.getMessage());
        }
        return bmap;
    }
}
