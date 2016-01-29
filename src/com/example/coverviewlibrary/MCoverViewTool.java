package com.example.coverviewlibrary;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MCoverViewTool{
	
    /**
	 * 记录加载状态
	 */
    private int refreshType = 1;
    
	/**
	 * 状态：正在加载
	 */
	public static final int REFRESH_Loading = 1;
	/**
	 * 状态：空数据
	 */
	public static final int REFRESH_Empty = 2;
	/**
	 * 状态：错误
	 */
	public static final int REFRESH_Error = 3;
	/**
	 * 状态：网络
	 */
	public static final int REFRESH_Network = 4;
	/**
	 * 状态：加载成功
	 */
	public static final int REFRESH_Succeed = 5;
	
	/**
	 * mCoverView背景色
     */
    private static int mBgColor = Color.parseColor("#faf0f0f0");
    /**
     * mTextView字体颜色
     */
    private static int mTextColor = Color.parseColor("#000000");
    
    /**
     * 空白图片资源
     */
    private int mEmptyImageRes = R.drawable.refresh_empty;
    /**
     * 错误图片资源
     */
    private int mErrorImageRes = R.drawable.refresh_crash;
    /**
     * 网络图片资源
     */
    private int mNetWorkImageRes = R.drawable.refresh_loadfailed;
    
    /**
     * 正在加载信息提示
     */
    private String mLoadingMessage = "正在加载，请稍候";
    /**
     * 錯誤提示信息
     */
    private String mEmptyMessage = "暂无数据";
    /**
     * 错误提示信息
     */
    private String mErrorMessage = "请求异常，请稍候重试";
    /**
     * 网络提示信息
     */
    private String mNetWorkMessage = "请求超时，请稍候重试";

	/**
     * 空白时按钮信息
     */
    private String mEmptyButtonMessage = "重试";
    /**
     * 错误时按钮信息
     */
    private String mErrorButtonMessage = "重试";
    /**
     * 网络时按钮信息
     */
    private String mNetWorkButtonMessage = "重试";
    /**
     * mButton點擊监听事件实例
     */
    private OnButtonClickListener mOnButtonClickListener;
    /**
     * mCoverView点击监听事件实例
     */
	private OnTapRefreshListener mOnTapRefreshListener;

	/**
	 * 用来遮盖的View的实例
	 */
	public CoverView mCoverView;
	/**
	 * RootView实例，用作CoverView的父容器
	 */
	private FrameLayout mRootView;
	/**
	 * CoverView的进度条
	 */
	public ProgressBar mProgressBar;
	/**
	 * CoverView的图片
	 */
	public ImageView mImageView;
	/**
	 * CoverView的文本
	 */
	public TextView mTextView;
	/**
	 * CoverView的Button
	 */
	public Button mButton;
	/**
	 * 给mCoverView设置的Tag
	 */
	private String mCoverViewTag = "mCoverViewTag";
	/**
	 * 标记点击mCoverView后是加载中的状态
	 */
	private boolean isRefreshing;

	/**
	 * 用于储存CoverView里的子View
	 */
	private List<View> mViews;
	/**
	 * Activity.this
	 */
	private Activity mActivity;
	
	
    // ---------------------------
    // public methods
    // ---------------------------
	
	/**
	 * 构造方法
	 * @param activity
	 */
	public MCoverViewTool(Activity activity) {
		init(activity);
	}
	/**
	 * 构造方法
	 * @param activity
	 */
	public MCoverViewTool(Activity activity,View view) {
		init(activity);
		cover(view);
	}
	
    /**
	 * 遮住view所在的区域
	 * @param view
	 */
	public void cover(final View view) {
		if (view == null) {
			return;
		}
		removeCover(false);
		// 状态栏高度
		Rect frame = new Rect();
		mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		// ActionBar高度
		int actionBarHeight = mActivity.getActionBar().getHeight();
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		initCoverView();
		mCoverView.setX(location[0]);
		mCoverView.setY(location[1] - statusBarHeight - actionBarHeight);
		int height = view.getHeight();
		int width = view.getWidth();
		LayoutParams lp = new LayoutParams(width, height);
		mCoverView.setLayoutParams(lp);
		mCoverView.setTag(mCoverViewTag);
		mRootView.addView(mCoverView);		
		//被遮住的View区域发生改变时、CoverView跟着改变
		view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
	
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom) {
				initCoverView();
				mCoverView.setX(left);
				mCoverView.setY(top);
				LayoutParams lp = new LayoutParams(right - left, bottom - top);
				mCoverView.setLayoutParams(lp);
			}
		});
	}
	/**
	 * 移除mCoverView
	 * 
	 * @param withAnimation
	 *            true以淡出形式移除CoverView、false则直接移除CoverView
	 */
	public void removeCover(boolean withAnimation) {
		if (mRootView != null&& ((mCoverView = (CoverView) mRootView.findViewWithTag(mCoverViewTag)) != null)) {
			final ViewGroup parent = (ViewGroup) mCoverView.getParent();
			if (withAnimation) {
				AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
				animation.setDuration(300);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
	
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
	
					@Override
					public void onAnimationEnd(Animation animation) {
						parent.removeView(mCoverView);
					}
				});
				mCoverView.startAnimation(animation);
			} else {
				parent.removeView(mCoverView);
			}
		}
	}
	
	/**
	 * 显示加载状态
	 */
	public void showLoading() {
	    this.refreshType = REFRESH_Loading;
	    changeRefreshStatus();
	    isRefreshing=true;
	}
	/**
	 * 显示空白结果
	 */
	public void showEmpty() {
	    this.refreshType = REFRESH_Empty;
	    changeRefreshStatus();
	    isRefreshing=false;
	}
	/**
	 * 显示错误结果
	 */
	public void showError() {
	    this.refreshType = REFRESH_Error;
	    changeRefreshStatus();
	    isRefreshing=false;
	}
	/**
	 * 显示网络结果
	 */
	public void showNetWork() {
	    this.refreshType = REFRESH_Network;
	    changeRefreshStatus();
	    isRefreshing=false;
	}
	/**
	 * 结束显示；
	 */
	public void showFinish() {
		removeCover(true);
	    isRefreshing = true;
	}
	// ---------------------------
    // private methods
    // ---------------------------
	/**
	 * 赋值初始化、mRootView初始化
	 * @param activity
	 */
	private void init(Activity activity){
		mActivity=activity;
		mRootView = (FrameLayout)activity.getWindow().getDecorView().findViewById(android.R.id.content);
	}
	
	/**
	 * 实例化mCoverView
	 * @return
	 */
	private CoverView initCoverView() {
		if (mCoverView == null) {
			mCoverView = CoverView.build(mActivity, mRootView);
			int childCount = mCoverView.getChildCount();
			mViews = new ArrayList<View>();
			for (int i = 0; i < childCount; i++) {
				mViews.add(mCoverView.getChildAt(i));
			}
			mProgressBar=(ProgressBar) mCoverView.findViewById(R.id.cover_view_progressBar);
			mImageView=(ImageView) mCoverView.findViewById(R.id.cover_view_image);
			mTextView=(TextView) mCoverView.findViewById(R.id.cover_view_tv);
			mButton=(Button) mCoverView.findViewById(R.id.cover_view_btn);
			mTextView.setTextColor(mTextColor);
			mCoverView.setBackgroundColor(mBgColor);
			//mButton点击事件
			mButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnButtonClickListener!=null){
						mOnButtonClickListener.onClick(v, refreshType);
					}
				}
			});
			//触发三种状态的点击事件、拦截点击事件。
			mCoverView.setOnTouchListener(new OnTouchListener() {
	
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction()==MotionEvent.ACTION_UP&&!isRefreshing&&mOnTapRefreshListener!=null) {
						mOnTapRefreshListener.OnTapRefresh(refreshType);
					}
					return true;
				}
			});
			//setMargins处理
			mCoverView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
				
				@Override
				public void onLayoutChange(View v, int left, int top, int right,
						int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
					int childTotalHeight=0;
					int[] childsHeights=new int[mCoverView.getChildCount()];
					for (int i = 0; i < mCoverView.getChildCount(); i++) {
						View view=mViews.get(i);
						if (view.getVisibility()==View.VISIBLE) {
							RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) view.getLayoutParams();
							childsHeights[i]=view.getHeight();
							if (i>=1) {
								childsHeights[i-1]+=lp.topMargin;
							}
							childTotalHeight+=view.getHeight();
						}else {
							childsHeights[i]=0;
						}
					}
					float shouldSetY=(mCoverView.getHeight()-childTotalHeight)/2;
					for (int i = 0; i < childsHeights.length; i++) {
						mViews.get(i).setY(shouldSetY);
						shouldSetY+=childsHeights[i];
					}
				}
			});
		}
		mProgressBar.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.GONE);
		mButton.setVisibility(View.GONE);
		return mCoverView;
	}

	/**
	 * 根据refreshType的值刷新mCoverView
	 */
	private void changeRefreshStatus() {
		switch (refreshType) {
		case REFRESH_Loading:
			mProgressBar.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.GONE);
			mTextView.setText(mLoadingMessage);
			break;
		case REFRESH_Empty:
			mProgressBar.setVisibility(View.GONE);
			mImageView.setImageResource(mEmptyImageRes);
			mImageView.setVisibility(View.VISIBLE);
			mTextView.setText(mEmptyMessage);
			mButton.setText(mEmptyButtonMessage);
			break;
		case REFRESH_Error:
			mProgressBar.setVisibility(View.GONE);
			mImageView.setImageResource(mErrorImageRes);
			mImageView.setVisibility(View.VISIBLE);
			mTextView.setText(mErrorMessage);
			mButton.setText(mErrorButtonMessage);
			break;
		case REFRESH_Network:
			mProgressBar.setVisibility(View.GONE);
			mImageView.setImageResource(mNetWorkImageRes);
			mImageView.setVisibility(View.VISIBLE);
			mTextView.setText(mNetWorkMessage);
			mButton.setText(mNetWorkButtonMessage);
			break;
		}
	}
	
	/**
	 * mCoverView点击事件接口
	 */
	public interface OnTapRefreshListener{
		/**
		 * mCoverView点击事件
		 * @param failedType 
		 */
		public void OnTapRefresh(int failedType);
	}
	/**
	 * mCoverView的 mButtom点击事件接口
	 */
	public interface OnButtonClickListener{
		/**
		 * mCoverView中Button点击事件
		 * @param view
		 * @param failedType
		 */
		public void onClick(View view,int failedType);
	}
	
	/**
	 * 设置CoverView背景色
	 * @param color
	 */
	public CoverView setCoverViewBackgroundColor(int color) {
		mCoverView.setBackgroundColor(color);
		return mCoverView;
	}
	
	/**
	 * 为CoverView的子View设置MarginTop
	 * @param view
	 * @param top
	 * @return
	 */
	public CoverView setMarginTop(View view, int top) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view
				.getLayoutParams();
		lp.setMargins(0, top, 0, 0);
		view.setLayoutParams(lp);
		return mCoverView;
	}
	/**
	 * 为mCoverView设置点击事件
	 * @param mOnTapRefreshListener
	 */
	public void setOnTapRefreshListener(OnTapRefreshListener mOnTapRefreshListener){
		this.mOnTapRefreshListener=mOnTapRefreshListener;
	}
	/**
	 * 为mButton设置点击事件
	 * @param mOnButtonClickListener
	 */
	public void setOnButtonLClickListener(OnButtonClickListener mOnButtonClickListener){
		this.mOnButtonClickListener= mOnButtonClickListener;
	}
	
	// ---------------------------
    // get/set
    // ---------------------------

    public void setmEmptyImageRes(int resId) {
        this.mEmptyImageRes = resId;
    }

    public void setmErrorImageRes(int resId) {
        this.mErrorImageRes = resId;
    }

    public void setmNetWorkImageRes(int resId) {
        this.mNetWorkImageRes = resId;
    }
    
	 // ---------------------------
    // get/set
    // ---------------------------

    public void setmEmptyMessage(String mEmptyMessage) {
        this.mEmptyMessage = mEmptyMessage;
    }

    public void setmErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }

    public void setmNetWorkMessage(String mNetWorkMessage) {
        this.mNetWorkMessage = mNetWorkMessage;
    }

    // ---------------------------
    // get/set
    // ---------------------------

    public void setmEmptyButtonMessage(String mEmptyButtonMessage) {
        this.mEmptyButtonMessage = mEmptyButtonMessage;
    }

    public void setmErrorButtonMessage(String mErrorButtonMessage) {
        this.mErrorButtonMessage = mErrorButtonMessage;
    }

    public void setmNetWorkButtonMessage(String mNetWorkButtonMessage) {
        this.mNetWorkButtonMessage = mNetWorkButtonMessage;
    }

    // ---------------------------
    // get/set
    // ---------------------------


}
