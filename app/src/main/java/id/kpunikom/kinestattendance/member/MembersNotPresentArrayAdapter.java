package id.kpunikom.kinestattendance.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;

import java.util.ArrayList;

import id.kpunikom.kinestattendance.R;
import id.kpunikom.kinestattendance.api.ApiClient;
import id.kpunikom.kinestattendance.utils.UnsafeOkHttpGlideModule;

public class MembersNotPresentArrayAdapter extends RecyclerView.Adapter<MembersNotPresentArrayAdapter.ViewHolder> {

    private Context context;
    private int listMemberLayout;
    private ArrayList<Members> membersList;

    // Constructor of the class
    public MembersNotPresentArrayAdapter(Context con, int layoutId, ArrayList<Members> membersList) {
        context = con;
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

        Glide glide = Glide.get(context);
        UnsafeOkHttpGlideModule unsafeOkHttpGlideModule = new UnsafeOkHttpGlideModule();
        unsafeOkHttpGlideModule.registerComponents(glide.getContext(),glide,glide.getRegistry());

        Glide.with(context)
                .load(new GlideUrl(ApiClient.getBaseUrl()+"dist/fotoprofile/"+photoURL))
                .into(employeePhoto);
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
}