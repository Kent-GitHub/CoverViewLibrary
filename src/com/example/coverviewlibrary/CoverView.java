package com.example.coverviewlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CoverView extends RelativeLayout{
	
	public static CoverView mCoverView;
	
	public TextView mTextView;
		
	public ImageView mImageView;
	
	public ProgressBar mProgressBar;
	
	public Button mButton;
	
	public CoverView(Context context) {
		super(context);
	}

	public CoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CoverView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public CoverView showProgressBar(){
			mProgressBar.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView showImageView(){
		mImageView.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView showTextView(){
		mTextView.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView showButton(){
		mButton.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView hideProgressBar(){
		mProgressBar.setVisibility(View.GONE);
		return this;
	}

	public CoverView hideImageView(){
		mImageView.setVisibility(View.GONE);
		return this;
	}
	public CoverView hideTextView(){
		mTextView.setVisibility(View.GONE);
		return this;
	}
	public CoverView hideButton(){
		mButton.setVisibility(View.GONE);
		return this;
	}
	public CoverView setLoadingText(String text){
		mTextView.setText(text);
		return this;
	}
	
	public void init(Context context) {
		mTextView=(TextView) mCoverView.findViewById(R.id.cover_view_info);
		mImageView=(ImageView) mCoverView.findViewById(R.id.cover_view_image);
		mProgressBar=(ProgressBar) mCoverView.findViewById(R.id.cover_view_progressBar);
		mButton=(Button) mCoverView.findViewById(R.id.cover_view_btn_refresh);
		hideImageView();
		hideButton();
	}
	
	public static CoverView build(Context context,ViewGroup view){
		mCoverView = (CoverView) LayoutInflater.from(context).inflate(R.layout.cover_view_layout,view,false);
		return mCoverView;
	}
	
}
