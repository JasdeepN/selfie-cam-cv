package uoit.cv.selfiecam.data;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uoit.cv.selfiecam.Main;
import uoit.cv.selfiecam.R;
import uoit.cv.selfiecam.snapshotFragment;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DataContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<SnapshotItem> ITEMS = new ArrayList<SnapshotItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, SnapshotItem> ITEM_MAP = new HashMap<Integer, SnapshotItem>();


//    public static void load() {
//        List<SnapshotItem> ITEMS = new ArrayList<SnapshotItem>();
//        Map<String, SnapshotItem> ITEM_MAP = new HashMap<String, SnapshotItem>();
//
//        Log.d("load", Main.fileCount + " files to load");
//        // Add some sample items.
//        for (int i = 0; i < Main.fileCount; i++) {
//            addItem(createSnapshotItem(i, Main.mySDCardImages.elementAt(i)), Main.paths.get(i));
//            Log.i("data", "added image");
//        }
//    }

    public static void addItem(SnapshotItem item, String filename) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
        Main.paths.put(item.id, filename);
        Main.mySDCardImages.add(item.img);
//        snapshotFragment.recycler.notifyDataSetChanged();

    }

    public static void removeItem(SnapshotItem item) {
        ITEMS.remove(item);
        ITEM_MAP.remove(item);
//        Main.fileCount--;
        snapshotFragment.recycler.notifyDataSetChanged();
    }

    public static void clearSnapshots(){
        ITEMS.clear();
        ITEM_MAP.clear();
        snapshotFragment.recycler.notifyDataSetChanged();
    }


    public static SnapshotItem createSnapshotItem(int position, Bitmap img) {
        return new SnapshotItem(position, img);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class SnapshotItem {
        public final int id;
        public Bitmap img;

        public int getId() {
            return id;
        }

        public Bitmap getImg() {
            return img;
        }

        public SnapshotItem(int id, Bitmap img) {
            this.id = id;
            this.img = img;
        }


    }
}
