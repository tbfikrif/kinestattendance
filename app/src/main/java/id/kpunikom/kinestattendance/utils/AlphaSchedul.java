package id.kpunikom.kinestattendance.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import cn.refactor.lib.colordialog.PromptDialog;
import id.kpunikom.kinestattendance.AlertDialogClass;

public class AlphaSchedul extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();

//        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = intent.getParcelableExtra(NOTIFICATION);
//        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
//        notificationManager.notify(id, notification);

        //Launch the alertDialog.

        Intent alarmIntent = new Intent("android.intent.action.MAIN");
        alarmIntent.setClass(context, AlertDialogClass.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start the popup activity

        context.startActivity(alarmIntent);

        Toast.makeText(context, "Yuk cek yang Alpha Hari ini!", Toast.LENGTH_SHORT).show();
    }

    public PromptDialog noConnectionDialog(Context context) {
        PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                .setAnimationEnable(true)
                .setTitleText("List yang Alpha Hari ini!")
                .setContentText("1. Siapa \n 2. Siipi")
                .setCancelable(false);
        promptDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        promptDialog.setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                mediaPlayer.stop();
                dialog.dismiss();
            }
        });
        return promptDialog;
    }
}
