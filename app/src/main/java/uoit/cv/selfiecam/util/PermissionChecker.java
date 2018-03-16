package uoit.cv.selfiecam.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by jasdeep on 2017-11-06.
 * <p>
 * Checks and asks for permissions for FINE_LOCATION and NETWORK_STATE
 */

public class PermissionChecker {
    private static final String TAG = "permission checker";
    private static final int REQUEST_CAM = 0;
    private static final int REQUEST_STOR = 1;
    private Context context;
    private Activity activity;

    /**
     * Default constructor for PermissionChecker
     *
     * @param con Calling activity's context
     * @param act Calling activity
     */
    public PermissionChecker(Context con, Activity act) {
        this.context = con;
        this.activity = act;
    }

    /**
     * Checks if permissions are granted and if they are not asks for them
     */
    public void getPermissions() {

        /**
         * Permission request codes
         */
        int permissionCAM = android.support.v4.content.PermissionChecker.checkSelfPermission
                            (context,
                             Manifest.permission.CAMERA);

        int permissionSTOR = android.support.v4.content.PermissionChecker.checkSelfPermission
                            (context,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Log.d(TAG, "check permissions");

        /**
         * Check to see if permission's are already granted
         */
        if (permissionCAM != 0) {
            Log.d(TAG, "not granted, need to get");
            /**
             * Check if already granted
             */
            if (android.support.v4.content.PermissionChecker.checkSelfPermission(context,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.CAMERA)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ActivityCompat.requestPermissions(activity,
                                                      new String[] {Manifest.permission.CAMERA},
                                                      REQUEST_CAM);
                } else {
                    /**
                     * Ask for the permissions since they have not been granted yet
                     */
                    Log.d(TAG, "request permissions");
                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(activity,
                                                      new String[] {Manifest.permission.CAMERA},
                                                      REQUEST_CAM);

                }
            }
            /**
             * Check if Network State is granted
             */
        } else if (permissionSTOR != 0) {
            Log.d(TAG, "not granted, need to get");
            ///is granted
            if (android.support.v4.content.PermissionChecker.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ActivityCompat.requestPermissions(activity,
                                                      new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                      REQUEST_STOR);
                } else {
                    /**
                     * Ask for the permissions since they have not been granted yet
                     */
                    Log.d(TAG, "request permissions");
                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(activity,
                                                      new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                      REQUEST_STOR);

                    // MY_PERMISSIONS_REQUEST_ACCESS_INTERNET is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                //already granted do nothing
                Log.d(TAG, "already granted");
            }
        }
    }
}
