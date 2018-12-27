package android.test.foldinglayout2;

import android.support.v4.view.PagerAdapter;
import android.view.View;

public abstract class PreviewAdapter extends PagerAdapter {

    protected FoldingLayout mMainView;

    public final void setViewPager(FoldingLayout previewView) {
        mMainView = previewView;
    }

    public abstract int getSize();

    public abstract void setInitPosition(int initPosition);

    @Override
    public abstract int getCount();

    @Override
    public abstract boolean isViewFromObject(View view, Object object);

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void notifyAdapterDataChanged() {
        notifyDataSetChanged();
    }

    public abstract Direct getDirect();

}
