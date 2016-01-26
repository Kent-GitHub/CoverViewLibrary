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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CoverView extends RelativeLayout{
	
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

	private void init(Context context) {
		if (inflateView==null) {
			return;
		}
		mTextView=(TextView) inflateView.findViewById(R.id.cover_view_info);
		mImageView=(ImageView) inflateView.findViewById(R.id.cover_view_image);
		mProgressBar=(ProgressBar) inflateView.findViewById(R.id.cover_view_progressBar);
		mButton=(Button) inflateView.findViewById(R.id.cover_view_btn_refresh);
		this.setBackgroundColor(Color.parseColor("#faf0f0f0"));
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	private static View inflateView;
	public static CoverView build(Context context,ViewGroup view){
		inflateView= LayoutInflater.from(context).inflate(R.layout.cover_view_layout,view,false);
		CoverView instance=new CoverView(context);
		instance.addView(inflateView);
		return instance;
	}
	
}
