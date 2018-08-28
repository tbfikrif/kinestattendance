package id.kpunikom.kinestattendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.refactor.lib.colordialog.PromptDialog;
import id.kpunikom.kinestattendance.api.ApiClient;
import id.kpunikom.kinestattendance.api.ApiInterface;
import id.kpunikom.kinestattendance.member.Members;
import id.kpunikom.kinestattendance.member.MembersPresentArrayAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertDialogClass extends Activity {
    private Button btnSendAlpha;
    private TextView tvNoAlpha;
    private RecyclerView recyclerView;
    private ArrayList<Members> memberList;
    private MembersPresentArrayAdapter memberArrayAdapter;

    private ApiInterface apiInterface;

    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();

        // Build the dialog
        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.dialog_layout, null);
        mAlertDlgBuilder.setCancelable(false);
        mAlertDlgBuilder.setView(mDialogView);
        mAlertDialog = mAlertDlgBuilder.create();
        mAlertDialog.show();

        // Recycler
        memberList = new ArrayList<>();
        btnSendAlpha = mDialogView.findViewById(R.id.btnSendAlpha);
        tvNoAlpha = mDialogView.findViewById(R.id.tvNoAlpha);
        recyclerView = mDialogView.findViewById(R.id.rvAlpha);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Members>> call = apiInterface.getListSudahAbsen();

        GetList(call);

        btnSendAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                finish();
                ShareWA();
            }
        });
    }

    private void GetList(Call<ArrayList<Members>> call) {
        // Set up progress before call
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMax(100);
        progressDoalog.setCancelable(false);
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.setMessage("Sedang mencoba mengambil data...");
        progressDoalog.setTitle("Loading");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDoalog.show();

        call.enqueue(new Callback<ArrayList<Members>>() {
            @Override
            public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {
                progressDoalog.dismiss();
                int no = 0;
                if (response.body().size() > 0) {
                    for (int i = 0; i < response.body().size(); i++) {
                        if (response.body().get(i).getStatusId().equals("6")) {
                            memberList.add(no, response.body().get(i));
                            memberArrayAdapter = new MembersPresentArrayAdapter(getApplicationContext(), R.layout.listpresent, memberList);
                            recyclerView.setAdapter(memberArrayAdapter);
                            no += 1;
                        }
                    }
                }
                if (memberList.size() > 0) {
                    tvNoAlpha.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                progressDoalog.dismiss();
                noStableConnectionDialog(getApplicationContext()).show();
            }
        });
    }

    private void ShareWA(){
        String employeeNames = "";
        if (memberList.size() > 0) {
            for (int i = 0; i < memberList.size(); i++) {
                employeeNames += "\n" + (i+1) + ". " + memberList.get(i).getNama();
            }
        } else {
            employeeNames = "\nTidak ada.";
        }
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "List yang Alpha Hari ini\n---"+employeeNames);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp belum diinstal.", Toast.LENGTH_SHORT).show();
        }
    }

    public PromptDialog noStableConnectionDialog(Context context) {
        PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                .setAnimationEnable(true)
                .setTitleText("Ups Koneksi ga stabil!")
                .setContentText("Yuk konekin dulu ke koneksi yang stabil. Pencet tombol Ok untuk kembali.")
                .setCancelable(false);
        promptDialog.setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                mAlertDialog.dismiss();
                finish();
            }
        });
        return promptDialog;
    }
}