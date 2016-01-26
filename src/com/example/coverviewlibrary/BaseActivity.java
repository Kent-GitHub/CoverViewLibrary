package com.example.coverviewlibrary;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
@EActivity
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
	 * 屏幕宽度
	 */
	protected int screenWidth;
	/**
	 * 屏幕高度
	 */
	protected int screenHeight;
	/**
	 * 点击CoverView刷新监听事件实例
	 */
	private OnTapRefreshListener mOnTapRefreshListener;
	/**
	 * 设置刷新监听事件
	 * @param listener
	 */
	public void setOnTapRefreshListener(OnTapRefreshListener listener){
		mOnTapRefreshListener=listener;
	}
	/**
	 * 遮住在坐标Y以下的部分
	 * @param topY
	 */
	public void cover(int topY){
		cover(topY,screenHeight);
	}
	/**
	 * 遮住topY以下bottomY以上的部分
	 * @param topY
	 * @param bottomY
	 */
	public void cover(int topY,int bottomY){
		cover(topY,bottomY,0,screenWidth);
	}
	/**
	 * 遮住topY以下bottomY以上、leftX右边rightX左边的部分
	 * @param topY
	 * @param bottomY
	 * @param leftX
	 * @param rightX
	 */
	public void cover(int topY,int bottomY,int leftX,int rightX){
		removeCover();
		
		//状态栏高度
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;
		//ActionBar高度
		int actionBarHeight = getActionBar().getHeight();
		getCoverView();
		mCoverView.setX(leftX);
		mCoverView.setY(topY);
		int height=bottomY-topY-actionBarHeight-statusBarHeight;
		int width=rightX-leftX;
		Log.d(mCoverViewTag, "height:"+height+", "+"width:"+width);
		LayoutParams lp=new LayoutParams(width,height);
		mCoverView.setLayoutParams(lp);
		
		mCoverView.setTag(mCoverViewTag);
		
		mRootView.addView(mCoverView);
		
	}
	/**
	 * 遮住v所在的区域
	 * @param v
	 */
	public void cover(View v){
		removeCover();
		
		//状态栏高度
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;
		//ActionBar高度
		int actionBarHeight = getActionBar().getHeight();
		int height=v.getHeight();
		int width=v.getWidth();
		
		int[] location =new int[2];
		v.getLocationOnScreen(location);
		
		getCoverView();
		mCoverView.setX(location[0]);
		mCoverView.setY(location[1]-statusBarHeight-actionBarHeight);
		
		LayoutParams lp=new LayoutParams(width,height);
		mCoverView.setLayoutParams(lp);
		
		mCoverView.setTag(mCoverViewTag);
		
		mRootView.addView(mCoverView);
		
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
	public void refreshing(){
		isTapRefreshing=true;
		mCoverView.showProgressBar();
		mCoverView.hideImageView();
		mCoverView.setLoadingText("正在加载，请稍候");
	}
	/**
	 * 更新CoverView状态为刷新成功
	 */
	public void refreshSucceeded(){
		isTapRefreshing=false;
		removeCoverWithAnimation();
	}
	/**
	 * 更新CoverView状态为刷新失败
	 */
	public void refreshFailed(){
		isTapRefreshing=false;
		mCoverView.hideProgressBar();
		mCoverView.showImageView();
		mCoverView.setLoadingText("加载失败，请检查网络连接");
	}
	
	@AfterViews
	protected void baseActyAfterViews(){
		DisplayMetrics dm = new DisplayMetrics();       
		getWindowManager().getDefaultDisplay().getMetrics(dm);       
		screenWidth = dm.widthPixels;       
		screenHeight = dm.heightPixels; 
		mRootView=(FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
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
	 * 实例化mCoverView并为其子View设置点击事件
	 * @return
	 */
	private CoverView getCoverView(){
		if (mCoverView==null) {
			mCoverView=CoverView_.build(getApplicationContext());
			mCoverView.mImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mOnTapRefreshListener!=null&&!isTapRefreshing) {
						mOnTapRefreshListener.onTapRefresh();
						refreshing();
					}
				}
			});
			mCoverView.mTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mOnTapRefreshListener!=null&&!isTapRefreshing) {
						mOnTapRefreshListener.onTapRefresh();
						refreshing();
					}
				}
			});
		}
		return mCoverView;
	}
	
}
