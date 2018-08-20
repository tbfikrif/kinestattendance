package id.kpunikom.kinestattendance.member;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import id.kpunikom.kinestattendance.R;

public class MembersNotPresentArrayAdapter extends RecyclerView.Adapter<MembersNotPresentArrayAdapter.ViewHolder> {

    public static final String BASE_URL = "http://192.168.1.32/kakatu/dist/fotoprofile/";
    private int listMemberLayout;
    private ArrayList<Members> membersList;
    Bitmap bitmap;
    // Constructor of the class
    public MembersNotPresentArrayAdapter(int layoutId, ArrayList<Members> membersList) {
        listMemberLayout = layoutId;
        this.membersList = membersList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return membersList == null ? 0 : membersList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listMemberLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView employeeName = holder.employeeName;
        ImageView employeePhoto = holder.employeePhoto;

        employeeName.setText(membersList.get(listPosition).getNama());
        String photoURL = membersList.get(listPosition).getFoto();
        if (!photoURL.contains(".jpg") && !photoURL.contains(".png")){
            photoURL = "no-profile.jpg";
        }
        new GetImageFromURL(employeePhoto).execute(BASE_URL+photoURL);
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView employeeName;
        public ImageView employeePhoto;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            employeeName = itemView.findViewById(R.id.tvEmployeeName);
            employeePhoto = itemView.findViewById(R.id.imgPhoto);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + employeeName.getText());
        }
    }

    public void UpdateDataRecycler(ArrayList<Members> newMembers){
        membersList.clear();
        membersList.addAll(newMembers);
        this.notifyDataSetChanged();
    }

    //Class for download IMAGE
    public class GetImageFromURL extends AsyncTask<String, Void, Bitmap> {
        ImageView imgV;

        public GetImageFromURL(ImageView imgV){
            this.imgV = imgV;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay = url[0];
            bitmap = null;
            try {
                InputStream srt = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(srt);
            } catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgV.setImageBitmap(bitmap);
        }
    }
}