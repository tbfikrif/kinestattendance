package id.kpunikom.kinestattendance.member;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ironz.unsafe.UnsafeAndroid;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.kpunikom.kinestattendance.R;
import id.kpunikom.kinestattendance.api.ApiClient;
import id.kpunikom.kinestattendance.utils.UnsafeOkHttpGlideModule;

public class MembersPresentArrayAdapter extends RecyclerView.Adapter<MembersPresentArrayAdapter.ViewHolder> {

    public static final String BASE_URL = "https://absensi.kakatu.co/dist/fotoprofile/";
    private Context context;
    private int listMemberLayout;
    private ArrayList<Members> membersList;

    // Constructor of the class
    public MembersPresentArrayAdapter(Context con, int layoutId, ArrayList<Members> membersList) {
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
        TextView clock = holder.clock;
        TextView statusId = holder.statusId;
        ImageView employeePhoto = holder.employeePhoto;

        String jamMasuk = membersList.get(listPosition).getJamMasuk();
        String[] splitHour = jamMasuk.split(":");
        int hour = Integer.parseInt(splitHour[0]);

        switch (membersList.get(listPosition).getStatusId()) {
            case "1":
                if (hour > 9) {
                    statusId.setText("Terlambat");
                    statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_late_rounded));
                    statusId.setTextColor(Color.BLACK);
                } else {
                    statusId.setText("On Time");
                    statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_ontime_rounded));
                    statusId.setTextColor(Color.WHITE);
                }
                break;
            case "2":
                statusId.setText("Tugas Kantor");
                statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_duty_rounded));
                statusId.setTextColor(Color.WHITE);
                break;
            case "3":
                statusId.setText("Sakit");
                statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_sick_rounded));
                statusId.setTextColor(Color.WHITE);
                break;
            case "4":
                statusId.setText("Izin");
                statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_permit_rounded));
                statusId.setTextColor(Color.WHITE);
                break;
            case "5":
                statusId.setText("Cuti");
                statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_vacation_rounded));
                statusId.setTextColor(Color.WHITE);
                break;
            case "6":
                statusId.setText("Alpha");
                statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_alpha_rounded));
                statusId.setTextColor(Color.WHITE);
                break;
            case "7":
                statusId.setText("Kerja Remote");
                statusId.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_remote_rounded));
                statusId.setTextColor(Color.BLACK);
                break;
            default:
                statusId.setBackgroundColor(Color.LTGRAY);
                break;
        }
        employeeName.setText(membersList.get(listPosition).getNama());
        clock.setText(membersList.get(listPosition).getJamMasuk());
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
        public TextView clock;
        public TextView statusId;
        public ImageView employeePhoto;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            employeeName = itemView.findViewById(R.id.tvEmployeeName);
            clock = itemView.findViewById(R.id.tvClock);
            statusId = itemView.findViewById(R.id.tvInformation);
            employeePhoto = itemView.findViewById(R.id.imgPhoto);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + employeeName.getText());
        }
    }
}