package uoit.cv.selfiecam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import uoit.cv.selfiecam.data.DataContent;

import static uoit.cv.selfiecam.Main.adapter;
import static uoit.cv.selfiecam.Main.toggle;

public class GalleryView extends AppCompatActivity {
    private static Bitmap bmap;
    int count;
    String path;
    private ImageView im;
    private TextView name;
    private TextView dim;
    private ImageButton back;
    private ImageButton next;
    private int total_imgs;
    private int reqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setResult(1);
        path = extras.getString("path to jpeg");
        count = extras.getInt("grid position");
        total_imgs = extras.getInt("total imgs");

        if (total_imgs <= 0){
            finish();
        }

        reqCode = extras.getInt("requestCode");

        Log.d("gallery view", "total images " + total_imgs);
        setContentView(R.layout.gallery_view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        im = findViewById(R.id.current_image);
        name = findViewById(R.id.image_name);
        dim = findViewById(R.id.image_dim);

        back = findViewById(R.id.back_button);
        next = findViewById(R.id.next_button);

        if (reqCode == 1001){
            back.setEnabled(false);
            next.setEnabled(false);
            next.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);

            bmap = Camera.current_item.getImg();

            im.setImageBitmap(bmap);
            name.setText(path);
            dim.setText(bmap.getWidth() + "x" + bmap.getHeight());

        } else {
            loadImage(path);
            if (bmap != null) {
                im.setImageBitmap(bmap);
                name.setText(path);
                dim.setText(bmap.getWidth() + "x" + bmap.getHeight());
            }
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_imgs >= 2) {
                    next();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_imgs >= 2) {
                    back();
                }
            }
        });
    }

    private void back() {
        count--;
        if (count <= -1) {
            count = total_imgs-1;
        }
        Log.d("count", count+"");
        String path = Main.paths.get(count);
        loadImage(path);

        im.setImageBitmap(bmap);
        name.setText(path);
        dim.setText(bmap.getWidth() + "x" + bmap.getHeight());

    }

    private void next() {
        count++;
        if (count >= total_imgs - 1) {
            count = 0;
        }
        String path = Main.paths.get(count);
        loadImage(path);


        im.setImageBitmap(bmap);
        name.setText(path);
        dim.setText(bmap.getWidth() + "x" + bmap.getHeight());
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_button) {
            // do something here
            Log.d("menu", "clicked");
            delete(path);

//
//            this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    snapshotFragment.recycler.notifyDataSetChanged();
//                }
//            });

//            im.setImageDrawable(getResources().getDrawable(R.drawable.camera_24dp));
//            dim.setText("deleted");
//            name.setText("deleted");

        }
        return super.onOptionsItemSelected(item);
    }

    public void delete(String path) {
        File file = new File(path);
        boolean deleted = file.delete();
        if (reqCode == 1001) {
            DataContent.removeItem(Camera.current_item);
        } else {
            Main.paths.clear();
            Main.mySDCardImages.clear();
            Main.loadImages();
        }
        setResult(Gallery.DELETE);
        finish();
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
