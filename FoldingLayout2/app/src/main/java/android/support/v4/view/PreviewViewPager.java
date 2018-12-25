package android.support.v4.view;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.test.foldinglayout2.FoldingLayout;
import android.test.foldinglayout2.PreviewPageTransformer;
import android.test.foldinglayout2.R;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class PreviewViewPager extends ViewPager {

    private float mInitialTouchY, mInitialTouchX;
    private ViewConfiguration mViewConfiguration;
    private FoldingLayout mMainView;
    private OnPageStartGoingListener mOnPageStartGoingListener;
    private OnPageStayListener mOnPageStayListener;
    private OnRetainInstanceListener mOnRetainInstanceListener;
    private boolean DEBUG = false;
    private boolean mTouchSlopEnable = true;
    private boolean mTouchSlopHandle = false;

    public PreviewViewPager(Context context) {
        super(context);
        setId(R.id.preview_viewpager);
        mViewConfiguration = ViewConfiguration.get(context);
    }

    public void setMainView(FoldingLayout view) {
        mMainView = view;
    }

    public interface OnPageStartGoingListener {
        void onPageStartGoing(Context context, State state);
    }

    public void setOnPageStartGoingListener(OnPageStartGoingListener listener) {
        mOnPageStartGoingListener = listener;
    }

    public interface OnPageStayListener {
        void onPageStayOn(int oldPage, long stayDuration);
    }

    public void setOnPageStayListener(OnPageStayListener listener) {
        mOnPageStayListener = listener;
    }

    public interface OnRetainInstanceListener {
        void onRetainInstanceReady(Context context);
    }

    public void setOnRetainInstanceListener(OnRetainInstanceListener listener) {
        mOnRetainInstanceListener = listener;
    }

    public View[] listChild() {
        View[] children = null;
        int count = getChildCount();
        if (count > 0) {
            children = new View[count];
            for (int i = 0; i < count; i++) {
                children[i] = getChildAt(i);
            }
        }
        return children;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mTouchSlopHandle) {
            return true;
        }
        if (!mTouchSlopEnable) {
            return false;
        }
        if (getChildCount() == 0) {
            return false;
        }
        if (getChildCount() == 1 && ev.getAction() == MotionEvent.ACTION_MOVE) {
            return false;
        }
//        if (getChildCount() == 2) {
//            if (mMainView != null) {
//                boolean hasShowHint = mMainView.hasShowHint();
//                Log.d("ENV", "viewpager2children|hasShowHint[" + hasShowHint + "]");
//                if (!hasShowHint) {
//                    return false;
//                }
//            }
//        }
        if (isFakeDragging()) {
            return true;
        }
        if (FakeDrag.ORIGINAL.ordinal() < mFakeDrag.ordinal()
                && FakeDrag.AFTER_END.ordinal() > mFakeDrag.ordinal()) {
            return true;
        }
        boolean ret;
        try {
            ret = super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mTouchSlopEnable) {
//            mMainView.toggleActions();
            return false;
        }
        if (getChildCount() == 0) {
            return false;
        }
        if (getChildCount() == 1 && ev.getAction() == MotionEvent.ACTION_MOVE) {
            return false;
        }
//        if (getChildCount() == 2) {
//            if (mMainView != null) {
//                boolean hasShowHint = mMainView.hasShowHint();
//                Log.d("ENV", "viewpager2children|hasShowHint(" + hasShowHint + ")");
//                if (!hasShowHint) {
//                    return false;
//                }
//            }
//        }
        if (isFakeDragging()) {
            return true;
        }
        if (FakeDrag.ORIGINAL.ordinal() < mFakeDrag.ordinal()
                && FakeDrag.AFTER_END.ordinal() > mFakeDrag.ordinal()) {
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mInitialTouchX = ev.getRawX();
            mInitialTouchY = ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            final int touchSlop = mViewConfiguration.getScaledTouchSlop();
            if (Math.abs(mInitialTouchX - ev.getRawX()) < touchSlop &&
                    Math.abs(mInitialTouchY - ev.getRawY()) < touchSlop) {
                if (isCurrentPreviewIdle()) {
//                    mMainView.toggleActions();
                    return false;
                }
            }
        }
        boolean ret;
        try {
            ret = super.onTouchEvent(ev);
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public void setPageTransformer(PreviewPageTransformer transformer) {
        boolean reverseDrawingOrder = true;
        if (transformer != null) {
            reverseDrawingOrder = transformer.reverseDrawingOrder();
        }
        super.setPageTransformer(reverseDrawingOrder, transformer);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (null != getAdapter()) {
            if (isFakeDragging()) {
                endFakeDrag();
            }
        }
        super.setAdapter(adapter);
        timestamp = System.currentTimeMillis();
    }

    public enum State {
        IDLE,
        GOING_LEFT,
        GOING_RIGHT,
    }

    private State mState;
    private State lastState;
    private State offsetState;
    private boolean resetIdel;
    private int oldPage;
    private long stayDuration = 0L;
    private long timestamp = 0L;

    @Override
    protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mState == State.IDLE && positionOffset > 0) {
            oldPage = getCurrentItem();
            mState = position == oldPage ? State.GOING_RIGHT : State.GOING_LEFT;
        }
        boolean goingRight = position == oldPage;
        if (mState == State.GOING_RIGHT && !goingRight) {
            mState = State.GOING_LEFT;
        } else if (mState == State.GOING_LEFT && goingRight) {
            mState = State.GOING_RIGHT;
        }
        float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;
        if (effectOffset == 0) {
            mState = State.IDLE;
        }
        if (offsetState != mState) {
            offsetState = mState;
            if (mOnPageStartGoingListener != null) {
                mOnPageStartGoingListener.onPageStartGoing(getContext(), offsetState);
            }
        }

        super.onPageScrolled(position, positionOffset, positionOffsetPixels);

        if (State.IDLE == mState) {
            if (oldPage != position && State.IDLE != lastState) {
                long current = System.currentTimeMillis();
                stayDuration = current - timestamp;
                timestamp = current;
                if (mOnPageStayListener != null) {
                    mOnPageStayListener.onPageStayOn(oldPage, stayDuration);
                }
            }
            if (notifyDataSetChanged) {
                if (getChildCount() == 1 && position == 0) {
                    resetPosition = position;
                }
                if (resetPosition == position) {
                    resetPosition = -1;
                    mMainView.dispatchOnScrollStateChanged(SCROLL_STATE_IDLE);
                    notifyDataSetChanged = false;
                }
            }
        }
        if (resetIdel) {
            resetIdel = false;
            if (oldPage != position && State.IDLE != lastState) {
                long current = System.currentTimeMillis();
                stayDuration = current - timestamp;
                timestamp = current;
                if (mOnPageStayListener != null) {
                    mOnPageStayListener.onPageStayOn(position, stayDuration);
                }
            }
        }
        lastState = mState;
    }

    public void enterCurrentScreen() {
        long current = System.currentTimeMillis();
        stayDuration = current - timestamp;
        timestamp = current;
    }

    public void leaveCurrentScreen() {
        long current = System.currentTimeMillis();
        stayDuration = current - timestamp;
        timestamp = current;
        if (mOnPageStayListener != null) {
            mOnPageStayListener.onPageStayOn(getCurrentItem(), stayDuration);
        }
    }

    public void resetIdel() {
        resetIdel = true;
        smoothScrollTo(getScrollX(), getScrollY(), 0);
        mState = State.IDLE;
        offsetState = State.IDLE;
    }

    private enum FakeDrag {
        ORIGINAL,
        BEFORE_BEGIN,
        AFTER_BEGIN,
        BEFORE_END,
        AFTER_END,
    }

    private FakeDrag mFakeDrag = FakeDrag.ORIGINAL;

    @Override
    public boolean beginFakeDrag() {
        if (getChildCount() == 0) {
            return false;
        }
        mFakeDrag = FakeDrag.BEFORE_BEGIN;
        boolean flag = super.beginFakeDrag();
        mFakeDrag = FakeDrag.AFTER_BEGIN;
        return flag;
    }

    @Override
    public void endFakeDrag() {
        mFakeDrag = FakeDrag.BEFORE_END;
        super.endFakeDrag();
        mFakeDrag = FakeDrag.AFTER_END;
    }

    @Override
    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        if (smoothScroll && always && getChildCount() > 0
                && item == getCurrentItem() && velocity > 0) {
            Log.d("RECORD", "useless smooth scroll");
            Log.d("ENV", "useless smooth scroll");
        }
        super.setCurrentItemInternal(item, smoothScroll, always, velocity);
        if (!always && getCurrentItem() == 0 && item == getCurrentItem()) {
            mMainView.onSelectInitialItem();
        }
    }

    /**
     * WARNNING: only in PreviewViewPager we can do this because no other entrance invoking onHiddenChanged
     */
    public void onCurrentItemHiddenChanged(boolean hidden) {
        ItemInfo info = super.infoForPosition(getCurrentItem());
        if (info != null) {
            if (scrollHint) {
                scrollHint = false;
                if (!hidden) {
                    if (mOnRetainInstanceListener != null) {
                        mOnRetainInstanceListener.onRetainInstanceReady(mMainView.getContext());
                    }
                    if (info.object instanceof Fragment) {
                        boolean retain = ((Fragment) info.object).getRetainInstance();
                        ((Fragment) info.object).setRetainInstance(retain);
                    }
                }

            } else if (notifyDataSetChanged) {
                notifyDataSetChanged = false;
                if (!hidden) {
                    if (mOnRetainInstanceListener != null) {
                        mOnRetainInstanceListener.onRetainInstanceReady(mMainView.getContext());
                    }
                    if (info.object instanceof Fragment) {
                        boolean retain = ((Fragment) info.object).getRetainInstance();
                        ((Fragment) info.object).setRetainInstance(retain);
                    }
                }

            } else if (!hidden) {
                if (mOnRetainInstanceListener != null) {
                    mOnRetainInstanceListener.onRetainInstanceReady(mMainView.getContext());
                }
                if (info.object instanceof Fragment) {
                    boolean retain = ((Fragment) info.object).getRetainInstance();
                    ((Fragment) info.object).setRetainInstance(retain);
                }
            }
            if (info.object instanceof Fragment) {
                ((Fragment) info.object).onHiddenChanged(hidden);
            }
        }
    }

    public boolean isCurrentPreviewIdle() {
        int[] parentLocation = new int[2];
        getLocationOnScreen(parentLocation);
        View currView = getCurrentView();
        View lastView = getViewByPosition(getCurrentItem() - 1);
        if (currView != null) {
            if (lastView != null) {
                int[] location = new int[2];
                lastView.getLocationOnScreen(location);
                Rect lastRect = new Rect(location[0], location[1],
                        location[0] + lastView.getMeasuredWidth(),
                        location[1] + lastView.getMeasuredHeight());
                currView.getLocationOnScreen(location);
                Rect currRect = new Rect(location[0], location[1],
                        location[0] + currView.getMeasuredWidth(),
                        location[1] + currView.getMeasuredHeight());
                if (location[0] == parentLocation[0] && location[1] == parentLocation[1]) {
                    // 判断与父view顶点重合，可兼容分屏模式
                    return !Rect.intersects(lastRect, currRect);
                }
            } else {
                int[] location = new int[2];
                currView.getLocationOnScreen(location);
                if (location[0] == parentLocation[0] && location[1] == parentLocation[1]) {
                    // 判断与父view顶点重合，可兼容分屏模式
                    return true;
                }
            }
        }
        return false;
    }

//    public void onCurrentItemAccessoryEvent(WallpaperInfo wInfo, PreviewAccessoryType type, PreviewAccessoryEvent event) {
//        ItemInfo info = super.infoForPosition(getCurrentItem());
//        if (info != null && info.object instanceof BasePreviewFragment) {
//            ((BasePreviewFragment) info.object).onAccessoryEvent(wInfo, type, event);
//        }
//    }

    boolean scrollHint = false;

    public void onScrollHint() {
        scrollHint = true;
    }

    private boolean isSmall(float positionOffset) {
        return Math.abs(positionOffset) < 0.0001;
    }

    public boolean isStateIdle() {
        return State.IDLE == mState;
    }

    public State getState() {
        return mState;
    }

    public void setTouchSlopEnable(boolean enable) {
        mTouchSlopEnable = enable;
    }

    public boolean touchSlopEnable() {
        return mTouchSlopEnable;
    }

    public void setTouchSlopHandle(boolean handle) {
        mTouchSlopHandle = handle;
    }

    @Override
    ItemInfo infoForPosition(int position) {
        ItemInfo info = super.infoForPosition(position);
        if (clipPopulate) {
            clipPopulate = false;
            if (info != null && info.scrolling) {
                info.scrolling = false;
            }
            ItemInfo ii = null;
            int startPos = position + 1;
            int endPos = position + getOffscreenPageLimit();
            for (int i = startPos; i <= endPos; i++) {
                ii = super.infoForPosition(i);
                if (ii != null && ii.scrolling) {
                    ii.scrolling = false;
                }
            }
            startPos = position - 1;
            endPos = position - getOffscreenPageLimit();
            for (int i = startPos; i >= endPos; i--) {
                ii = super.infoForPosition(i);
                if (ii != null && ii.scrolling) {
                    ii.scrolling = false;
                }
            }
        }
        return info;
    }

    int resetPosition = -1;

    public void resetCurrentItem(int newItem) {
        resetPosition = newItem;
        notifyDataSetChanged = false;
        clipPopulate = true;
        setCurrentItem(newItem, false);
    }

    boolean clipPopulate;

    @Override
    void populate(int newCurrentItem) {
        super.populate(newCurrentItem);
        // open when debug info
//        if (DEBUG) {
//            ItemInfo info = super.infoForPosition(newCurrentItem);
//            if (info != null) {
//                String key = ((WallpaperInfo) ((View) info.object).getTag()).key;
//                Log.d("ACME", "current " + newCurrentItem + ", " + key);
//            }
//        }
    }

    boolean notifyDataSetChanged;

    @Override
    void dataSetChanged() {
        notifyDataSetChanged = true;
        super.dataSetChanged();
    }

    public View getCurrentView() {
        return getViewByPosition(getCurrentItem());
    }

    public View getViewByPosition(int position) {
        ItemInfo info = super.infoForPosition(position);
        if (info != null && info.object instanceof View) {
            return (View) info.object;
        } else if (info != null && info.object instanceof Fragment) {
            return ((Fragment) info.object).getView();
        }
        return null;
    }
}