package com.example.coverviewlibrary;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

public class CoverViewTool{
	public static final int REFRESH_Loading = 0;
	public static final int REFRESH_NetworkError = 1;
	public static final int REFRESH_NoDatas = 2;
	public static final int REFRESH_Crash = 3;
	public static final int REFRESH_Succeed = 4;

	private int refreshNetErrorResID = R.drawable.refresh_loadfailed;
	private int refreshNoDatasResID = R.drawable.refresh_empty;
	private int refreshCrashResID = R.drawable.refresh_loadfailed;

	/**
	 * 用来遮盖的View的实例
	 */
	public CoverView mCoverView;
	/**
	 * RootView实例
	 */
	protected FrameLayout mRootView;
	/**
	 * 给mCoverView设置的Tag
	 */
	protected String mCoverViewTag = "mCoverViewTag";
	/**
	 * 标记点击mCoverView后是否还在加载中
	 */
	public boolean isTapRefreshing;
	/**
	 * 点击CoverView刷新监听事件实例
	 */
	private OnTapRefreshListener mOnTapRefreshListener;

	private List<View> mViews;
	
	private Context mContext;
	
	private Activity mActivity;
	
	public CoverViewTool(Activity activity) {
		mActivity=activity;
		mContext=activity.getApplicationContext();
		mRootView = (FrameLayout)activity.getWindow().getDecorView().findViewById(android.R.id.content);
	}
	

	/**
	 * 设置刷新监听事件
	 * 
	 * @param listener
	 */
	public void setOnTapRefreshListener(OnTapRefreshListener listener) {
		mOnTapRefreshListener = listener;
	}

	/**
	 * 遮住view所在的区域
	 * 
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
		stateRefreshLoading();
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
		if (mRootView != null
				&& ((mCoverView = (CoverView) mRootView
						.findViewWithTag(mCoverViewTag)) != null)) {
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
	 * 更新CoverView状态为正在刷新
	 */
	public void stateRefreshLoading() {
		isTapRefreshing = true;
		mCoverView.showProgressBar();
		mCoverView.hideImageView();
		mCoverView.setLoadingText("正在加载，请稍候");
	}

	/**
	 * 更新CoverView状态为刷新成功
	 */
	public void stateRefreshSucceeded() {
		isTapRefreshing = false;
		removeCover(true);
	}

	/**
	 * 更新CoverView状态为刷新失败
	 */
	public void stateRefreshFailed(int type) {
		isTapRefreshing = false;
		mCoverView.hideProgressBar();
		switch (type) {
		case REFRESH_NetworkError:
			mCoverView.mImageView.setImageResource(refreshNetErrorResID);
			mCoverView.setLoadingText("请求超时，请检查网络连接");
			break;
		case REFRESH_NoDatas:
			mCoverView.mImageView.setImageResource(refreshNoDatasResID);
			mCoverView.setLoadingText("没有数据");
			break;
		case REFRESH_Crash:
			mCoverView.mImageView.setImageResource(refreshCrashResID);
			mCoverView.setLoadingText("服务器崩溃了");
			break;
		default:
			mCoverView.mImageView.setImageResource(refreshNetErrorResID);
			mCoverView.setLoadingText("出错了");
			break;
		}
		mCoverView.showImageView();
	}


	/**
	 * 点击CoverView刷新接口
	 * 
	 * @author kent
	 * 
	 */
	public interface OnTapRefreshListener {
		void onTapRefresh();
	}

	/**
	 * 设置CoverView图片资源
	 * 
	 * @param resId
	 */
	public CoverView setCoverViewImageResourse(int type, int resId) {
		switch (type) {
		case REFRESH_NetworkError:
			refreshNetErrorResID = resId;
			break;
		case REFRESH_NoDatas:
			refreshNoDatasResID = resId;
			break;
		case REFRESH_Crash:
			refreshCrashResID = resId;
			break;
		}
		return mCoverView;
	}

	/**
	 * 设置CoverView图片Drawable
	 * 
	 * @param drawable
	 */
	public CoverView setCoverViewImageDrawable(Drawable drawable) {
		mCoverView.mImageView.setImageDrawable(drawable);
		return mCoverView;
	}

	/**
	 * 设置CoverView图片bitmap
	 * 
	 * @param bm
	 */
	public CoverView setCoverViewImageBitmap(Bitmap bm) {
		mCoverView.mImageView.setImageBitmap(bm);
		return mCoverView;
	}

	/**
	 * 设置CoverView提示文字
	 * 
	 * @param text
	 */
	public CoverView setCoverViewText(String text) {
		mCoverView.mTextView.setText(text);
		return mCoverView;
	}

	/**
	 * 设置CoverView背景色
	 * 
	 * @param color
	 */
	public CoverView setCoverViewBackgroundColor(int color) {
		mCoverView.setBackgroundColor(color);
		return mCoverView;
	}

	public CoverView setCoverViewOrder(int progressbar, int imageView,
			int TextView, int button) {
		if (progressbar >= 0 && progressbar < 4 && imageView >= 0
				&& imageView < 4 && TextView >= 0 && TextView < 4
				&& button >= 0 && button < 4) {

			Log.d("setCoverViewOrder", "progressbar:" + progressbar
					+ ", imageView" + imageView + ", TextView" + TextView
					+ ", button" + button);
			mViews.set(progressbar, mCoverView.mProgressBar);
			mViews.set(imageView, mCoverView.mImageView);
			mViews.set(TextView, mCoverView.mTextView);
			mViews.set(button, mCoverView.mButton);
		} else {
			return mCoverView;
		}
		for (int i = 0; i < 4; i++) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mViews
					.get(i).getLayoutParams();
			if (i == 0) {
				lp.addRule(RelativeLayout.BELOW, 0);
			} else {
				lp.addRule(RelativeLayout.BELOW, mViews.get(i - 1).getId());
			}
			mViews.get(i).setLayoutParams(lp);
		}

		return mCoverView;
	}

	public CoverView setMargins(View view, int left, int top, int right,
			int bottom) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view
				.getLayoutParams();
		lp.setMargins(left, top, right, bottom);
		view.setLayoutParams(lp);
		return mCoverView;
	}

	/**
	 * 实例化mCoverView并拦截点击事件
	 * 
	 * @return
	 */
	private CoverView initCoverView() {
		if (mCoverView == null) {
			mCoverView = CoverView.build(mContext, mRootView);
			int childCount = mCoverView.inflateView.getChildCount();
			mViews = new ArrayList<View>();
			for (int i = 0; i < childCount; i++) {
				mViews.add(mCoverView.inflateView.getChildAt(i));
			}
			mCoverView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						if (mOnTapRefreshListener != null) {
							mOnTapRefreshListener.onTapRefresh();
							stateRefreshLoading();
						}
						break;
					}
					return true;
				}
			});
		}
		return mCoverView;
	}

	private void setViewPosition() {

	}

}
