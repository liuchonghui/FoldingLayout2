package android.test.foldinglayout2;

import android.support.v4.view.ViewPager;
import android.view.View;

public abstract class PreviewPageTransformer implements ViewPager.PageTransformer {

    public PreviewPageTransformer() {
        super();
    }

    public boolean reverseDrawingOrder() {
        return true;
    }

    protected abstract void transformAdapterItem(View view, float position);

    public boolean handleAdapterItemTransforms() {
        return false;
    }

    protected void onAdapterItemTransformPage(View view, float position) {
    }

    @Override
    public void transformPage(View view, float position) {
        transformAdapterItem(view, position);
    }

    private float getFactor(float position) {
        return -position / 2;
    }
}
