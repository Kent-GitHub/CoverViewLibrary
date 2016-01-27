package com.example.coverviewlibrary;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BaseActivity extends Activity{
	
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
	protected String mCoverViewTag="mCoverViewTag";
	/**
	 * 标记点击mCoverView后是否还在加载中
	 */
	public boolean isTapRefreshing;
	/**
	 * 点击CoverView刷新监听事件实例
	 */
	private OnTapRefreshListener mOnTapRefreshListener;
	/**
	 * 设置刷新监听事件
	 * @param listener
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	private void init() {
		mRootView=(FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
	}

	public void setOnTapRefreshListener(OnTapRefreshListener listener){
		mOnTapRefreshListener=listener;
	}

	/**
	 * 遮住view所在的区域
	 * @param view
	 */
	public void cover(final View view){
		if (view==null) {
			return;
		}
		removeCover();
		//状态栏高度
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;
		//ActionBar高度
		int actionBarHeight = getActionBar().getHeight();
		int[] location =new int[2];
		view.getLocationOnScreen(location);
		initCoverView();
		mCoverView.setX(location[0]);
		mCoverView.setY(location[1]-statusBarHeight-actionBarHeight);
		int height=view.getHeight();
		int width=view.getWidth();
		LayoutParams lp=new LayoutParams(width,height);
		mCoverView.setLayoutParams(lp);
		mCoverView.setTag(mCoverViewTag);
		mRootView.addView(mCoverView);
		stateRefreshLoading();
		view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				initCoverView();
				mCoverView.setX(left);
				mCoverView.setY(top);
				LayoutParams lp=new LayoutParams(right-left,bottom-top);
				mCoverView.setLayoutParams(lp);
			}
		});
	}
	/**
	 * 移除mCoverView
	 */
	public void removeCover(){
		if (mRootView!=null&&((mCoverView=(CoverView) mRootView.findViewWithTag(mCoverViewTag))!=null)) {
			ViewGroup parent=(ViewGroup) mCoverView.getParent();
			parent.removeView(mCoverView);
		}
	}
	/**
	 * 移除mCoverView，带淡出效果
	 */
	public void removeCoverWithAnimation(){
		if (mRootView!=null&&((mCoverView=(CoverView) mRootView.findViewWithTag(mCoverViewTag))!=null)) {
			final ViewGroup parent=(ViewGroup) mCoverView.getParent();
			AlphaAnimation animation=new AlphaAnimation(1.0f, 0f);
			animation.setDuration(300);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					parent.removeView(mCoverView);
				}
			});
			mCoverView.startAnimation(animation);
		}
	}
	/**
	 * 更新CoverView状态为正在刷新
	 */
	public void stateRefreshLoading(){
		isTapRefreshing=true;
		mCoverView.showProgressBar();
		mCoverView.hideImageView();
		mCoverView.setLoadingText("正在加载，请稍候");
	}
	/**
	 * 更新CoverView状态为刷新成功
	 */
	public void stateRefreshSucceeded(){
		isTapRefreshing=false;
		removeCoverWithAnimation();
	}
	/**
	 * 更新CoverView状态为刷新失败
	 */
	public void stateRefreshFailed(){
		isTapRefreshing=false;
		mCoverView.hideProgressBar();
		mCoverView.mImageView.setImageResource(R.drawable.refresh_loadfailed);
		mCoverView.showImageView();
		mCoverView.setLoadingText("请求超时，请检查网络连接");
	}
	/**
	 * 更新CoverView状态为没有数据
	 */
	public void stateRefreshNoDatas(){
		isTapRefreshing=false;
		mCoverView.hideProgressBar();
		mCoverView.mImageView.setImageResource(R.drawable.refresh_empty);
		mCoverView.showImageView();
		mCoverView.setLoadingText("暂无数据");
	}
	
	/**
	 * 点击CoverView刷新接口
	 * @author kent
	 *
	 */
	public interface OnTapRefreshListener{
		void onTapRefresh();
	}
	/**
	 * 设置CoverView图片资源
	 * @param resId
	 */
	public CoverView setCoverViewImageResourse(int resId){
		mCoverView.mImageView.setImageResource(resId);
		return mCoverView;
	}
	/**
	 * 设置CoverView图片Drawable
	 * @param drawable
	 */
	public CoverView setCoverViewImageDrawable(Drawable drawable){
		mCoverView.mImageView.setImageDrawable(drawable);
		return mCoverView;
	}
	/**
	 * 设置CoverView图片bitmap
	 * @param bm
	 */
	public CoverView setCoverViewImageBitmap(Bitmap bm){
		mCoverView.mImageView.setImageBitmap(bm);
		return mCoverView;
	}
	/**
	 * 设置CoverView提示文字
	 * @param text
	 */
	public CoverView setCoverViewText(String text){
		mCoverView.mTextView.setText(text);
		return mCoverView;
	}
	/**
	 * 设置CoverView背景色
	 * @param color
	 */
	public CoverView setCoverViewBackgroundColor(int color){
		mCoverView.setBackgroundColor(color);
		return mCoverView;
	}
	
	/**
	 * 实例化mCoverView并拦截点击事件
	 * @return
	 */
	private CoverView initCoverView(){
		
		if (mCoverView==null) {
			mCoverView=CoverView.build(getApplicationContext(),mRootView);
			mCoverView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						if (mOnTapRefreshListener!=null) {
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
}
