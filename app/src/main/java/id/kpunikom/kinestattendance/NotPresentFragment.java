package id.kpunikom.kinestattendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        call.enqueue(new Callback<ArrayList<Members>>() {
            @Override
            public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {
                memberList = response.body();
                memberArrayAdapter = new MembersNotPresentArrayAdapter(R.layout.listnotpresent, memberList);
                recyclerView.setAdapter(memberArrayAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                //Toast.makeText(getContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
