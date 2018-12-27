package android.test.foldinglayout2;

import android.content.Context;
import android.support.v4.view.PreviewViewPager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class FoldingLayout extends FrameLayout {

    protected PreviewViewPager mViewPager;
    private boolean finishInflate;
    private PreviewAdapter mAdapter;
    private PreviewPageTransformer transformer;
    private int viewpagerInitPosition;

    ViewConfiguration mViewConfiguration;
    Context mContext;

    public FoldingLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public FoldingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public FoldingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
//        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.PreviewView);
//        try {
//            showPosts = a.getBoolean(R.styleable.PreviewView_showPosts, false);
//        } finally {
//            a.recycle();
//        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        handleFinishInflate();
        finishInflate = true;
    }

    protected void handleFinishInflate() {
        mViewConfiguration = ViewConfiguration.get(mContext);

        mViewPager = new PreviewViewPager(mContext);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setMainView(this);
        addView(mViewPager, 0);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    return cannotMove();
                }
                return false;
            }
        });
        setOnPageChangeListener(null);
    }

    public boolean cannotMove() {
        boolean flag = !mViewPager.touchSlopEnable();
        return flag;
    }

    protected PreviewPageChangeListener internalPageChangeistener;

    public void setOnPageChangeListener(final PreviewPageChangeListener listener) {
        internalPageChangeistener = listener;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                View current = mViewPager.getCurrentView();

                if (listener != null) {
                    listener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (listener != null) {
                    listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setOnPageStartGoingListener(new PreviewViewPager.OnPageStartGoingListener() {
            @Override
            public void onPageStartGoing(Context context, PreviewViewPager.State state) {
                if (listener != null) {
                    listener.onPageStartGoing(context, state);
                }
            }
        });
        mViewPager.setOnPageStayListener(new PreviewViewPager.OnPageStayListener() {
            @Override
            public void onPageStayOn(int oldPage, long stayDuration) {
                if (listener != null) {
                    listener.onPageStayOn(oldPage, stayDuration);
                }
            }
        });
        mViewPager.setOnRetainInstanceListener(new PreviewViewPager.OnRetainInstanceListener() {
            @Override
            public void onRetainInstanceReady(Context context) {
                if (listener != null) {
                    listener.onRetainInstanceReady(context);
                }
            }
        });
    }

    public void dispatchOnScrollStateChanged(int state) {
        if (internalPageChangeistener != null) {
            internalPageChangeistener.onPageScrollStateChanged(state);
        }
    }

    public void onSelectInitialItem() {
//        if (this.listener != null) {
//            this.listener.onSelectInitialItem(getContext());
//        }
    }

    public void setAdapter(PreviewAdapter adapter, int initPosition) {
        if (null == adapter) {
            return;
        }
        mAdapter = adapter;
        mAdapter.setViewPager(this);

        viewpagerInitPosition = parseInitPositionByDirect(mAdapter.getDirect(), initPosition);

        mViewPager.setAdapter(mAdapter);

        if (Direct.CIRCLE == mAdapter.getDirect()) {
            // 双向滑动
            mViewPager.setCurrentItem(viewpagerInitPosition, false);
        } else if (Direct.FLAT == mAdapter.getDirect()) {
            // 平坦滑动
            if (initPosition >= 0) {
                mViewPager.setCurrentItem(viewpagerInitPosition, false);
            }
        }

        mViewPager.setPageTransformer(getPreviewPageTransformer());
    }

    private int parseInitPositionByDirect(Direct direct, int initPosition) {
        if (Direct.CIRCLE == direct) {
            // 双向滑动
            viewpagerInitPosition = mAdapter.getCount() == 1 ?
                    0 : mAdapter.getSize() * 10;
            mAdapter.setInitPosition(viewpagerInitPosition);
        } else if (Direct.FLAT == direct) {
            // 平坦滑动
            if (initPosition >= 0) {
                viewpagerInitPosition = initPosition;
                mAdapter.setInitPosition(viewpagerInitPosition);
            } else {
                viewpagerInitPosition = 0;
                mAdapter.setInitPosition(viewpagerInitPosition);
            }
        } else if (Direct.SLOOP == direct) {
            // 向右滑动
            viewpagerInitPosition = 0;
            mAdapter.setInitPosition(viewpagerInitPosition);
        } else {
            viewpagerInitPosition = 0;
            mAdapter.setInitPosition(viewpagerInitPosition);
        }
        return viewpagerInitPosition;
    }

    public void setAdapter(PreviewAdapter adapter) {
        setAdapter(adapter, 0);
    }

    public void setPreviewPageTransformer(PreviewPageTransformer transformer) {
        this.transformer = transformer;
    }

    public PreviewPageTransformer getPreviewPageTransformer() {
        return this.transformer;
    }
}
