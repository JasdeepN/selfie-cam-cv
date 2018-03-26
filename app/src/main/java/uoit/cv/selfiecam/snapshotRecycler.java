package uoit.cv.selfiecam;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uoit.cv.selfiecam.data.DataContent;
import uoit.cv.selfiecam.snapshotFragment.OnListFragmentInteractionListener;
import uoit.cv.selfiecam.data.DataContent.SnapshotItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link uoit.cv.selfiecam.data.DataContent.SnapshotItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class snapshotRecycler extends RecyclerView.Adapter<snapshotRecycler.ViewHolder> {

    private final List<DataContent.SnapshotItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public snapshotRecycler(List<DataContent.SnapshotItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_snapshot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d("recycler", "call to recycler");
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
//        holder.mImgView.setImageDrawable(mValues.get(position).img);
        holder.mImgView.setImageBitmap((mValues.get(position).img));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //this may need to be FINAL
        public ImageView mImgView;
        public DataContent.SnapshotItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImgView = (ImageView) view.findViewById(R.id.img_view);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
