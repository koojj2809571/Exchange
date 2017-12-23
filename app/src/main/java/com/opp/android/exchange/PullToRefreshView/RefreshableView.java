package com.opp.android.exchange.PullToRefreshView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opp.android.exchange.R;

/**
 * Created by OPP on 2017/11/4.
 */

public class RefreshableView extends LinearLayout implements View.OnTouchListener {

    //--------------------------------------------定义变量--------------------------------------------------
    //下拉头状态
    public static final int STATUS_PULL_TO_REFRESH = 0; //下拉中但未到达可执行刷新位置
    public static final int STATUS_RELEASE_TO_REFRESH = 1; //超过可执行刷新位置提示释放刷新
    public static final int STATUS_REFRESHING = 2; //刷新中
    public static final int STATUS_REFRESH_FINISHED = 3; //完成刷新
    private int currentStatus = STATUS_REFRESH_FINISHED; //当前状态，初始化为完成刷新状态
    private int lastStatus = currentStatus; //上一状态，初始化为当前状态

    //更新时间相关
    private SharedPreferences mUpdateTimeRecorder; //纪录刷新时间
    public static final long ONE_MINUTE = 60 * 1000; //一分钟的毫秒值
    public static final long ONE_HOUR = 60 * ONE_MINUTE; //一个钟头的毫秒值
    public static final long ONE_DAY = 24 * ONE_HOUR; //一天的毫秒值
    public static final long ONE_MONTH = 30 * ONE_DAY; //一个月的毫秒值
    public static final long ONE_YEAR = 12 * ONE_MONTH; //一年的毫秒值
    private static final String UPDATED_AT = "updated_at"; //在preferences存储刷新时间的键
    private long lastUpdateTime; //上次更新时间毫秒值

    //初始化下拉头控件及其子控件
    private View mHeader; //下拉刷新头
    private ProgressBar mProgressBar; //刷新中显示进度条
    private ImageView mArrow; //提示手指移动箭头图标
    private TextView mDescription; //指示下拉释放文字描述
    private TextView mUpdatedAt; //显示更新时间

    private RecyclerView mRecyclerView; //需要被刷新的列表控件

    //手指移动相关
    private float mDownY;
    private int mTouchSlop; //手指移动时，移动距离达到某一值时，系统将判定为滚动控件，该距离的int值
    private int mHideHeaderHeight; //隐藏下拉头时的被隐藏高度，本次实现中刚好为下拉头高度的复数
    private MarginLayoutParams mHeaderLayoutParams; //下拉头布局参数
    public static final int SCROLL_SPEED = -20;


    //判断标志
    private boolean hasLoadOnce; //判断布局是否加载过一次
    private boolean ableToPull; //判断当前是否可以下拉，即是否滚动到头

    //自定义下拉刷新监听毁掉接口
    private PullToRefreshListener mListener;
    private int mId = -1; //为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分


    //--------------------------------------------具体实现--------------------------------------------------

    //---下拉刷新控件构造器，创建控件实例---
    public RefreshableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mUpdateTimeRecorder = PreferenceManager.getDefaultSharedPreferences(context);
        mHeader = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh, null, true);
        mProgressBar = mHeader.findViewById(R.id.progress_bar);
        mArrow = mHeader.findViewById(R.id.arrow);
        mDescription = mHeader.findViewById(R.id.description);
        mUpdatedAt = mHeader.findViewById(R.id.updated_at);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        refreshUpdatedAtValue();
        setOrientation(VERTICAL);
        addView(mHeader, 0);
    }

    //---重写父类方法---
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !hasLoadOnce) {
            mHideHeaderHeight = -mHeader.getHeight();
            mHeaderLayoutParams = (MarginLayoutParams) mHeader.getLayoutParams();
            mHeaderLayoutParams.topMargin = mHideHeaderHeight;
            mRecyclerView = (RecyclerView) getChildAt(1);
            mRecyclerView.setOnTouchListener(this);
            hasLoadOnce = true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float mMoveY = event.getRawY();
                    int distance = (int) (mMoveY - mDownY);
                    if (distance <= 0 && mHeaderLayoutParams.topMargin <= mHideHeaderHeight) {
                        return false;
                    }
                    if (distance < mTouchSlop) {
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (mHeaderLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }
                        mHeaderLayoutParams.topMargin = (distance / 2) + mHideHeaderHeight;
                        mHeader.setLayoutParams(mHeaderLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        new HideHeaderTask().execute();
                    }
                    break;
            }
            if (currentStatus == STATUS_PULL_TO_REFRESH || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                mRecyclerView.setPressed(false);
                mRecyclerView.setFocusable(false);
                mRecyclerView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                return true;
            }
        }
        return false;
    }

    //---Method---
    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    /**
     * 判断当前触摸事件是否触发刷新功能
     * 注：原例中使用ListView显示列表，可直接调用findFirstVisibleItemPosition().
     * RecyclerView中需先获取LayoutManager并强制转换为LinearLayoutManager，
     * 才可调用类似方法获取当前列表首个可见项目索引号。
     *
     * @param event ：触摸事件实例
     */
    private void setIsAbleToPull(MotionEvent event) {
        RecyclerView.LayoutManager mLayoutManager = mRecyclerView.getLayoutManager(); //获取列表layoutManager实例，必须为LinearLayoutManger才能调用获取首个可见项目索引号的方法
        View firstChild = mRecyclerView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                if (!ableToPull) {
                    mDownY = event.getRawY();
                }
                ableToPull = true;
            } else {
                if (mHeaderLayoutParams.topMargin != mHideHeaderHeight) {
                    mHeaderLayoutParams.topMargin = mHideHeaderHeight;
                    mHeader.setLayoutParams(mHeaderLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            ableToPull = true;
        }
    }

    private void updateHeaderView() {
        if (lastStatus != currentStatus){
            if (currentStatus == STATUS_PULL_TO_REFRESH){
                mDescription.setText(getResources().getString(R.string.pull_to_refresh));
                mArrow.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                rotateArrow();
            }else if (currentStatus == STATUS_RELEASE_TO_REFRESH){
                mDescription.setText(getResources().getString(R.string.release_to_refresh));
                mArrow.setVisibility(VISIBLE);
                mProgressBar.setVisibility(GONE);
                rotateArrow();
            }else if (currentStatus == STATUS_REFRESHING){
                mDescription.setText(getResources().getString(R.string.refreshing));
                mProgressBar.setVisibility(VISIBLE);
                mArrow.clearAnimation();
                mArrow.setVisibility(GONE);
            }
            refreshUpdatedAtValue();
        }
    }

    private void refreshUpdatedAtValue() {
        lastUpdateTime = mUpdateTimeRecorder.getLong(UPDATED_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = getResources().getString(R.string.time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }
        mUpdatedAt.setText(updateAtValue);
    }

    private void rotateArrow() {
        float pivotX = mArrow.getWidth()/2f;
        float pivotY = mArrow.getHeight()/2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH){
            fromDegrees = 180f;
            toDegrees = 360f;
        }else if (currentStatus == STATUS_RELEASE_TO_REFRESH){
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees,toDegrees,pivotX,pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        mArrow.startAnimation(animation);
    }

    public void finishRefreshing() {
        currentStatus = STATUS_REFRESH_FINISHED;
        mUpdateTimeRecorder.edit().putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
        new HideHeaderTask().execute();
    }

    //--------------------------------------------内部类--------------------------------------------------
    //---后台任务---
    private class RefreshingTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int topMargin = mHeaderLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeaderView();
            mHeaderLayoutParams.topMargin = topMargin[0];
            mHeader.setLayoutParams(mHeaderLayoutParams);
        }
    }

    private class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = mHeaderLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= mHideHeaderHeight) {
                    topMargin = mHideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            mHeaderLayoutParams.topMargin = topMargin[0];
            mHeader.setLayoutParams(mHeaderLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            mHeaderLayoutParams.topMargin = topMargin;
            mHeader.setLayoutParams(mHeaderLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }
}
