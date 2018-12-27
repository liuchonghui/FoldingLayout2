package android.test.foldinglayout2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class FolderAdapter extends PreviewAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<Node> mItems;
    protected int initPosition;

    public FolderAdapter(Context context, List<Node> items) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mItems = items;
    }

    @Override
    public int getSize() {
        return mItems.size();
    }

    @Override
    public void setInitPosition(int initPosition) {
        this.initPosition = initPosition;
    }

    @Override
    public int getCount() {
        return getSize();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Direct getDirect() {
        return Direct.FLAT;
    }

    public int getPositionInList(int position) {
        return position % getSize();
    }

    protected Node getPosItem(int pos) {
        return mItems.get(pos);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int pos = getPositionInList(position);
        final Node item = getPosItem(pos);
        ViewGroup viewgroup = (ViewGroup) mInflater.inflate(R.layout.item_layout, null);

        adjustViewGroup(viewgroup, position, pos, item);

        container.addView(viewgroup, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        viewgroup.setTag(item);
        return viewgroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        view.setTag(null);
        object = null;
    }

    public void adjustViewGroup(ViewGroup viewgroup, int position, int pos, Node item) {
        if (!TextUtils.isEmpty(item.background)) {
            int roundRadius = 18;
            int fillColor = Color.parseColor(item.background);

            GradientDrawable gd = new GradientDrawable();
            gd.setColor(fillColor);
            gd.setCornerRadius(roundRadius);

            View bg = viewgroup.findViewById(R.id.item_bg);
            bg.setBackgroundDrawable(gd);
        }
        if (!TextUtils.isEmpty(item.title)) {
            TextView title = (TextView) viewgroup.findViewById(R.id.item_text);
            title.setText(item.title);
        }
    }
}
