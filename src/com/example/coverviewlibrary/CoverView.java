package com.example.coverviewlibrary;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import com.example.coverviewlibrary.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@EViewGroup(resName="cover_view_layout")
public class CoverView extends RelativeLayout{
	@ViewById(resName="cover_view_info")
	public TextView mTextView;

	@ViewById(resName="cover_view_image")
	public ImageView mImageView;
	
	@ViewById(resName="cover_view_progressBar")
	public ProgressBar mProgressBar;
	
	@ViewById(resName="cover_view_btn_refresh")
	public Button btn_refresh;
	
	public CoverView(Context context) {
		super(context);
		init();
	}

	public CoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CoverView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public void showImageView(){
		mImageView.setVisibility(View.VISIBLE);
	}
	public void hideImageView(){
		mImageView.setVisibility(View.GONE);
	}
	public void showProgressBar(){
		mProgressBar.setVisibility(View.VISIBLE);
	}
	public void hideProgressBar(){
		mProgressBar.setVisibility(View.GONE);
	}
	public void setLoadingText(String text){
		mTextView.setText(text);
	}

	@AfterViews
	void afterViews(){
		mImageView.setImageResource(R.drawable.refresh_loadfailed);
		showImageView();
		hideProgressBar();
	}

	private void init() {
		setBackgroundColor(Color.parseColor("#faf0f0f0"));
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	
}
