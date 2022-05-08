package by.cw.filterapp.filters;

import android.graphics.Bitmap;

public class MedianFilter {
    Bitmap srcImg;

    public MedianFilter(Bitmap srcImg) {
        this.srcImg = srcImg;
    }

    public Bitmap filterImage(int radius) {
        assert (srcImg != null);
        assert (radius > 0);

        int pixelCount = srcImg.getHeight() * srcImg.getWidth();

        int[] filtered = new int[pixelCount];
        for (int k = 0; k < pixelCount; k++) {
            int i = k % srcImg.getWidth();
            int j = k / srcImg.getWidth();
            filtered[k] = Util.getMedian(srcImg, i, j, radius);
        }
        return Util.createImageFromPixels(filtered, srcImg.getWidth(), srcImg.getHeight());
    }
}