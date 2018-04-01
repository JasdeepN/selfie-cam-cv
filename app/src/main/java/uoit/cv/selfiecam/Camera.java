package uoit.cv.selfiecam;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import uoit.cv.selfiecam.data.DataContent;



public class Camera extends Activity implements CvCameraViewListener2, snapshotFragment
        .OnListFragmentInteractionListener, ControlFragment.controllerFragmentListener {

    private CascadeClassifier feature_detector;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat grayscaleImage;
    private int imgX;
    private int imgY;
    private int absoluteFaceSize;
    static Mat currentImg;
    int backButtonCount = 0;
    private CountDownTimer timer;
    public static SimpleDateFormat sdf = new SimpleDateFormat("YY-MM-dd-HH-mm-ss");
    public static DataContent.SnapshotItem current_item;
    private boolean timeout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Intent resultIntent = new Intent();
        // TODO Add extras or a data URI to this intent as appropriate
        // resultIntent.putExtra("some_key", "String data");
        Toast.makeText(this, "Tap Detect to begin.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart(){
        timer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                timeout = false;
            }
        }.start();

        super.onStart();
    }


    private void run(){
        feature_detector = new CascadeClassifier( Main.mCascadeFile.getAbsolutePath() );
        //must add this line
        feature_detector.load( Main.mCascadeFile.getAbsolutePath() );
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.cam1);
        // front camera on tablet
        mOpenCvCameraView.setCameraIndex(1);
        //front camera for emulator
//        mOpenCvCameraView.setCameraIndex(0);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
//            DataContent.clearSnapshots();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        currentImg = new Mat(height,width, CvType.CV_8UC4);
        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = (int) (height * 0.07);
        imgX = width;
        imgY = height;
    }


    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat inputImg = inputFrame.rgba();
//        Imgproc.cvtColor(inputImg, grayscaleImage, Imgproc.COLOR_BGR2GRAY);
//        do frame claculations here
        grayscaleImage = inputFrame.gray();
//        Imgproc.cvtColor(inputImg, currentImg, Imgproc.COLOR_BGR2RGB);
        currentImg = inputImg;
        MatOfRect faces = new MatOfRect();
        if (Main.toggle) {
            if (feature_detector != null) {
                feature_detector.detectMultiScale(grayscaleImage, faces, 1.1, 200, 2,
                        new Size(100, 70), new Size());
            }

            // If there are any faces found, draw a rectangle around it
            Rect[] facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++) {
                if (!timeout) {
                    onPictureTaken();
                    timeout = true;
                    timer.start();

                }
                Imgproc.rectangle(inputImg, facesArray[i].tl(), facesArray[i].br(), new Scalar(0,
                        255,
                        0, 255), 3);
            }
        }
        return inputImg;
    }

    public static Mat getCurrentFrame(){
        return currentImg;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    run();
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this,
                mLoaderCallback);
    }
    @Override
    public void onListFragmentInteraction(DataContent.SnapshotItem item) {
        Log.d("camera", "Fragment interaction");

        Log.d("paths", Main.paths + "");
        Log.d("id", item.id + "");

        if (item != null) {
//            File file = new File(Main.paths.get(item.id));
//            DataContent.removeItem(item);
//            Main.paths.remove(item.id);
//            Main.mySDCardImages.remove(item.id);
//            boolean deleted = file.delete();
            Intent intent = new Intent(this, GalleryView.class);
            intent.putExtra("path to jpeg", item.path);
            intent.putExtra("total imgs", 1);
            intent.putExtra("requestCode", 1001);
            current_item = item;
            this.finish();
            startActivityForResult(intent, 1);


        }
    }

    public void onPictureTaken() {
        try {
            String currentDateandTime = sdf.format(new Date());
            String fileName = Main.folder+ "/selfie_" + currentDateandTime + ".jpg";

            Log.d("save", fileName+" image saved");
            Bitmap bmp = null;
            try {
                bmp = Bitmap.createBitmap(currentImg.cols(), currentImg.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(currentImg, bmp);
            }
            catch (CvException e){Log.d("Exception",e.getMessage());}
            DataContent.SnapshotItem new_item = DataContent.createSnapshotItem(Main.fileCount,
                    bmp, fileName);
            DataContent.addItem(new_item, fileName);
            Imgproc.cvtColor(currentImg, currentImg, Imgproc.COLOR_BGR2RGB);

            Imgcodecs.imwrite(fileName, currentImg);

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    snapshotFragment.recycler.notifyDataSetChanged();
                }
            });

            Log.d("picture taken", "updated dataset");

        } catch (Exception e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }

    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }



    @Override
    public void onControlFragmentInteraction(View uri) {
        Log.d("controller", "clicked"+uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Gallery.DELETE) {
            if (DataContent.ITEMS.size() != 0) {
                current_item = DataContent.ITEMS.get(0);
            } else {
                current_item = null;
            }
        }
    }
}

