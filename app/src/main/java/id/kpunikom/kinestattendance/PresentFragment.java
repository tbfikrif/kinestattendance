package id.kpunikom.kinestattendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import id.kpunikom.kinestattendance.member.MembersPresentArrayAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresentFragment extends Fragment {

    public static int jumlahSudahAbsen = 0;

    // Scanner
    private SurfaceView svScanner;
    private TextView tvResult;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Boolean codeScanned = false;
    private final int RequestCameraPermissionID = 1001;

    // JSON
    String id_anggota, nama;

    //RecyclerView
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

        //RecyclerView
        memberList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rvPresent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //API
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Members>> call = apiInterface.getListSudahAbsen();

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

                            //Create Vibrate
                            Vibrator vibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(50);

                            //Create Sound
                            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), uri);
                            mediaPlayer.start();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            View view = getLayoutInflater().inflate(R.layout.popup_present, null);
                            Button closeButton = view.findViewById(R.id.closeButton);
                            TextView textViewResult = view.findViewById(R.id.tvScanResult);

                            //Get JSON
                            try {
                                JSONObject object =new JSONObject(qrcodes.valueAt(0).displayValue);
                                id_anggota = object.getString("id_anggota");
                                nama = object.getString("nama");
                                DateFormat df = new SimpleDateFormat("HH:mm");
                                String currentTime = df.format(Calendar.getInstance().getTime());
                                String tempTime = "09:07";

                                textViewResult.setText(nama);

                                //Post API
                                apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                                Call<ArrayList<Members>> call = apiInterface.postHadir(id_anggota, tempTime);

                                call.enqueue(new Callback<ArrayList<Members>>() {
                                    @Override
                                    public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<ArrayList<Members>> call, Throwable t) {

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            builder.setView(view);
                            final AlertDialog dialog = builder.create();
                            dialog.show();

                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    codeScanned = false;
                                    ShareWA();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });

        call.enqueue(new Callback<ArrayList<Members>>() {
            @Override
            public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {
                memberList = response.body();
                memberArrayAdapter = new MembersPresentArrayAdapter(R.layout.listpresent, memberList);
                recyclerView.setAdapter(memberArrayAdapter);
                jumlahSudahAbsen = memberArrayAdapter.getItemCount();
            }

            @Override
            public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                //Toast.makeText(getContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Members>> call = apiInterface.getListSudahAbsen();

        call.enqueue(new Callback<ArrayList<Members>>() {
            @Override
            public void onResponse(Call<ArrayList<Members>> call, Response<ArrayList<Members>> response) {
                memberList = response.body();
                memberArrayAdapter = new MembersPresentArrayAdapter(R.layout.listpresent, memberList);
                recyclerView.setAdapter(memberArrayAdapter);
                jumlahSudahAbsen = memberArrayAdapter.getItemCount();
            }

            @Override
            public void onFailure(Call<ArrayList<Members>> call, Throwable t) {
                //Toast.makeText(getContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ShareWA(){
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
}