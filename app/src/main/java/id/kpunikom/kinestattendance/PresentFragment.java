package id.kpunikom.kinestattendance;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import id.kpunikom.kinestattendance.api.ApiClient;
import id.kpunikom.kinestattendance.api.ApiInterface;
import id.kpunikom.kinestattendance.member.Members;
import id.kpunikom.kinestattendance.member.MembersAmount;
import id.kpunikom.kinestattendance.member.MembersCheck;
import id.kpunikom.kinestattendance.member.MembersPresentArrayAdapter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PresentFragment extends Fragment {

    private static final String TAG = "PresentFragment";

    public int countPresent = 0;

    // Scanner
    private SurfaceView svScanner;
    private TextView tvResult;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Boolean codeScanned = false;
    private final int RequestCameraPermissionID = 1001;

    // JSON
    String id_anggota, nama;

    // Container
    private TextView tvCountPresent;
    private TextView tvCountEmployee;
    private TextView tvDay;
    private TextView tvDate;

    // RecyclerView
    private TextView tvNoBodyPresent;
    RecyclerView recyclerView;
    ArrayList<Members> memberList;
    MembersPresentArrayAdapter memberArrayAdapter;

    // Retrofit
    private ApiInterface apiInterface;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(svScanner.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public PresentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_present, container, false);

        // Scanner
        svScanner = view.findViewById(R.id.svScanner);
        tvResult = view.findViewById(R.id.tvResult);
        barcodeDetector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector).setAutoFocusEnabled(true).setRequestedPreviewSize(480, 680).build();

        // RecyclerView
        tvNoBodyPresent = view.findViewById(R.id.tvNoBodyPresent);
        memberList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rvPresent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Container
        tvCountPresent = view.findViewById(R.id.tvCountPresent);
        tvCountEmployee = view.findViewById(R.id.tvCountEmployee);
        tvDay = view.findViewById(R.id.tvDay);
        tvDate = view.findViewById(R.id.tvDate);

        // API
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Members>> call = apiInterface.getListSudahAbsen();
        Call<MembersAmount> membersAmountCall = apiInterface.getJumlahAnggota();

        // Event Scanner
        svScanner.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(svScanner.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0 && !codeScanned) {
                    tvResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Validasi
                            codeScanned = true;

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            if (preferences.getBoolean("switch_vibrate", true)){
                                //Create Vibrate
                                Vibrator vibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(50);
                            }

                            if (preferences.getBoolean("switch_sound", true)) {
                                //Create Sound
                                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), uri);
                                mediaPlayer.start();
                            }

                            //Get JSON
                            try {
                                JSONObject object =new JSONObject(qrcodes.valueAt(0).displayValue);

                                if (object.has("id_anggota")) {
                                    id_anggota = object.getString("id_anggota");
                                    nama = object.getString("nama");
                                    //Post API
                                    Call<MembersCheck> membersCheckCall = apiInterface.checkMember(id_anggota);
                                    MembersCheck(membersCheckCall);
                                } else {
                                    NotValidDialog();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                NotValidDialog();
                            }

//                            final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // Do something after 5s = 5000ms
//                                    codeScanned = false;
//                                }
//                            }, 5000);
                        }
                    });
                }
            }
        });

        // Event API
        GetList(call);
        GetMembersAmount(membersAmountCall);

        // Current Date
        tvDay.setText(GetCurrentDay());
        tvDate.setText(GetCurrentDate());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // API
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Members>> call = apiInterface.getListSudahAbsen();

        GetList(call);
    }

    private void GetList(Call<ArrayList<Members>> call) {
        call.enqueue(new Callback<ArrayList<Members>>() {
            @Override
            public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {
                memberList = response.body();
                memberArrayAdapter = new MembersPresentArrayAdapter(getContext(), R.layout.listpresent, memberList);
                recyclerView.setAdapter(memberArrayAdapter);
                countPresent = memberArrayAdapter.getItemCount();
                if (countPresent > 0) {
                    tvNoBodyPresent.setVisibility(View.GONE);
                }
                tvCountPresent.setText(String.valueOf(countPresent));
            }

            @Override
            public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                buildDialog(getContext()).show();
            }
        });
    }

    private void GetMembersAmount(Call<MembersAmount> membersAmountCall) {
        membersAmountCall.enqueue(new Callback<MembersAmount>() {
            @Override
            public void onResponse(Call<MembersAmount> call, Response<MembersAmount> response) {
                String amount = response.body().getJumlah();
                tvCountEmployee.setText(amount);
            }

            @Override
            public void onFailure(Call<MembersAmount> call, Throwable t) {

            }
        });
    }

    private void MembersCheck(Call<MembersCheck> membersCheckCall){
        membersCheckCall.enqueue(new Callback<MembersCheck>() {
            @Override
            public void onResponse(Call<MembersCheck> call, Response<MembersCheck> response) {
                if (response.body().getResponse().equals("Belum Absen")) {
                    DateFormat df = new SimpleDateFormat("HH:mm");
                    String currentTime = df.format(Calendar.getInstance().getTime());

                    apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<ArrayList<Members>> arrayListCall = apiInterface.postHadir(id_anggota, currentTime);
                    arrayListCall.enqueue(new Callback<ArrayList<Members>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {

                        }

                        @Override
                        public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                            buildDialog(getContext()).show();
                        }
                    });

                    SuccesDialog();
                } else {
                    FailDialog();
                }
            }

            @Override
            public void onFailure(Call<MembersCheck> call, Throwable t) {
                buildDialog(getContext()).show();
            }
        });
    }

    private void ShareWA(){
        DateFormat df = new SimpleDateFormat("HH:mm");
        String currentTime = df.format(Calendar.getInstance().getTime());
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hadir\n---\nSaya, "+nama+" sudah hadir dikantor pada hari ini pukul "+currentTime);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String GetCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    private String GetCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        return simpleDateFormat.format(calendar.getTime());
    }

    private void SuccesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.popup_present, null);
        Button closeButton = view.findViewById(R.id.closeButton);
        TextView textViewResult = view.findViewById(R.id.tvScanResult);

        textViewResult.setText(nama);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanned = false;
                ShareWA();
                dialog.dismiss();
            }
        });
    }

    private void FailDialog(){
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Ups")
                .setMessage("Kamu sudah absen hari ini\nmasa lupa ;)")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        codeScanned = false;
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void NotValidDialog(){
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Ups")
                .setMessage("QRCode gak sesuai nih!")
                .setPositiveButton("Tutup", null) //Set to null. We override the onclick
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        codeScanned = false;
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
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