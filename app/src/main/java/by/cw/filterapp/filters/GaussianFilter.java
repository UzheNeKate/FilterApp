package by.cw.filterapp.filters;

import android.graphics.Bitmap;

public class GaussianFilter {
    Bitmap srcImg;

    public GaussianFilter(Bitmap srcImg) {
        this.srcImg = srcImg;
    }

    public Bitmap filterImage() {
        assert (srcImg != null);

        int pixelCount = srcImg.getHeight() * srcImg.getWidth();

        int[] filtered = new int[pixelCount];
        for (int k = 0; k < pixelCount; k++) {
            int i = k % srcImg.getWidth();
            int j = k / srcImg.getWidth();
            filtered[k] = Util.getMean(srcImg, i, j);
        }
        return Util.createImageFromPixels(filtered, srcImg.getWidth(), srcImg.getHeight());
    }
}
