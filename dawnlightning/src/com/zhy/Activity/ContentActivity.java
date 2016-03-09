package com.zhy.Activity;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;




import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;


import android.widget.ImageView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dawnlightning.ucqa.R;
import com.mob.tools.utils.UIHandler;
import com.zhy.Adapter.ContentAdapter;

import com.zhy.Bean.BaseBean;
import com.zhy.Bean.CommentBean;

import com.zhy.Bean.DetailedBean;
import com.zhy.Bean.PicsBean;

import com.zhy.Db.SharedPreferenceDb;

import com.zhy.Dialog.TitlePopup;

import com.zhy.Share.ShareModel;
import com.zhy.Share.ShareTool;
import com.zhy.Util.AppUtils;
import com.zhy.Util.HttpConstants;
import com.zhy.Util.HttpUtil;

import com.zhy.Util.ResultCallback;
import com.zhy.View.TitleBar;


public class ContentActivity extends BaseActivity implements OnClickListener, PlatformActionListener, Callback{


	
	private ArrayList<String> message;
	private Context context;
	private DetailedBean detailedBean;//详细

	private ListView contentlist;

	private TextView totalreplay;
	private ImageView write;
	private ImageView commonlist;
	private ImageView share;
	private ContentAdapter contentadapter;
	private final static String [] fileds={"开头","正文","图片"};
	private HashMap<String,DetailedBean> content;
	private TitleBar contenttitlebar;
	private int page=1;
	private int totlenum=0;
	private TitlePopup titlePopup;
	private ShareTool Share;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		initview();
		initdata();
		getcontent();
	}

	private void initview(){
		ShareSDK.initSDK(this);
		contenttitlebar=(TitleBar)findViewById(R.id.content_TitleBar);	
		contenttitlebar.setBackgroundColor(getResources().getColor(R.color.blue));
		contentlist=(ListView) findViewById(R.id.contentListView);
		totalreplay=(TextView) findViewById(R.id.action_comment_count);
		write=(ImageView) findViewById(R.id.action_write_comment);
		commonlist=(ImageView) findViewById(R.id.action_view_comment);
		share=(ImageView) findViewById(R.id.action_share);
		
		
	}
	private void initdata(){
		context=this;
		Intent intent=getIntent();
		message=intent.getStringArrayListExtra("message");
		totlenum=Integer.parseInt(message.get(message.size()-1));
		totalreplay.setText(String.valueOf(totlenum));
		
			
		
		
		contenttitlebar.showLeftAndRight("详细", getResources().getDrawable(R.drawable.app_back),null, new backlistener(), null);
	
		
		
	}
	private void initevent(){
		
		write.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				showUpdataDialog();
			}
			
		});
		
		commonlist.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(totlenum!=0){
				Intent i=new Intent();
				i.setClass(context, CommentActivity.class);
				i.putStringArrayListExtra("message", message);
				startActivity(i);
				}else{
					Toast.makeText(context, "没有评论", Toast.LENGTH_SHORT).show();
				}
				
			}
			
		});
		share.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				share();
				
			}
			
		});
	}
	class backlistener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
			finish();
		}
	
	}
	class commentlistener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			titlePopup.show(v);
		}
	
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View arg0) {
		
		
	}
	public void setcomment(final String str){
		if(AppUtils.checkNetwork(context)==true){
		final List<NameValuePair> allP=new ArrayList<NameValuePair>();
		allP.add(new BasicNameValuePair("commentsubmit","true"));
		allP.add(new BasicNameValuePair("formhash",new SharedPreferenceDb(context).getformhash()));
		allP.add(new BasicNameValuePair("id",message.get(3).toString()));
		allP.add(new BasicNameValuePair("idtype","bwztid"));
		allP.add(new BasicNameValuePair("message",str));
		new	HttpUtil().doPost(HttpConstants.HTTP_COMMENT.replace("!", message.get(1)), allP, new ResultCallback() {
			
			@SuppressLint("NewApi")
			@Override
			public void getReslt(String result) {
				// TODO 自动生成的方法存根
				if(!result.isEmpty() ){
				
					BaseBean b=JSON.parseObject(result, BaseBean.class);
					if("0".equals(b.getCode().toString().trim())){
						
						
						 Toast.makeText(ContentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
						 totlenum=totlenum+1;
						 totalreplay.setText(String.valueOf(totlenum));
						 
					}else{
						Toast.makeText(ContentActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
					
					}
				
					}else{
						Toast.makeText(ContentActivity.this, "服务器响应失败", Toast.LENGTH_SHORT).show();
						
					}
				
			}
			
			
		});}else{
			Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
		}
	
	}
	
	@SuppressLint("NewApi")
	private void getcontent(){
		if(AppUtils.checkNetwork(context)==true){
		page=1;
		List<NameValuePair> allP=new ArrayList<NameValuePair>();
		allP.add(new BasicNameValuePair("uid",message.get(4)));
		allP.add(new BasicNameValuePair("id",message.get(3)));
		allP.add(new BasicNameValuePair("page",String.valueOf(page)));
		initProgressDialog();
		new	HttpUtil().doPost(HttpConstants.HTTP_CONSULT_DETAIL, allP, new ResultCallback(){

			@Override
			public void getReslt(String result) {
				if(!result.isEmpty() ){
					BaseBean b=JSON.parseObject(result, BaseBean.class);
					Log.e("msg", b.getData());
					if("0".equals(b.getCode())){
						//try{
						JSONObject j=(JSONObject) JSON.parse(b.getData());
						JSONObject js=(JSONObject) JSON.parse(j.getString("bwzt"));
						detailedBean=new DetailedBean();
						 detailedBean.setAge(js.getString("age"));
						 detailedBean.setContent(js.getString("message"));
						 detailedBean.setDatetime(js.getString("dateline"));
						 detailedBean.setSubject(js.getString("subject"));
						 detailedBean.setUid(js.getString("uid"));
						 detailedBean.setUsename(js.getString("username"));
						 detailedBean.setAvatar_url(js.getString("avatar_url"));
						 detailedBean.setName(js.getString("name"));
						 detailedBean.setComment(JSON.parseArray(js.getString("replylist"),CommentBean.class));
						 JSONArray jsonArray = JSONArray.parseArray(js.getString("pics")); 
						 List<PicsBean> list=new ArrayList<PicsBean>();
						 if(jsonArray!=null){
						 for(int k=0;k<jsonArray.size();k++){  
				               System.out.print(jsonArray.get(k) + "\t");  
				               Log.e("strpic",jsonArray.get(k).toString());
				               JSONObject objpic=(JSONObject) JSON.parse(jsonArray.get(k).toString());
				              
				               PicsBean p=new PicsBean( objpic.getString("picurl"),"");
				               list.add(p);
				           }  
						
						
						 detailedBean.setPics(list);
						 }
						 content=new HashMap<String,DetailedBean>();
						 for(int i=0;i<fileds.length;i++){
							 Log.e("Tag",fileds[i]);
							 content.put(fileds[i], detailedBean);
						 }
					
						
						 contentadapter=new ContentAdapter(context,content);
						 contentlist.setAdapter(contentadapter);
						 initevent();
						 close();
					
						
					}else{
						Toast.makeText(context, "获取详细失败", Toast.LENGTH_SHORT).show();
						 close();
						
					}
				}else{
					Toast.makeText(context, "服务器响应失败", Toast.LENGTH_SHORT).show();
					 close();
					
					
					}
				
				
			}
		
	
		
		});}else{
			Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();}
		}
	public  Bitmap decodeResource(Resources resources, int id) {  
		  
	    int densityDpi = resources.getDisplayMetrics().densityDpi;  
	    Bitmap bitmap;  
	    TypedValue value = new TypedValue();  
	    resources.openRawResource(id, value);  
	    BitmapFactory.Options opts = new BitmapFactory.Options();  
	    opts.inPreferredConfig = Bitmap.Config.ALPHA_8;  
	    if (densityDpi > DisplayMetrics.DENSITY_HIGH) {  
	        opts.inTargetDensity = value.density;  
	        bitmap = BitmapFactory.decodeResource(resources, id, opts);  
	    }else{  
	        bitmap = BitmapFactory.decodeResource(resources, id);  
	    }  
	  
	    return bitmap;  
	}  
	
	public void showToast(String str){
		Toast.makeText(ContentActivity.this, str, Toast.LENGTH_SHORT).show();
	}
	
	
	
	protected void showUpdataDialog() {

        final com.zhy.Dialog.CustomDialogUpd.Builder builder = new com.zhy.Dialog.CustomDialogUpd.Builder(context);
        builder.setTitle("发表评论");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                // TODO Auto-generated method stub
                String  content= builder.getEditText().getText().toString().trim();
                setcomment(content);
                dialog.dismiss();
            }
        });
		// 当点取消按钮时进行登录
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

        builder.create().show();
    }

	 private void share(){
		 Share = new ShareTool(context);
         Share.setPlatformActionListener(ContentActivity.this);
         ShareModel model = new ShareModel();
         model.setImageUrl("https://ucqa.dawnlightning.com/capi/app/lightup_logo.png");
         model.setText(detailedBean.getContent());
         model.setTitle(detailedBean.getSubject());
         model.setUrl("https://ucqa.dawnlightning.com/capi/turn.php?a=1&b=2&c=3");
         Share.initShareParams(model);
         Share.showShareWindow();
	 }
	
	  @Override
	    public void onCancel(Platform arg0, int arg1)
	    {	
		 
	        Message msg = new Message();
	        msg.what = 0;
	        UIHandler.sendMessage(msg, this);
	        Share.dismiss();
	    }

	    @Override
	    public void onComplete(Platform plat, int action, HashMap<String, Object> res)
	    {
	        Message msg = new Message();
	        Log.e("Tag", "sharecomplete");
	        msg.arg1 = 1;
	        msg.arg2 = action;
	        msg.obj = plat;
	        UIHandler.sendMessage(msg, this);
	        Share.dismiss();
	    }

	    @Override
	    public void onError(Platform arg0, int arg1, Throwable arg2)
	    {	 Log.e("Tag", "error");
	        Message msg = new Message();
	        msg.what = 1;
	        UIHandler.sendMessage(msg, this);
	        Share.dismiss();
	    }

	    @Override
	    public boolean handleMessage(Message msg)
	    {
	        int what = msg.what;
	        if (what == 1)
	        {
	            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
	            Share.dismiss();
	        }else{
	       
	            Share.dismiss();
	        }
	        
	        return false;
	    }

	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	           
	           finish();
	        }
	        return super.onKeyDown(keyCode, event);
	    }

		@Override
		protected void onDestroy() {
			System.gc();
			super.onDestroy();
		}
	
}
