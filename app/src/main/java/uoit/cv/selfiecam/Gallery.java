package uoit.cv.selfiecam;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import uoit.cv.selfiecam.data.DataContent;

import static uoit.cv.selfiecam.Main.mySDCardImages;
import static uoit.cv.selfiecam.Main.paths;


public class Gallery extends AppCompatActivity implements AdapterView.OnItemClickListener {
    static int DELETE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("all paths", paths.toString());
        setContentView(R.layout.gallery_layout);
//        ListView listView = (ListView) findViewById(R.id.list);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        Main.loadImages();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("gallery", "clicked on item " + position);
                Camera.current_item = DataContent.ITEM_MAP.get(position);
                Intent intent = new Intent(getBaseContext(), GalleryView.class);
                intent.putExtra("path to jpeg", paths.get(position));
                intent.putExtra("grid position", position);
                intent.putExtra("total imgs", mySDCardImages.size());
                intent.putExtra("requestCode", 1002);

                startActivityForResult(intent, 1002);
            }
        });

        // Instance of ImageAdapter Class
        gridView.setAdapter(Main.adapter);
        Toast.makeText(this, "Click an image to open viewer.", Toast.LENGTH_LONG).show();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("gallery", "click");
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(getApplicationContext(), Camera.class);
        DataContent.clearSnapshots();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DELETE) {
//            Main.adapter.notifyDataSetChanged();
            Toast.makeText(this, "Image Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

}
