package com.zhy.Adapter;

import java.util.ArrayList;
import java.util.HashMap;
import com.dawnlightning.ucqa.R;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhy.Activity.PictureActivity;
import com.zhy.Bean.DetailedBean;
import com.zhy.Bean.PicsBean;
import com.zhy.Util.HttpConstants;
import com.zhy.Util.UnitTransformUtil;
import com.zhy.View.NoScrollGridView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class ContentAdapter extends BaseAdapter {
	private Context context;
	private HashMap<String,DetailedBean> content;
	private LayoutInflater layoutInflater;
	private Message message;
	private Picture picture;
	private Title title;
	private ArrayList<String> piclsit;
	private MainGridViewAdapter mainGridViewAdapter;
	private final static String [] fileds={"开头","正文","图片"};
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	public ContentAdapter(Context context,HashMap<String,DetailedBean> content){
		this.context=context;
		this.content=content;
		layoutInflater = (LayoutInflater) LayoutInflater.from(context);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	    // default
	    .threadPriority(Thread.NORM_PRIORITY - 2)
	    .denyCacheImageMultipleSizesInMemory()
	    .discCacheFileNameGenerator(new Md5FileNameGenerator())
	    .tasksProcessingOrder(QueueProcessingType.LIFO)
	    .denyCacheImageMultipleSizesInMemory()
	    .memoryCache(new LruMemoryCache((int) (6 * 1024 * 1024)))
	    .memoryCacheSize((int) (2 * 1024 * 1024))
	    .memoryCacheSize(13)
	    // default
	    .discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
	    .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
	    .build();
		imageLoader.init(config);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.defalut)
				.showImageOnFail(R.drawable.defalut).cacheInMemory().cacheOnDisc()
				.displayer(new RoundedBitmapDisplayer(20)).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}
	@SuppressWarnings("unused")
	private String  getkeys(int arg0){
		int postion=0;
		String nowkey="";
		for(String key:content.keySet()){
			if(postion==arg0){
				nowkey=key;
				break;
			}else{
				postion++;
			}
		}
		return nowkey;
	}
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return content.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO 自动生成的方法存根
		int postion=0;
		
		String nowkey="";
		for(String key:content.keySet()){
			if(postion==arg0){
				nowkey=key;
				break;
			}else{
				postion++;
			}
		}
		return content.get(nowkey);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO 自动生成的方法存根
		return arg0;
	}

	
	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		String keys=fileds[arg0];
		Log.e("arg0", String.valueOf(arg0));
		DetailedBean dt=content.get(keys);
		//if (null == arg1) {
			switch(arg0){

			case 0:
				arg1=layoutInflater.inflate(R.layout.item_content_title,null);
				title=new Title();
				title.ivUserLogo=(ImageView) arg1.findViewById(R.id.iv_leftlogo);
				title.tvUserName=(TextView)arg1.findViewById(R.id.userName);
				title.tvUserAge=(TextView)arg1.findViewById(R.id.tvAge);
				title.moodinTime=(TextView)arg1.findViewById(R.id.sendTime);
				arg1.setTag(R.id.title_id,title);
				break;
			case 1:
				arg1=layoutInflater.inflate(R.layout.item_content_message,null);
				message=new Message();
				message.moodinContent=(TextView) arg1.findViewById(R.id.checkInfo);
				message.subject=(TextView) arg1.findViewById(R.id.showInfo);
				arg1.setTag(R.id.message_id,message);
				break;
			case 2:
				arg1=layoutInflater.inflate(R.layout.item_content_pictrue,null);
				picture=new Picture();
				picture.moodinGridView=(NoScrollGridView) arg1.findViewById(R.id.myGridView);
				arg1.setTag(R.id.pictrue_id, picture);
				break;
			
		
			}
	
		
		if(title==null){
			title=	(Title)	 arg1.getTag(R.id.title_id);
		}
		if(message==null){
			message=(Message) arg1.getTag(R.id.message_id);
		}
		if(picture==null){
			picture=(Picture) arg1.getTag(R.id.pictrue_id);
		}
		
		switch(keys){
		case "开头":
			
			seticon(dt.getAvatar_url());
			if(!dt.getName().isEmpty()){
				title.tvUserName.setText(dt.getName());
			}else{
				title.tvUserName.setText(dt.getUsename());
			}
		
			title.tvUserAge.setText(dt.getAge());
			title.moodinTime.setText(dt.getDatetime());
			break;
		case "正文":
		
			message.moodinContent.setText(dt.getContent().trim());
			message.subject.setText(dt.getSubject().trim());
		
			
			break;
		case "图片":
			if(dt.getPics()!=null){
				piclsit=new ArrayList();
				for(PicsBean p:dt.getPics()){
					
					piclsit.add(HttpConstants.HTTP_IP+p.getUrl());
				
				}
				
				if (piclsit.size() > 0) {
					
					
					 mainGridViewAdapter = new MainGridViewAdapter(context, piclsit);
					//说明有三行
					if(piclsit.size()>6){
						
						LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) picture.moodinGridView.getLayoutParams(); // 取控件mGrid当前的布局参数
						linearParams.height = UnitTransformUtil.dip2px(context, 300);// 
						picture.moodinGridView.setHorizontalSpacing(10);
						picture.moodinGridView.setLayoutParams(linearParams);
					
					
						
						
					}else if(piclsit.size()>3){
						
						LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) picture.moodinGridView.getLayoutParams(); // 取控件mGrid当前的布局参数
						linearParams.height = UnitTransformUtil.dip2px(context, 210);// 
						picture.moodinGridView.setHorizontalSpacing(10);
						picture.moodinGridView.setLayoutParams(linearParams);
					
						
						
					}
					picture.moodinGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
					picture.moodinGridView.setAdapter(mainGridViewAdapter);
					picture.moodinGridView.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							MainGridViewAdapter ad =(MainGridViewAdapter) arg0.getAdapter();
							Intent i=new Intent();
							i.putExtra("image", ad.getBitmap(arg2));
							i.setClass(context, PictureActivity.class);
							context.startActivity(i);
							
						}
						
					});
				}}else{
					
				}
			break;
		
			
		}
		
		return arg1;
	}
	

	public class Message{
		// 标题
		private TextView subject;

		// 心情内容
		private TextView moodinContent;
	}
	public class Picture{
		// 心情图片
		private NoScrollGridView moodinGridView;

	}
	
	
	public class Title{
		// 用户图像
		private ImageView ivUserLogo;

		// 用户昵称
		private TextView tvUserName;
		
		// 用户年龄
		private TextView tvUserAge;

		// 心情时间
		private TextView moodinTime;
	}
	private void seticon(String url){
		
		imageLoader.displayImage(url, title.ivUserLogo, options);
		
	
}}
