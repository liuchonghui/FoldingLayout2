package android.test.foldinglayout2;

import android.content.Context;
import android.support.v4.view.PreviewViewPager;
import android.support.v4.view.ViewPager;

public abstract class PreviewPageChangeListener implements ViewPager.OnPageChangeListener {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public abstract void onPageStartGoing(Context context, PreviewViewPager.State state);

    public abstract void onPageStayOn(int oldPage, long stayDuration);

    public abstract void onRetainInstanceReady(Context context);
}
