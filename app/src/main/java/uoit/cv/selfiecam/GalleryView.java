package uoit.cv.selfiecam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class GalleryView extends AppCompatActivity {
    Bitmap bmap;
    int count;
    private ImageView im;
    private TextView name;
    private TextView dim;
    private ImageButton back;
    private ImageButton next;
    private int total_imgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        String path = extras.getString("path to jpeg");
        count = extras.getInt("grid position");
        total_imgs = extras.getInt("total imgs");

        Log.d("gallery view", "total images "+total_imgs);
        setContentView(R.layout.gallery_view);

        im = findViewById(R.id.current_image);
        name = findViewById(R.id.image_name);
        dim = findViewById(R.id.image_dim);

        back = findViewById(R.id.back_button);
        next = findViewById(R.id.next_button);

        loadImage(path);

        im.setImageBitmap(bmap);
        name.setText(path);
        dim.setText(bmap.getWidth() + "x" + bmap.getHeight());

        if (count == total_imgs-1){
            next.setEnabled(false);
            back.setEnabled(true);
        } else if (count == 0){
            next.setEnabled(true);
            back.setEnabled(false);
        }

        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                count++;
                String path = Main.paths.get(count);
                loadImage(path);
                im.setImageBitmap(bmap);
                name.setText(path);
                dim.setText(bmap.getWidth() + "x" + bmap.getHeight());
                if (count == total_imgs-1){
                    next.setEnabled(false);
                    back.setEnabled(true);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                count--;
                String path = Main.paths.get(count);
                loadImage(path);
                im.setImageBitmap(bmap);
                name.setText(path);
                dim.setText(bmap.getWidth() + "x" + bmap.getHeight());
                if (count == 0){
                    next.setEnabled(true);
                    back.setEnabled(false);
                }
            }
        });
    }

    private void loadImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
        try {
            bmap = BitmapFactory.decodeFile(path, options);
            Log.d("loading", "path = " + path);
        } catch (Exception ex) {
            Log.e("LoadImageFiles", ex.getMessage());
        }
        Log.d("single image load", "complete");
    }
}
