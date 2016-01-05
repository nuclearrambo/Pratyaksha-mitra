package com.pratyaksha;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pratyaksha.R;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCategory extends AppCompatActivity{
	
	//Views
	LinearLayout body;
	NumberProgressBar progressBar;
	ListView categoryListView;
	LinearLayout categoryActivity;
	//List of Posts
	private List<Post> posts = new ArrayList<Post>();
	
	//Variables

	String categoryURL;
	String nextPageURL;
	public int progress = 15;
	int currentPage = 1;
	int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount;

	//Android related
	Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_category);
		setViews();
		Intent intent = getIntent();
		categoryURL = intent.getStringExtra("cat_link");
		Log.d("Pratyaksha_DEBUG", "Category Link:" + categoryURL);
		//new FetchPost().execute(categoryURL);
		new FetchPostNano().get(categoryURL);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(android.R.id.home == item.getItemId()){
	    	finish();
	    }
		return super.onOptionsItemSelected(item);
	}
	
	public void setViews(){
//		progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
//		progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24));
//		progressBar.setProgress(20);
//		// retrieve the top view of our application
//		final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
//		decorView.addView(progressBar);
//
//		// Here we try to position the ProgressBar to the correct position by looking
//		// at the position where content area starts. But during creating time, sizes
//		// of the components are not set yet, so we have to wait until the components
//		// has been laid out
//		// Also note that doing progressBar.setY(136) will not work, because of different
//		// screen densities and different sizes of actionBar
//		ViewTreeObserver observer = progressBar.getViewTreeObserver();
//		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//		    @SuppressWarnings("deprecation")
//			@Override
//		    public void onGlobalLayout() {
//		        View contentView = decorView.findViewById(android.R.id.content);
//		        progressBar.setY(contentView.getY() - 10);
//
//		        ViewTreeObserver observer = progressBar.getViewTreeObserver();
//		        observer.removeGlobalOnLayoutListener(this);
//		    }
//		});
		categoryActivity = (LinearLayout)findViewById(R.id.categoryLL);
		Toolbar toolBar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);
		toolBar.setVisibility(View.VISIBLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		progressBar = (NumberProgressBar)findViewById(R.id.progressBar);
		progressBar.setProgress(progress);
		categoryListView = (ListView)findViewById(R.id.categoryListView);
		
	}

	private class FetchPostNano extends AsyncHttpClient {

		private String url;
		public void setUrl(String url){
			this.url = url;
		}

		Document content = null;
		AsyncHttpClient client = new AsyncHttpClient();
		public void get(String url){
			client.get(url, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
					String httpResponse = new String(bytes, StandardCharsets.UTF_8);
					content = Jsoup.parse(httpResponse);
					nextPageURL = content.select("a.next").select("a[href]").attr("abs:href");
					//Log.d("ViewCategory", "nextpageURL: "+nextPageURL);
					//populate UI Thread
					try{
						//Call function to populate content
						populateViews(content);

						//Add delay of 2s and remove progressBar from screen
						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								// Do something after 2s = 2000ms
								progressBar.setVisibility(View.GONE);
							}
						}, 2000);
					}
					catch(NullPointerException e){
						e.printStackTrace();
						retryButton();
					}
				}

				@Override
				public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

				}

				@Override
				public void onProgress(long bytesWritten, long totalSize) {
					super.onProgress(bytesWritten, totalSize);
					final long progressPercentage = bytesWritten/totalSize;
					Log.d("LoopJ","Progress: "+progressPercentage);
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							progress=progress+3;
							progressBar.setProgress(progress);
						}
					}, 25);
					//progressBar.setProgress((int)progressPercentage);
				}
			});
		}

	}
	
	public void populateViews(Document postContent){
		
		Elements requiredContent = postContent.select("div.article-wrapper");

		//Extract article block
		Elements articleBlocks = requiredContent.select("article.entry");
		for(Element articleBlock : articleBlocks){
			posts.add(new Post(articleBlock.select("h2").text(), 
					articleBlock.select("a[href]").attr("abs:href"),
					articleBlock.select("p").text(),
					articleBlock.select("img").attr("src")));
		}
		// Add footer view
        //View footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.infinite_scroll_layout, null, false);
		final Button loadMoreButton = new Button(getApplicationContext());
		loadMoreButton.setText("Next...");
		if(nextPageURL.startsWith("http")){
			loadMoreButton.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new FetchPostNano().get(nextPageURL);
					categoryListView.removeFooterView(loadMoreButton);
				}
				
			});
			categoryListView.addFooterView(loadMoreButton);
		}
		ArrayAdapter<Post> postAdapter = new categoryAdapter();
			//postAdapter.addAll(posts);
			//postAdapter.notifyDataSetChanged();
		categoryListView.setAdapter(postAdapter);
		setClickCallBack();
		if(currentPage > 1){
			categoryListView.setSelection((categoryListView.getCount() - categoryListView.getCount()/(currentPage))+1);
		}
		currentPage++;
	}
	
	private class categoryAdapter extends ArrayAdapter<Post> {

		public categoryAdapter() {
			super(ViewCategory.this, R.layout.category_item_layout, posts);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View itemView = convertView;
			if(itemView == null){
				itemView = getLayoutInflater().inflate(R.layout.category_item_layout, parent, false);
			}
			
			Post currentPost = posts.get(position);
			
			TextView headingText = (TextView)itemView.findViewById(R.id.heading);
			headingText.setText(currentPost.getHeading());
			
			TextView descriptionText = (TextView)itemView.findViewById(R.id.description);
			descriptionText.setText(currentPost.getDescription());
			
			ImageView preview = (ImageView)itemView.findViewById(R.id.previewImg);
			Glide.with(getApplicationContext())
			.load(currentPost.getImgURL())
			.into(preview);
			return itemView;
		}
		
	}
	
	private void setClickCallBack(){
		ListView list = (ListView)findViewById(R.id.categoryListView);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Post clickedPost = posts.get(position);
				Intent intent = new Intent(ViewCategory.this, ViewPost.class);
				intent.putExtra("post_link", clickedPost.getPostURL());
				startActivity(intent);
				
			}
			
		});
	}
	
		public void retryButton(){
			Toast.makeText(getApplicationContext(), "Network connection failed!", Toast.LENGTH_LONG).show();
			RelativeLayout rl = new RelativeLayout(ViewCategory.this);
			Button refresh = new Button(getApplicationContext());
			refresh.setText("TRY AGAIN");
			refresh.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
					Intent refreshIntent = new Intent(getIntent());
					startActivity(refreshIntent);
					overridePendingTransition(0, 0);
				}
				
			});
			
			@SuppressWarnings("deprecation")
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
			lp.addRule(RelativeLayout.CENTER_VERTICAL, refresh.getId());
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, refresh.getId());
			rl.addView(refresh, lp);
			rl.bringToFront();
			categoryActivity.addView(rl);
		}



}
