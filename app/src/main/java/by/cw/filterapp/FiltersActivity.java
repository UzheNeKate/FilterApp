package by.cw.filterapp;

import android.Manifest;
import android.app.AlertDialog;
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

import by.cw.filterapp.filters.BarGraph;
import by.cw.filterapp.filters.GaussianFilter;
import by.cw.filterapp.filters.LinearContrast;
import by.cw.filterapp.filters.MedianFilter;
import by.cw.filterapp.filters.tasks.BarGraphTask;
import by.cw.filterapp.filters.tasks.GaussianFilterTask;
import by.cw.filterapp.filters.tasks.LinearContrastTask;
import by.cw.filterapp.filters.tasks.MedianFilterTask;

public class FiltersActivity extends AppCompatActivity {

    ImageView imageView;
    Button medianButton;
    Button sourceButton;
    Button linearButton;
    Button gaussianButton;
    Button barGraphRgbButton;
    Button pressedButton;

    Bitmap current;
    public Bitmap sourceBitmap;
    public Bitmap medianBitmap;
    public Bitmap linearContrastBitmap;
    public Bitmap gaussianBitmap;
    public Bitmap barGraphRgbBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        setTitle(getString(R.string.filter_activity_title));

        imageView = findViewById(R.id.imv);
        medianButton = findViewById(R.id.btnMedian);
        sourceButton = findViewById(R.id.btnSource);
        linearButton = findViewById(R.id.btnLinear);
        gaussianButton = findViewById(R.id.btnGaussian);
        barGraphRgbButton = findViewById(R.id.btnBgRgb);

        pressedButton = sourceButton;
        sourceButton.setEnabled(false);
        sourceButton.setAlpha(0.3f);

        medianButton.setOnClickListener(this::OnMedianClick);
        linearButton.setOnClickListener(this::OnLinearClick);
        sourceButton.setOnClickListener(this::OnSourceClick);
        gaussianButton.setOnClickListener(this::OnGaussianClick);
        barGraphRgbButton.setOnClickListener(this::OnBarGraphRgbClick);

        Uri uri = getIntent().getParcelableExtra("img");
        imageView.setImageURI(uri);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        sourceBitmap = drawable.getBitmap();
        current = sourceBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setMessage(R.string.sure_to_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> FiltersActivity.this.finish())
                .setNegativeButton(R.string.no, null)
                .show();
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
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "filtered_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            current = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            current.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Snackbar.make(
                    v, getString(R.string.successfully_saved), BaseTransientBottomBar.LENGTH_SHORT
            ).show();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(
                    v, getString(R.string.wrong_save), BaseTransientBottomBar.LENGTH_SHORT
            ).show();
        }
    }

    private void OnMedianClick(View v) {
        pressedButton.setAlpha(1);
        pressedButton.setEnabled(true);
        pressedButton = medianButton;
        medianButton.setAlpha(0.3f);
        medianButton.setEnabled(false);

        if (medianBitmap != null) {
            imageView.setImageBitmap(medianBitmap);
            return;
        }
        imageView.invalidate();

        MedianFilter mf = new MedianFilter(sourceBitmap);
        MedianFilterTask task = new MedianFilterTask(mf, imageView, FiltersActivity.this);
        task.execute();
    }

    private void OnLinearClick(View v){
        pressedButton.setAlpha(1);
        pressedButton.setEnabled(true);
        pressedButton = linearButton;
        linearButton.setAlpha(0.3f);
        linearButton.setEnabled(false);

        if (linearContrastBitmap != null) {
            imageView.setImageBitmap(linearContrastBitmap);
            return;
        }
        imageView.invalidate();

        LinearContrast lc = new LinearContrast(sourceBitmap);
        LinearContrastTask task = new LinearContrastTask(lc, imageView, FiltersActivity.this);
        task.execute();
    }

    private void OnBarGraphRgbClick(View view) {
        pressedButton.setAlpha(1);
        pressedButton.setEnabled(true);
        pressedButton = barGraphRgbButton;
        barGraphRgbButton.setAlpha(0.3f);
        barGraphRgbButton.setEnabled(false);

        if (barGraphRgbBitmap != null) {
            imageView.setImageBitmap(barGraphRgbBitmap);
            return;
        }
        imageView.invalidate();

        BarGraph bg = new BarGraph(sourceBitmap);
        BarGraphTask task = new BarGraphTask(bg, imageView, FiltersActivity.this);
        task.execute();
    }

    private void OnGaussianClick(View view) {
        pressedButton.setAlpha(1);
        pressedButton.setEnabled(true);
        pressedButton = gaussianButton;
        gaussianButton.setAlpha(0.3f);
        gaussianButton.setEnabled(false);

        if (gaussianBitmap != null) {
            imageView.setImageBitmap(gaussianBitmap);
            return;
        }
        imageView.invalidate();

        GaussianFilter gf = new GaussianFilter(sourceBitmap);
        GaussianFilterTask task = new GaussianFilterTask(gf, imageView, FiltersActivity.this);
        task.execute();
    }

    private void OnSourceClick(View v) {
        pressedButton.setAlpha(1);
        pressedButton.setEnabled(true);
        pressedButton = sourceButton;
        sourceButton.setAlpha(0.3f);
        sourceButton.setEnabled(false);

        imageView.setImageBitmap(sourceBitmap);
    }
}