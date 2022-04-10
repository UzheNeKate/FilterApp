package by.cw.filterapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FiltersActivity extends AppCompatActivity {

    ImageView imageView;
    Button medianButton;
    Button sourceButton;

    Bitmap current;
    Bitmap sourceBitmap;
    Bitmap medianBitmap;
    //... все фильтры

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        imageView = findViewById(R.id.imv);
        medianButton = findViewById(R.id.btnMedian);
        sourceButton = findViewById(R.id.btnSource);

        medianButton.setOnClickListener(this::OnMedianClick);
        sourceButton.setOnClickListener(this::OnSourceClick);

        //Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
        //Uri uri = Uri.parse(getIntent().getStringExtra("img"));
        Uri uri = getIntent().getParcelableExtra("img");
        imageView.setImageURI(uri);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        sourceBitmap = bitmap;
        current = sourceBitmap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filters_menu_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                saveImage(findViewById(R.id.action_search));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveImage(View v) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0
            );
        }

        String root = Environment.getExternalStorageDirectory().toString();
        System.out.println(root);
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "filter_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            current.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Snackbar.make(
                    v, getString(R.string.successfully_saved), BaseTransientBottomBar.LENGTH_SHORT
            ).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OnMedianClick(View v) {
        if (medianBitmap != null) {
            imageView.setImageBitmap(medianBitmap);
            return;
        }
        imageView.invalidate();

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            Color rgb = Color.valueOf(sourceBitmap.getPixel(1,3));
//            float[] hsv = new float[3];
//            Color.colorToHSV(sourceBitmap.getPixel(1,3), hsv);
//        }

        Bitmap bitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.setWidth(3);

        medianBitmap = bitmap;
        imageView.setImageBitmap(medianBitmap);
//        medianBitmap = filterImage();
//        imageView.setImageBitmap(medianBitmap);
        current = medianBitmap;
    }

    private void OnSourceClick(View v) {
        imageView.setImageBitmap(sourceBitmap);
        current = sourceBitmap;
    }
}