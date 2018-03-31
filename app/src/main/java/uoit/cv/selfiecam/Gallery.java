package uoit.cv.selfiecam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


import uoit.cv.selfiecam.data.DataContent;
import uoit.cv.selfiecam.util.ImageAdapter;


public class Gallery extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Vector<Bitmap> myThumbs;
    private Hashtable<Integer, String> paths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myThumbs = Main.mySDCardImages;
        paths = Main.paths;
        Log.d("all paths", paths.toString());
        setContentView(R.layout.gallery_layout);
        ListView listView = (ListView) findViewById(R.id.list);
        ImageAdapter adapter = new ImageAdapter(this, myThumbs, paths);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("gallery", "clicked on item " + position);

                Intent intent = new Intent(getBaseContext(), GalleryView.class);
                intent.putExtra("path to jpeg", paths.get(position));
                intent.putExtra("grid position", position);
                intent.putExtra("total imgs", paths.size());

                startActivityForResult(intent, 0);
            }
        });
        // Instance of ImageAdapter Class
        gridView.setAdapter(adapter);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("gallery", "click");
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(getApplicationContext(), Camera.class);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

}
