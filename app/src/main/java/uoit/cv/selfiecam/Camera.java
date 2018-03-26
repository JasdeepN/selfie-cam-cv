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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import uoit.cv.selfiecam.data.DataContent;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;


public class Camera extends Activity implements CvCameraViewListener2, snapshotFragment.OnListFragmentInteractionListener {
    private CameraBridgeViewBase mOpenCvCameraView;
    CascadeClassifier faceDetector;
    CascadeClassifier smileDetector;
    private Mat grayscaleImage;
    private int absoluteFaceSize;
    long timeout = 50;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent resultIntent = new Intent();
        // TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putExtra("some_key", "String data");
        setResult(Activity.RESULT_OK, resultIntent);
    }


    private void run(){

        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.harr_smile);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();

            faceDetector = new CascadeClassifier( mCascadeFile.getAbsolutePath() );
            //must add this line
            faceDetector.load( mCascadeFile.getAbsolutePath() );

        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

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
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);

        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = (int) (height * 0.2);
    }


    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat inputImg = inputFrame.rgba();
//        Imgproc.cvtColor(inputImg, grayscaleImage, Imgproc.COLOR_BGR2RGB);
        //do frame claculations here
        Imgproc.cvtColor(inputImg, grayscaleImage, Imgproc.COLOR_RGBA2RGB);

        MatOfRect faces = new MatOfRect();

        if (faceDetector != null) {
            faceDetector.detectMultiScale(grayscaleImage, faces, 1.1, 100, 2,
                    new Size(100, 100), new Size());
        }

        // If there are any faces found, draw a rectangle around it
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i <facesArray.length; i++) {
            if (timeout >= 50){
                onPictureTaken(inputImg);
                timeout = 0;
//                snapshotFragment.recycler.notifyDataSetChanged();

            }
            Imgproc.rectangle(inputImg, facesArray[i].tl(), facesArray[i].br(), new Scalar(0,
                    255,
                    0, 255), 3);
        }
        timeout++;


//        Log.i("test", timeout+"");

        return inputImg;
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
            File file = new File(Main.paths.get(item.id));
            DataContent.removeItem(item);
            boolean deleted = file.delete();
        }
    }

    public void onPictureTaken(Mat data) {

    //Log.i(TAG, "Saving a bitmap to file");
    // The camera preview was automatically stopped. Start it again.
        // Write the image in a file (in jpeg format)
        try {
            String currentDateandTime = sdf.format(new Date());
            String fileName = Main.folder+ "/sample_picture_" + currentDateandTime + ".jpeg";
//            Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
//            String filename = "/storage/emulated/0/DCIM/Camera/samplepass.jpg";
            Imgcodecs.imwrite(fileName,data);

            Log.d("save", fileName+" image saved");
            Bitmap bmp = null;
            Mat tmp = new Mat (720, 1280, CvType.CV_8U, new Scalar(4));
            try {
                //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
                Imgproc.cvtColor(data, tmp, Imgproc.COLOR_BGRA2RGBA, 4);
                bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(tmp, bmp);
            }
            catch (CvException e){Log.d("Exception",e.getMessage());}
            Main.fileCount++;
            DataContent.SnapshotItem new_item = new DataContent.SnapshotItem(Main.fileCount, bmp);
            DataContent.addItem(new_item);

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




}

