package id.kpunikom.kinestattendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.refactor.lib.colordialog.PromptDialog;
import id.kpunikom.kinestattendance.api.ApiClient;
import id.kpunikom.kinestattendance.api.ApiInterface;
import id.kpunikom.kinestattendance.member.Members;
import id.kpunikom.kinestattendance.member.MembersNotPresentArrayAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotPresentFragment extends Fragment {

    //RecyclerView
    private TextView tvAllPresent;
    private RecyclerView recyclerView;
    private ArrayList<Members> memberList;
    private MembersNotPresentArrayAdapter memberArrayAdapter;

    // Retrofit
    private ApiInterface apiInterface;

    public NotPresentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_not_present, container, false);

        //RecyclerView
        memberList = new ArrayList<>();
        tvAllPresent = view.findViewById(R.id.tvAllPresent);
        recyclerView = view.findViewById(R.id.rvNotPresent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //API
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Members>> call = apiInterface.getListBelumAbsen();

        GetList(call);

        return view;
    }

    private void GetList(Call<ArrayList<Members>> call) {
        // Set up progress before call
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(getContext());
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
                memberList = response.body();
                memberArrayAdapter = new MembersNotPresentArrayAdapter(getContext(), R.layout.listnotpresent, memberList);
                recyclerView.setAdapter(memberArrayAdapter);
                if (memberList.size() > 0) {
                    tvAllPresent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                progressDoalog.dismiss();
                noStableConnectionDialog(getContext()).show();
            }
        });
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
                getActivity().finish();
            }
        });
        return promptDialog;
    }
}
