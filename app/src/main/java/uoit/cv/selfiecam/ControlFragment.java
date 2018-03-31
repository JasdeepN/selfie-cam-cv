package uoit.cv.selfiecam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.Date;

import uoit.cv.selfiecam.data.DataContent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ControlFragment.controllerFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private controllerFragmentListener mListener;
    private ToggleButton toggle;
    private ImageButton snapshot;
    private ImageButton gallery;

    public static SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");


    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View Custmv = inflater.inflate(R.layout.fragment_control, container, true);
        this.snapshot = (ImageButton) Custmv.findViewById(R.id.shoot);
        this.gallery = (ImageButton) Custmv.findViewById(R.id.gallery);
        this.toggle = (ToggleButton) Custmv.findViewById(R.id.toggleButton);

        toggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("toggle", "clicked");
                Main.toggle = !Main.toggle;
            }
        });

        gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("gallery", "clicked");
                if (Main.paths.size() > 0) {
                    Intent intent = new Intent(Custmv.getContext(), Gallery.class);
                    getActivity().finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Take a picture first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        snapshot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("snapshot", "clicked");
                onTakePicture();
            }
        });

        return Custmv;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onControlFragmentInteraction(view);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof controllerFragmentListener) {
            mListener = (controllerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface controllerFragmentListener {
        // TODO: Update argument type and name
        void onControlFragmentInteraction(View uri);
    }

    public void onTakePicture() {
        Mat frame = Camera.getCurrentFrame();
        try {
            String currentDateandTime = sdf.format(new Date());
            String fileName = Main.folder+ "/selfie_" + currentDateandTime + ".jpg";

            Log.d("save", fileName+" image saved");
            Bitmap bmp = null;
            try {
                bmp = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(frame, bmp);
            }
            catch (CvException e){Log.d("Exception",e.getMessage());}
            DataContent.SnapshotItem new_item = DataContent.createSnapshotItem(Main.fileCount,
                    bmp,fileName);
            DataContent.addItem(new_item, fileName);
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);

            Imgcodecs.imwrite(fileName, frame);

             getActivity().runOnUiThread(new Runnable() {
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
