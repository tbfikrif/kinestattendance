package id.kpunikom.kinestattendance.member;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

public class MembersArrayAdapter extends RecyclerView.Adapter<id.kpunikom.kinestattendance.member.MembersArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listMemberLayout;
    private ArrayList<id.kpunikom.absensihadir.model.Members> membersList;
    Bitmap bitmap;
    // Constructor of the class
    public MembersArrayAdapter(int layoutId, ArrayList<id.kpunikom.absensihadir.model.Members> membersList) {
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
        TextView noUrut = holder.noUrut;
        TextView nama = holder.nama;
        TextView email = holder.email;
        TextView jam_masuk = holder.jam_masuk;
        ImageView foto = holder.foto;
        int temp = listPosition + 1;

        noUrut.setText(Integer.toString(temp));
        switch (membersList.get(listPosition).getStatus_id()) {
            case "1":
                noUrut.setBackgroundColor(Color.parseColor("#00c0ef"));
                break;
            case "2":
                noUrut.setBackgroundColor(Color.parseColor("#3c8dbc"));
                break;
            case "3":
                noUrut.setBackgroundColor(Color.parseColor("#dd4b39"));
                break;
            case "4":
                noUrut.setBackgroundColor(Color.parseColor("#dd4b39"));
                break;
            case "5":
                noUrut.setBackgroundColor(Color.parseColor("#00a65a"));
                break;
            case "6":
                noUrut.setBackgroundColor(Color.BLACK);
                break;
            case "7":
                noUrut.setBackgroundColor(Color.WHITE);
                noUrut.setTextColor(Color.BLACK);
                break;
            default:
                noUrut.setBackgroundColor(Color.LTGRAY);
                break;
        }

        nama.setText(membersList.get(listPosition).getNama());
        email.setText(membersList.get(listPosition).getEmail());
        jam_masuk.setText(membersList.get(listPosition).getJam_masuk());
        String fotoURL = membersList.get(listPosition).getFoto();
        if (!fotoURL.contains(".jpg") && !fotoURL.contains(".png")){
            fotoURL = "no-profile.jpg";
        }
        new GetImageFromURL(foto).execute("http://192.168.1.32/kakatu/dist/fotoprofile/"+fotoURL);
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView noUrut;
        public TextView nama;
        public TextView email;
        public TextView jam_masuk;
        public ImageView foto;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
//            noUrut = itemView.findViewById(R.id.txNoUrut);
//            nama = itemView.findViewById(R.id.nama);
//            email = itemView.findViewById(R.id.email);
//            jam_masuk = itemView.findViewById(R.id.jam_masuk);
//            foto = itemView.findViewById(R.id.foto_profil);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + nama.getText());
        }
    }

    public void UpdateDataRecycler(ArrayList<id.kpunikom.absensihadir.model.Members> newMembers){
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