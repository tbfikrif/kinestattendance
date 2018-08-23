package id.kpunikom.kinestattendance;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import id.kpunikom.kinestattendance.api.ApiClient;
import id.kpunikom.kinestattendance.api.ApiInterface;
import id.kpunikom.kinestattendance.member.Members;
import id.kpunikom.kinestattendance.member.MembersNotPresentArrayAdapter;
import id.kpunikom.kinestattendance.member.MembersPresentArrayAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotPresentFragment extends Fragment {

    //RecyclerView
    RecyclerView recyclerView;
    ArrayList<Members> memberList;
    MembersNotPresentArrayAdapter memberArrayAdapter;

    // Retrofit
    private ApiInterface apiInterface;

    public NotPresentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_not_present, container, false);

        //RecyclerView
        memberList = new ArrayList<>();
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
        call.enqueue(new Callback<ArrayList<Members>>() {
            @Override
            public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {
                memberList = response.body();
                memberArrayAdapter = new MembersNotPresentArrayAdapter(getContext(), R.layout.listnotpresent, memberList);
                recyclerView.setAdapter(memberArrayAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                buildDialog(getContext()).show();
            }
        });
    }

    public AlertDialog.Builder buildDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ga ada koneksi nih!");
        builder.setMessage("Yuk konekin dulu ke internet.\nPencet tombol Ok untuk kembali.");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });

        return builder;
    }
}
