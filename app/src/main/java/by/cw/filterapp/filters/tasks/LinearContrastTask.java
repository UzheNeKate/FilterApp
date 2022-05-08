package by.cw.filterapp.filters.tasks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

import by.cw.filterapp.FiltersActivity;
import by.cw.filterapp.R;
import by.cw.filterapp.filters.LinearContrast;

public class LinearContrastTask extends AsyncTask<Void, Void, Bitmap> {

    private static final int NOTIFY_ID = new Random().nextInt();
    private static String CHANNEL_ID = "Filter app channel";
    FiltersActivity main;

    LinearContrast filter;
    ImageView view;

    public LinearContrastTask(LinearContrast filter, ImageView view, FiltersActivity main) {
        super();
        this.filter = filter;
        this.view = view;
        this.main = main;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return filter.filterImage();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        main.linearContrastBitmap = result;
        view.setImageBitmap(result);

        createNotificationChannel();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(main, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(main.getString(R.string.notification))
                        .setContentText(main.getString(R.string.notification_processed))
                        .setPriority(Notification.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(main);
        notificationManager.notify(NOTIFY_ID, builder.build());
        Log.i("beeeeeeeeeeeeeeeeee", "*******************************tut");//убрать везде
    }

    void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId =  CHANNEL_ID;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mChannel.setDescription("This is default channel used for all other notifications");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(main);
            notificationManager.createNotificationChannel(mChannel);
        }
    }
}
