package id.kpunikom.kinestattendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AlphaFragment extends Fragment {

    private Button btnSendAlpha;
    private TextView tvNoAlpha;
    private RecyclerView recyclerView;
    private ArrayList<Members> memberList;
    private MembersPresentArrayAdapter memberArrayAdapter;

    private ApiInterface apiInterface;

    public AlphaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alpha, container, false);

        memberList = new ArrayList<>();
        btnSendAlpha = view.findViewById(R.id.btnSendAlpha);
        tvNoAlpha = view.findViewById(R.id.tvNoAlpha);
        recyclerView = view.findViewById(R.id.rvAlpha);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Members>> call = apiInterface.getListSudahAbsen();

        GetList(call);

        btnSendAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareWA();
            }
        });

        return view;
    }

    private void GetList(Call<ArrayList<Members>> call) {
        call.enqueue(new Callback<ArrayList<Members>>() {
            @Override
            public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {
                int no = 0;
                if (response.body().size() > 0) {
                    for (int i = 0; i < response.body().size(); i++) {
                        if (response.body().get(i).getStatusId().equals("6")) {
                            memberList.add(no, response.body().get(i));
                            memberArrayAdapter = new MembersPresentArrayAdapter(getContext(), R.layout.listpresent, memberList);
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
                noStableConnectionDialog(getContext()).show();
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
            Toast.makeText(getContext(), "Whatsapp belum diinstal.", Toast.LENGTH_SHORT).show();
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
                getActivity().finish();
            }
        });
        return promptDialog;
    }
}
