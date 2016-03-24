package com.pratyaksha;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ViewPost extends AppCompatActivity{

	//Views
	TextView postHeading;
	TextView postBody;
	ImageView postImage;
	LinearLayout postbody;
	NumberProgressBar progressBar;

	//Elements
	String heading;
	String image;
	String publishDate;

	//Variables
	String postURL;
	String imageURL;
	Document postContent;
	int progress = 15;
	String summary;

	//Android related
	Handler handler = new Handler();
	Toolbar toolBar;

	//Cache Related
	SimpleDiskCache cache;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_view_post);
		toolBar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);
		toolBar.setVisibility(View.VISIBLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		progressBar = (NumberProgressBar)findViewById(R.id.progressBar);
		progressBar.setProgress(progress);
		setViews();

		Intent intent = getIntent();
		postURL = intent.getStringExtra("post_link");
		imageURL = intent.getStringExtra("image_link");
		//FetchPost fetch = new FetchPost();
		//fetch.execute();

		//Get post Content
		FetchPostNano nano = new FetchPostNano();
		nano.setUrl(postURL);
		nano.get(postURL);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_viewpost, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.refresh:
				Intent intent = getIntent();
				finish();
				startActivity(intent);
				return true;
			case R.id.share:
				shareIt(findViewById(R.id.share));
				return true;
		}
	    if(android.R.id.home == item.getItemId()){
	    	finish();

	    }
		return super.onOptionsItemSelected(item);
	}

	public void setupFacebookShareIntent(String title, String summary, String url) {
//		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//
//		final String urlToShare = url;
//		final String summaryToShare = summary;
////		final Button shareButton = (Button)findViewById(R.id.fb_share_button);
//
//		fab.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//Write here anything that you wish to do on click of FAB
//				shareIt(fab, summaryToShare, urlToShare);
//			}
//		});

//		shareButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				shareIt(shareButton, summaryToShare, urlToShare);
////				try {
////					Intent intent1 = new Intent();
////					intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
////					intent1.setAction("android.intent.action.SEND");
////					intent1.setType("text/plain");
////					intent1.putExtra("android.intent.extra.TEXT", urlToShare);
////					startActivity(intent1);
////				} catch (Exception e) {
////					// If we failed (not native FB app installed), try share through SEND
////					Intent intent = new Intent(Intent.ACTION_SEND);
////					String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
////					intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
////					startActivity(intent);
////				}
//
//			}
//		});


//		ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
//
//		ShareDialog shareDialog;
//		FacebookSdk.sdkInitialize(getApplicationContext());
//		shareDialog = new ShareDialog(this);
//
//		ShareLinkContent content = new ShareLinkContent.Builder()
//				.setContentUrl(Uri.parse("https://developers.facebook.com"))
//				.build();
//		shareButton.setShareContent(content);
//		shareDialog.show(content);

	}

	public void shareIt(View view){
		//sharing implementation
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = summary;

		PackageManager pm = view.getContext().getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(sharingIntent, 0);
		for(final ResolveInfo app : activityList) {

			String packageName = app.activityInfo.packageName;
			Log.d("PACKAGES",packageName);
			Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
			targetedShareIntent.setType("text/plain");
			targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "share");
			if(!packageName.contains("com.google.drive") ||packageName.contains("com.estrongs")) {
				if (TextUtils.equals(packageName, "com.facebook.katana")) {
					targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, postURL);
				} else {
					targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + "Read More at: " + postURL);
				}

				targetedShareIntent.setPackage(packageName);
				targetedShareIntents.add(targetedShareIntent);
			}

		}

		Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Share");

		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
		startActivity(chooserIntent);

	}

	private class FetchPostNano extends AsyncHttpClient{

		private String url;
		public void setUrl(String url){
			this.url = url;
		}

		AsyncHttpClient client = new AsyncHttpClient();
		public void get(String url){
			final String postURL = url;
			client.get(url, new AsyncHttpResponseHandler() {
				boolean postLoaded = false;

				@Override
				public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

					try {
						String httpResponse = new String(bytes, CharEncoding.UTF_8);
						postContent = Jsoup.parse(httpResponse);
						//Log.d("DEBUG", httpResponse);

						MainActivity.cache.put(postURL, postContent.toString());
						Log.d("CACHE", "Page Store in Cache");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e1) {
						Log.d("CACHE", "Failed to store page to Cache");
						e1.printStackTrace();
					} catch (NullPointerException e2) {
						e2.printStackTrace();
					}
					//populate UI Thread
					try {
						//Call function to populate content
						populateViews();

						//Add delay of 2s and remove progressBar from screen
						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								// Do something after 5s = 5000ms
								progressBar.setVisibility(View.GONE);
							}
						}, 2000);
					} catch (NullPointerException e) {
						retryButton();
					}

				}

				@Override
				public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
					if (!postLoaded) {
						loadOffline(postURL);
					}

				}

				@Override
				public void onProgress(long bytesWritten, long totalSize) {
					super.onProgress(bytesWritten, totalSize);
					final long progressPercentage = bytesWritten / totalSize;
					Log.d("LoopJ", "Progress: " + progressPercentage);
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							progress = progress + 3;
							progressBar.setProgress(progress);
						}
					}, 25);
					//progressBar.setProgress((int)progressPercentage);
				}

				@Override
				public void onRetry(int retryNo) {
					super.onRetry(retryNo);
					//Toast.makeText(getApplicationContext(), "Network trouble! Retry attempt: " + retryNo, Toast.LENGTH_LONG).show();

					if (!postLoaded) {
						postLoaded = loadOffline(postURL);
					}

					if (!postLoaded) {
						postHeading.setText("ERROR! Retrying:/ \n Attempt #" + retryNo);
					}
				}
			});
		}

	}

	private boolean loadOffline(String postURL){
		boolean flag = false;
		try {
			SimpleDiskCache.StringEntry meta = MainActivity.cache.getString(postURL);
			try {
				postContent = Jsoup.parse(meta.getString());
				Log.d("CACHE_DBG", postContent.toString());
				populateViews();
				Toast.makeText(getApplicationContext(), "Loaded article from offline cache", Toast.LENGTH_SHORT).show();
				flag = true;
			} catch (NullPointerException e) {
				retryButton();
				Toast.makeText(getApplicationContext(), "No network, No cache! Retry later!", Toast.LENGTH_LONG).show();
				postHeading.setText("ERROR :(");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.e("CACHE_ERROR", "IO Exception, no cached article found");
			postHeading.setText("ERROR :(");
			retryButton();
		}
		return flag;
	}

	private class FetchPost extends AsyncTask<Void, Void, Document>
	{

		@Override
		protected Document doInBackground(Void... URL) {
			// TODO Auto-generated method stub
			try {
				postContent = Jsoup.connect(postURL).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Pratyaksha_ERROR", "Error fetching post");
			}
			return postContent;
		}
		
		@Override
		protected void onPostExecute(Document postContent){
			//Update progress
			progress = progress + 90;
			progressBar.setProgress(progress);
			try{
				//Call function to populate content
				populateViews();

				//Add delay of 2s and remove progressBar from screen
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Do something after 5s = 5000ms
						progressBar.setVisibility(View.GONE);
					}
				}, 2000);
			}
			catch(NullPointerException e){
				retryButton();
			}
		}
		
	}
	
	public void setViews(){
//		progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
//		progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24));
//		progressBar.setProgress(20);
//
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
		postHeading = (TextView)findViewById(R.id.postHeading);
		postBody = (TextView)findViewById(R.id.postBody);
		postImage = (ImageView)findViewById(R.id.featuredImage);
		postbody = (LinearLayout)findViewById(R.id.postlayout);

	}
	
	public void populateViews(){
		try {
			//heading
			heading = postContent.select("h1.entry-title").text();

			//Publish Date
			publishDate = postContent.select("time.published").first().text();
			postBody.setText(publishDate);
			postBody.setTextSize(14);

			//Add a horizontal seperator line
			View line = new View(this);
			line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
			line.setBackgroundColor(Color.rgb(51, 51, 51));
			postbody.addView(line);

			//body
			Elements bodyDiv = postContent.select("div.entry-content");
			summary = extractPara(bodyDiv) + "...";
			//featured img
			if (imageURL == null) {
				imageURL = postContent.select("div.entry-content")
						.select("img")
						.first()
						.attr("abs:src");
			}
			postHeading.setText(heading);
			toolBar.setTitle(heading);
			Glide.with(getApplicationContext())
					.load(imageURL)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.pratyaksha)
					.into(postImage);

			setupFacebookShareIntent(heading, summary, postURL);

			//Set selectable option to true;
			postBody.setTextIsSelectable(true);
		}
		catch(IllegalArgumentException e){
			e.printStackTrace();
		}

	}
	
	public String extractPara(Elements bodyDiv){
		int paraCount = 0;
		Elements paras = bodyDiv.select("p");
		for(Element para : paras){
			paraCount++;
			Element paraImg = para.select("img").first();
			if(paraImg != null & paraCount != 1)
			{
				ImageView paraContentImg = new ImageView(ViewPost.this);
				Glide.with(ViewPost.this)
				.load(paraImg.attr("abs:src"))
				.asBitmap()
				.into(paraContentImg);
				postbody.addView(paraContentImg);
			}
			TextView text = new TextView(ViewPost.this);
			text.setTextSize(20);
			text.setLineSpacing(5.0f , 1.0f);
			text.setPadding(8, 5, 5, 5);
			text.setText("\t" + para.text() + "\n");
			postbody.addView(text);
		}
		return StringUtils.substring(paras.text(), 0, 150);
	}
	
	public void retryButton(){
		Toast.makeText(getApplicationContext(), "Network connection failed!", Toast.LENGTH_LONG).show();
		RelativeLayout rl = new RelativeLayout(ViewPost.this);
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
		postbody.addView(rl);
	}
}
