package com.example.coverviewlibrary;

import com.example.coverviewlibrary.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class CoverView extends LinearLayout{
	
	public TextView mTextView;
		
	public ImageView mImageView;
	
	public ProgressBar mProgressBar;
	
	public Button mButton;
	
	public CoverView(Context context) {
		super(context);
		init(context);
	}

	public CoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CoverView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
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
	private void init(Context context) {
		if (inflateView==null) {
			return;
		}
		mTextView=(TextView) inflateView.findViewById(R.id.cover_view_info);
		mImageView=(ImageView) inflateView.findViewById(R.id.cover_view_image);
		mProgressBar=(ProgressBar) inflateView.findViewById(R.id.cover_view_progressBar);
		mButton=(Button) inflateView.findViewById(R.id.cover_view_btn_refresh);
		this.setBackgroundColor(Color.parseColor("#faf0f0f0"));
		hideImageView();
		hideButton();
	}
	
	private static LinearLayout inflateView;
	public static CoverView build(Context context,ViewGroup view){
		inflateView= (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cover_view_layout,view,false);
		CoverView instance=new CoverView(context);
		instance.addView(inflateView);
		return instance;
	}
	
}
