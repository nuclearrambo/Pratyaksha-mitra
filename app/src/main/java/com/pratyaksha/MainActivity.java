package com.pratyaksha;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.facebook.appevents.AppEventsLogger;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity{
	String[] homeURL = new String[]{"http://www.pratyaksha-mitra.com/"};
	TextView featuredText;
	ImageView featuredView;
	LinearLayout linearlayout;
	Map<String, List<String>> slider_elements = new HashMap<String, List<String>>();
	static String IMG_CACHE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PRTKSH_CACHE/";
	static String CONTENT_CACHE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PRTKSH_CACHE/SITE_CONTENT";
	File imgs = new File(IMG_CACHE_FOLDER);
	File site_content = new File(CONTENT_CACHE_FOLDER);
	Handler mHandler;
	int featuredImgCount = 0;
	int progress=0;
	NumberProgressBar progressBar;
	Document mainContent;
	Document htmlContent = null;
	private ShareActionProvider mShareActionProvider;
	Handler handler = new Handler();
	
	private Context context;
	ImageButton previous, next;
	int id=0;

	//Drawer
	Toolbar toolBar;
	private ListView mDrawerList;
	private ArrayAdapter<String> mAdapter;
	@SuppressWarnings("deprecation")
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private String mActivityTitle;

	//Initialize Array list to extract all the slider content
	//and place each element in corresponding array location
	List<String> hrefList = new ArrayList<String>();
	List<String> h2List = new ArrayList<String>();
	List<String> summaryList = new ArrayList<String>();
	List<String> imageList = new ArrayList<String>();

	//Array list for rest of the links to internal pages
	List<String> midContentLinks = new ArrayList<String>();
	List<String> midContentText = new ArrayList<String>();
	public static SimpleDiskCache cache;
	//Define colours to choose from
	String[] colors = {"#3f51b5",
			"#5677fc",
			"#03a9f4",
			"#00bcd4",
			"#40c4ff",
			"#996680",
			"#ff5177",
			"#7c4dff",
			"#ff5722",
			"#ff9800",
			"#009688",
			"#259b24"};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
	   super.onRestoreInstanceState(savedState);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.menu_layout, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.refresh);


		// Return true to display menu
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);



//		// create new ProgressBar and style it
//		progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
//		progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24));
//		progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.progressbar), PorterDuff.Mode.SRC_IN);
//		progressBar.setProgress(progress);
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
//		    @Override
//		    public void onGlobalLayout() {
//		        View contentView = decorView.findViewById(android.R.id.content);
//		        progressBar.setY(contentView.getTop());
//
//		        ViewTreeObserver observer = progressBar.getViewTreeObserver();
//		        observer.removeGlobalOnLayoutListener(this);
//		    }
//		});
		setContentView(R.layout.activity_main_feed);

		toolBar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);
		toolBar.setVisibility(View.VISIBLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		progressBar = (NumberProgressBar)findViewById(R.id.progressBar);

		//featuredText = (TextView)findViewById(R.id.featuredSliderText);
		//featuredView = (ImageView)findViewById(R.id.featuredView);
		//featuredView.setVisibility(View.GONE);
		linearlayout= (LinearLayout)findViewById(R.id.svLinearLayout);
		linearlayout.setPadding(5, 5, 5, 5);

		//Slider navigation
//		previous = (ImageButton)findViewById(R.id.prev);
//
//		next = (ImageButton)findViewById(R.id.next);


		if(!imgs.exists()){
			imgs.mkdir();
		}
		if(!site_content.exists()){
			site_content.mkdir();
		}

		if (!Glide.isSetup()) {
			   GlideBuilder gb = new GlideBuilder(this);
			   DiskCache dlw = DiskLruCacheWrapper.get(imgs, 250 * 1024 * 1024);
			   gb.setDiskCache(dlw);
			   Glide.setup(gb);
			}
		try {
			if(!SimpleDiskCache.SDCFlag) {
				cache = SimpleDiskCache.open(site_content, 1, 1000 * 1024);
				cache.setSDCFlag(true);
				Log.d("CACHE", "CACHE folder opened");
			}
			else{
				Log.d("CACHE", "CACHE folder opened previously, continuing...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		mHandler = new Handler();
	//	if(isNetworkOnline()){
			FetchFeedNano nano = new FetchFeedNano();
			nano.setUrl(homeURL[0]);
			nano.get(homeURL[0]);
	//	}
//	//	else{
//			try{
//				SimpleDiskCache.StringEntry meta = cache.getString(homeURL[0]);
//				Document doc = Jsoup.parse(meta.toString());
//				getFeaturedContent(doc);
//				getLinksToInternalPosts(doc);
//				populateFeaturedContent(imageList);
//			} catch (IOException e1) {
//				Log.e("CACHE", getCallingActivity().toString()+"Not able to parse cache");
//				retryButton();
//			} catch(NullPointerException e1){
//				//retryButton();
//				Toast.makeText(getApplicationContext(), "Network connection failed!", Toast.LENGTH_LONG).show();
//				RelativeLayout rl = new RelativeLayout(this);
//				Button refresh = new Button(this);
//				refresh.setText("TRY AGAIN");
//				refresh.setOnClickListener(new OnClickListener(){
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						finish();
//						Intent refreshIntent = new Intent(getIntent());
//						startActivity(refreshIntent);
//						overridePendingTransition(0, 0);
//					}
//
//				});
//				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
//				lp.addRule(RelativeLayout.CENTER_VERTICAL, refresh.getId());
//				lp.addRule(RelativeLayout.CENTER_HORIZONTAL, refresh.getId());
//				rl.addView(refresh, lp);
//				linearlayout.addView(rl);
//				Log.e("CACHE", "No cache, no network");
//			}
//		}

		//Drawer
		mDrawerList = (ListView)findViewById(R.id.navList);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mActivityTitle = getTitle().toString();
		setupDrawer();
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		//Start background service
		this.context = this;
		Intent alarm = new Intent(this.context, PeriodicTaskReceiver.class);

		boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, alarm, PendingIntent.FLAG_NO_CREATE)!= null);
		if(alarmRunning == false){
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
			AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 1000*60*3, pendingIntent);

		}
	}

	private class FetchFeedNano extends AsyncHttpClient {
		private String url;
		public void setUrl(String url){
			this.url = url;
		}

		AsyncHttpClient client = new AsyncHttpClient();
		public void get(final String url){
			final String postURL = url;
			client.get(postURL, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String httpResponse;
					Document doc;
					try{
						httpResponse = new String(responseBody, CharEncoding.UTF_8);
						doc = Jsoup.parse(httpResponse);
						progress = progress+5;
						progressBar.setProgress(progress);

						//Fills all the "list" objects with featured content elements
						getFeaturedContent(doc);

						//Populate the featured slider
						populateFeaturedContent(imageList);

						getLinksToInternalPosts(doc);
						//Throw data on debug console
						for(int i = 0; i < hrefList.size(); i++){
							Log.d("Pratyaksha_DEBUG", h2List.get(i)+"==>"+imageList.get(i)+"==>"+summaryList.get(i)+"==>"+hrefList.get(i));

						}
						Toast.makeText(getApplicationContext(), "Will load images", Toast.LENGTH_SHORT).show();



						//Finally store in cache
						try {
							cache.put(url, doc.toString());
//							Log.d("CACHE", "Page Store in Cache. URL:" + postURL + "content" + doc.toString());
						} catch (IOException e) {
							Log.d("CACHE", "Failed to store page to Cache");
							e.printStackTrace();
						}
						SimpleDiskCache.StringEntry meta = cache.getString(postURL);
//						Log.d("CACHE", "Cache test: "+meta.getString());

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
						Log.e("CACHE", "NullPointerException in FetchFeedNano");
						e.printStackTrace();
					}
					catch(IOException e){
						e.printStackTrace();
						Log.e("CACHE", "Error storing data in FetchFeed()");
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					try {
						SimpleDiskCache.StringEntry meta = MainActivity.cache.getString(postURL);
						Document doc = Jsoup.parse(meta.getString());
						Log.d("CACHE", "MainActivity content " +doc.toString());
						progress = progress+5;
						progressBar.setProgress(progress);

						//Fills all the "list" objects with featured content elements
						getFeaturedContent(doc);

						//Populate the featured slider
						populateFeaturedContent(imageList);

						getLinksToInternalPosts(doc);
						//Throw data on debug console
						for(int i = 0; i < hrefList.size(); i++){
							Log.d("Pratyaksha_DEBUG", h2List.get(i)+"==>"+imageList.get(i)+"==>"+summaryList.get(i)+"==>"+hrefList.get(i));

						}
						Toast.makeText(getApplicationContext(), "Will load images", Toast.LENGTH_SHORT).show();


					} catch (IOException e) {
						e.printStackTrace();
						retryButton();
						Toast.makeText(getApplicationContext(), "No network, No cache!", Toast.LENGTH_LONG).show();
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
				}
			});
		}


	}
//	private class FetchFeed extends AsyncTask<String, Void, Document> {
//
//		@Override
//		protected Document doInBackground(String... URL) {
//			// TODO Auto-generated method stub
//
//			try {
//				htmlContent = Jsoup.connect(URL[0]).get();
//				try {
//					MainActivity.cache.put(URL[0], htmlContent.toString());
//					Log.d("CACHE", "Page Stored in Cache");
//				}catch(IOException e1){
//					Log.e("CACHE", getCallingActivity().toString()+"Could not store cache");
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Log.e("Pratyaksha_ERROR", "Error fetching post");
//			}
//			return htmlContent;
//		}
//
//		@Override
//		protected void onPostExecute(Document doc){
//			try{
//				progress = progress+5;
//				progressBar.setProgress(progress);
//
//				//Fills all the "list" objects with featured content elements
//				getFeaturedContent(doc);
//				getLinksToInternalPosts(doc);
//				//Throw data on debug console
//				for(int i = 0; i < hrefList.size(); i++){
//					Log.d("Pratyaksha_DEBUG", h2List.get(i)+"==>"+imageList.get(i)+"==>"+summaryList.get(i)+"==>"+hrefList.get(i));
//
//				}
//				Toast.makeText(getApplicationContext(), "Will load images", Toast.LENGTH_SHORT).show();
//
//				//Populate the featured slider
//				populateFeaturedContent(imageList);
//			}
//			catch(NullPointerException e){
//				try{
//					SimpleDiskCache.StringEntry meta = cache.getString(homeURL[0]);
//					doc = Jsoup.parse(meta.toString());
//
//					getFeaturedContent(doc);
//					getLinksToInternalPosts(doc);
//					populateFeaturedContent(imageList);
//				} catch (IOException e1) {
//					Log.e("CACHE", getCallingActivity().toString()+"Not able to parse cache");
//					retryButton();
//				} catch(NullPointerException e1){
//					retryButton();
//					Log.e("CACHE", "No cache, no network");
//				}
//
//
//			}
//
//		}
//
//	}

	private class FetchPostsFromPage extends AsyncTask<String, Void, Wrapper> {

		@Override
		protected Wrapper doInBackground(String... URL) {
			// TODO Auto-generated method stub
			Document htmlContent = null;
			try {
				htmlContent = Jsoup.connect(URL[0]).get();
				try {
					MainActivity.cache.put(URL[0], htmlContent.toString());
				}catch(IOException e){
					Log.d("CACHE", getCallingActivity().toString()+"Could not store CACHE"+e);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Pratyaksha_ERROR", "Error fetching post from " + URL[0]);
			}
			Wrapper wrapper = new Wrapper();
			wrapper.htmlContent = htmlContent;
			wrapper.categoryURL = URL[0];
			wrapper.category = URL[1];
			return wrapper;
		}


		@Override
		protected void onPostExecute(Wrapper wrapper){

			//Create content list linearlayout
			final LinearLayout articleListLL = new LinearLayout(MainActivity.this);
			articleListLL.setOrientation(LinearLayout.VERTICAL);
			//Start with invisible layout displaying only the category name
			articleListLL.setVisibility(View.GONE);

			//RelativeLayout categoryLayout = new RelativeLayout(MainActivity.this);
			LinearLayout categoryLayout = new LinearLayout(MainActivity.this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 15, 0, 5);
			categoryLayout.setOrientation(LinearLayout.VERTICAL);
			float scale = getResources().getDisplayMetrics().density;
			int paddingDp = (int)(5 * scale+0.5f);
			categoryLayout.setLayoutParams(params);
			try{
				//ArrayList<String> categoryChildList = new ArrayList<String>();
				progressBar.setProgress(progress+10);
				Document doc = wrapper.htmlContent;
				final String category = wrapper.category;
				final String categoryURL = wrapper.categoryURL;
				Elements articles = doc.select("article.entry");
				TextView categoryName = new TextView(MainActivity.this);
				Random random = new Random();

				//Set Visual parameters of the Category.
				categoryName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				int headlineBaseColor = Color.parseColor(colors[random.nextInt((colors.length - 0)) + 0]);
				categoryName.setBackgroundColor(headlineBaseColor);
				categoryName.setText(category);
				Drawable toggle = getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp);
				categoryName.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,toggle,null);
				categoryName.setTextColor(getResources().getColor(R.color.headline_color));
				categoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
				categoryName.setGravity(Gravity.CENTER);
				categoryName.bringToFront();
				categoryName.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						if(articleListLL.getVisibility() == View.GONE){
							//articleListLL.setVisibility(View.VISIBLE);
							expand(articleListLL);
						}
						else{
							//articleListLL.setVisibility(View.GONE);
							collapse(articleListLL);
						}
//						Intent catIntent = new Intent(getApplicationContext(), ViewCategory.class);
//						catIntent.putExtra("cat_link", categoryURL);
//						startActivity(catIntent);
					}
				});
				categoryLayout.addView(categoryName);
				int colorStep = 10;
				int currentColor = 10;
				int baseColor = 70;

				for(final Element article : articles){
					currentColor = currentColor + colorStep;
					progress = progress+1;
					progressBar.setProgress(progress);

					//categoryChildList.add(article.select("h2").text());
					TextView headline = new TextView(MainActivity.this);
					headline.setId(id);
					headline.setText(article.select("h2").text());
					headline.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
					headline.setTextColor(Color.argb(255, baseColor+currentColor, baseColor+currentColor, 255));
					headline.setPadding(5, 7, 5, 7);
					articleListLL.addView(headline);
					headline.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getApplicationContext(), ViewPost.class);
							intent.putExtra("post_link", article.select("a[href]").attr("abs:href"));
							startActivity(intent);
						}

					});
				}
				categoryLayout.addView(articleListLL);
				categoryLayout.setBackgroundResource(R.drawable.card_background_selector);
				linearlayout.addView(categoryLayout);
				id=id+1;
				//linearlayout.addView(headingsListView);
			}catch(NullPointerException e){
				retryButton();
			}

		}

	}

//	public void populateCategories(Wrapper wrapper){
//		final LinearLayout articleListLL = new LinearLayout(MainActivity.this);
//		articleListLL.setOrientation(LinearLayout.VERTICAL);
//		articleListLL.setVisibility(View.GONE);
//		LinearLayout categoryLayout = new LinearLayout(MainActivity.this);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		params.setMargins(0,10,0,5);
//		categoryLayout.setOrientation(LinearLayout.VERTICAL);
//		float scale = getResources().getDisplayMetrics().density;
//		int paddingDp = (int)(5 * scale+0.5f);
//		categoryLayout.setLayoutParams(params);
//		try {
//			//ArrayList<String> categoryChildList = new ArrayList<String>();
//			progressBar.setProgress(progress + 10);
//			Document doc = wrapper.htmlContent;
//			final String category = wrapper.category;
//			final String categoryURL = wrapper.categoryURL;
//			Elements articles = doc.select("article.entry");
//			TextView categoryName = new TextView(MainActivity.this);
//			Random random = new Random();
//
//			//Set Visual parameters of the Category.
//			categoryName.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//			int headlineBaseColor = Color.parseColor(colors[random.nextInt((colors.length - 0)) + 0]);
//			categoryName.setBackgroundColor(headlineBaseColor);
//			categoryName.setText(category);
//			Drawable toggle = getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp);
//			categoryName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, toggle, null);
//			categoryName.setTextColor(getResources().getColor(R.color.headline_color));
//			categoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
//			categoryName.setGravity(Gravity.CENTER);
//			categoryName.bringToFront();
//
//			categoryName.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					if (articleListLL.getVisibility() == View.GONE) {
//						//articleListLL.setVisibility(View.VISIBLE);
//						expand(articleListLL);
//					} else {
//						//articleListLL.setVisibility(View.GONE);
//						collapse(articleListLL);
//					}
////						Intent catIntent = new Intent(getApplicationContext(), ViewCategory.class);
////						catIntent.putExtra("cat_link", categoryURL);
////						startActivity(catIntent);
//				}
//			});
//
//			categoryLayout.addView(categoryName);
//			linearlayout.addView(categoryLayout);
//			id = id + 1;
//		}
//			catch(NullPointerException e){
//				retryButton();
//			}
//	}

	//Fills #h2List, #hrefList, #imageList, #summaryList
	public void getFeaturedContent(Document doc){

		//Extract slider content
		Elements slider_articles = null;
		try{
			slider_articles = doc.select("article.slide_item");
		}
		catch(NullPointerException e) {
			Toast.makeText(getApplicationContext(), "NETWORK ERROR", Toast.LENGTH_SHORT).show();
		}
		//Extract links and place in array list
		Elements h2Elements = slider_articles.select("h2");
		for(Element h2Element : h2Elements){
			h2List.add(h2Element.text());
		}
		Elements hrefElements = h2Elements.select("a[href]");
		for(Element hrefElement : hrefElements){
			hrefList.add(hrefElement.attr("abs:href"));
		}
		Elements imageElements = slider_articles.select("img");
		for(Element imageElement : imageElements){
			String imageURL = imageElement.attr("abs:src");
			if(imageURL.contains("-220x173")){
				imageURL = imageURL.replace("-220x173","");
			}
			imageList.add(imageURL);
		}
		Elements summaryElements = slider_articles.select("p.p-summary");
		for(Element summaryElement : summaryElements){
			summaryList.add(summaryElement.text());
		}
	}

	public void populateFeaturedContent(final List<String> imageurlList){
		for(int k =0; k<imageurlList.size(); k++){
			final int fCount = k;
			LinearLayout f = new LinearLayout(MainActivity.this);
			f.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0,10,0,10);
			f.setLayoutParams(params);
			ImageView img = new ImageView(this);
			Glide.with(getApplicationContext())
					.load(imageurlList.get(k))
					.into(img);
			f.addView(img);
			TextView heading = new TextView(this);
			heading.setPadding(10, 5, 10, 10);
			heading.setBackgroundColor(Color.parseColor("#80000000"));
			heading.setText(h2List.get(k));
			heading.setTextColor(Color.parseColor("#FCFCFC"));
			heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
			heading.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), ViewPost.class);
					intent.putExtra("post_link", hrefList.get(fCount));
					intent.putExtra("image_link", imageList.get(fCount));
					System.out.println("IMG COUNT = " + fCount);
					startActivity(intent);

				}
			});
			f.addView(heading);
			f.setBackgroundResource(R.drawable.card_background_selector);
			linearlayout.addView(f);
			Log.d("Featured", "ADDED");
		}
//		StartFeaturedSlider();
//		featuredText.setVisibility(View.VISIBLE);
	}

	//Fills #midContentLinks and #midContentText
	//Gathers categories to fetch category related headlines
	public void getLinksToInternalPosts(Document doc){
		Elements midContentElements = doc.select("nav.main-navigation") //First get Primary menu items then use same Elements object to grab secondary menu items
									  .select("li.cat-item");
		for(Element midContentElement : midContentElements){
			midContentLinks.add(midContentElement.select("a[href]").attr("abs:href"));
			midContentText.add(midContentElement.text());
		}
		midContentElements = doc.select("nav.secondary-navigation")
				.select("li.page_item");
		for(Element midContentElement : midContentElements){
			midContentLinks.add(midContentElement.select("a[href]").attr("abs:href"));
			midContentText.add(midContentElement.text());
		}

//		//Nested menu branch point removed for a more fluid navigation
//		midContentLinks.remove(9); midContentLinks.remove(13);
//		midContentText.remove(9); midContentText.remove(13);

		//Drawer
		addDrawerItems();



		//Throw debug data on console and call background task to populate the links

//		for(int i = 0; i<midContentLinks.size(); i++){
//			Log.d("Pratyaksha_DEBUG", midContentText.get(i)+"==>"+ midContentLinks.get(i));
//			String[] fetchPostsparams = {midContentLinks.get(i), midContentText.get(i)};
//			new FetchPostsFromPage().execute(fetchPostsparams);
//		}

//		StringBuilder text = new StringBuilder();
//		File rssInput = new File(IMG_CACHE_FOLDER+"/rss.xml");
//		List<String> rssURLList = new ArrayList<String>();
//		Document rssDocument;
//		try {
//			if(rssInput.exists()) {
//				BufferedReader br = new BufferedReader(new FileReader(rssInput));
//				String line;
//				while ((line = br.readLine()) != null) {
//					text.append(line);
//				}
//				br.close();
//				Document rssDoc = Jsoup.parse(rssInput, "UTF-8", "http://pratyaksha-mitra.com");
//				Elements items = rssDoc.select("item");
//				for(Element item : items){
//					String rssURL = item.select("link").text().replaceAll("\\s", "");
//					rssURLList.add(rssURL);
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//		List<String> articleHeadings = new ArrayList<String>();
//		List<String> articleURLs = new ArrayList<String>();
//		List<String> articleSummaries = new ArrayList<String>();
//		List<String> articleImgs = new ArrayList<String>();

		Elements mainPageArticles = doc.getElementsByTag("article");
		for(Element mainPageArticle : mainPageArticles){
			LinearLayout f = new LinearLayout(MainActivity.this);
			f.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0,10,0,10);
			f.setLayoutParams(params);

			Elements article = mainPageArticle.select("article");
			String articleHeading = article.select("h2").text();
			final String articleURL = article.select("a[href]").attr("abs:href");
			String articleSummary = article.select("p.p-summary").text();
			String articleImgTemp = article.select("img").attr("abs:src");

			//Remove the image dimension at the end of image URL to give better resolution image.
			try {
				String imgSize = StringUtils.substringAfterLast(articleImgTemp, "-");

				imgSize = StringUtils.substringBeforeLast(imgSize, ".");
				Log.d("ARTICLEIMG", "Image Size: "+imgSize);
				if(imgSize!=null && imgSize.length() < 9){
					articleImgTemp = articleImgTemp.replace("-"+imgSize, "");
				}
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}


			final String articleImg = articleImgTemp;

//			articleHeadings.add(articleHeading);
//			articleURLs.add(articleURL);
//			articleSummaries.add(articleSummary);
//			articleImgs.add(articleImg);

			if(!h2List.contains(articleHeading)){
				ImageView img = new ImageView(this);
				Glide.with(getApplicationContext())
						.load(articleImg)
						.into(img);
				f.addView(img);
				TextView heading = new TextView(this);
				heading.setPadding(10, 5, 10, 10);
				Random random = new Random();
				int headlineBaseColor = Color.parseColor(colors[random.nextInt((colors.length - 0)) + 0]);
				heading.setBackgroundColor(headlineBaseColor);
				heading.setText(articleHeading);
				heading.setTextColor(Color.parseColor("#FCFCFC"));
				heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

				TextView summaryText = new TextView(this);
				summaryText.setText(articleSummary);
				summaryText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

				f.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(), ViewPost.class);
						intent.putExtra("post_link", articleURL);
						intent.putExtra("image_link", articleImg);

						startActivity(intent);

					}
				});
				f.addView(heading);
				f.addView(summaryText);
				f.setBackgroundResource(R.drawable.card_background_selector);
				linearlayout.addView(f);

			}
		}
//		Log.d("SWAP_DBG", "BEFORE SWAP: "+articleHeadings.toString());
//		for(int i=0; i<articleURLs.size();i++){
//			int index;
//			if(rssURLList.contains(articleURLs.get(i))){
//				index = rssURLList.indexOf(articleURLs.get(i));
//				Collections.swap(articleHeadings, index, articleHeadings.indexOf(articleURLs.get(i)));
//				//Collections.swap(articleImgs)
//
//			}
//		}
//		Log.d("SWAP_DBG", "AFTER_SWAP: "+articleHeadings.toString());

	}

	public static void expand(final View v) {
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		// Older versions of android (pre API 21) cancel animations for views with a height of 0.
		v.getLayoutParams().height = 1;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1
						? LayoutParams.WRAP_CONTENT
						: (int)(targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1){
					v.setVisibility(View.GONE);
				}else{
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public void populateSideBarMenu(Document doc){

	}

//	//Image slide view
	Runnable mStatusChecker = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if(progressBar.getProgress() == 100){
				progressBar.setVisibility(View.GONE);
			}

			Glide.with(getApplicationContext())
					.load(imageList.get(featuredImgCount))
			.into(featuredView);

			featuredText.setText(h2List.get(featuredImgCount));
			featuredText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					featuredImgCount--;
					if (featuredImgCount < 0) {
						featuredImgCount = imageList.size() - 1;
					}
					System.out.println("IMG COUNT = " + featuredImgCount);
					Intent intent = new Intent(getApplicationContext(), ViewPost.class);
					intent.putExtra("post_link", hrefList.get(featuredImgCount));
					intent.putExtra("image_link", imageList.get(featuredImgCount));
					System.out.println("IMG COUNT = " + featuredImgCount);
					startActivity(intent);
				}

			});

			mHandler.postDelayed(mStatusChecker, 3000);
			featuredImgCount++;
			if(featuredImgCount == imageList.size()){
				featuredImgCount = 0;
			}
			featuredText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					featuredImgCount--;
					if (featuredImgCount < 0) {
						featuredImgCount = imageList.size() - 1;
					}
					System.out.println("IMG COUNT = " + featuredImgCount);
					Intent intent = new Intent(getApplicationContext(), ViewPost.class);
					intent.putExtra("post_link", hrefList.get(featuredImgCount));
					intent.putExtra("image_link", imageList.get(featuredImgCount));
					System.out.println("IMG COUNT = " + featuredImgCount);
					startActivity(intent);
				}

			});
		}

	};

	void StartFeaturedSlider(){
		mStatusChecker.run();
		previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				featuredImgCount--;
				if (featuredImgCount < 0) {
					featuredImgCount = imageList.size() - 1;
				}
				Glide.with(getApplicationContext())
						.load(imageList.get(featuredImgCount))
						.into(featuredView);
				featuredText.setText(h2List.get(featuredImgCount));
				featuredText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						featuredImgCount--;
						if (featuredImgCount < 0) {
							featuredImgCount = imageList.size() - 1;
						}
						System.out.println("IMG COUNT = " + featuredImgCount);
						Intent intent = new Intent(getApplicationContext(), ViewPost.class);
						intent.putExtra("post_link", hrefList.get(featuredImgCount));
						intent.putExtra("image_link", imageList.get(featuredImgCount));
						System.out.println("IMG COUNT = " + featuredImgCount);
						startActivity(intent);
					}
				});

			}
		});

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				featuredImgCount++;
				if(featuredImgCount==imageList.size()-1){
					featuredImgCount=0;
				}
				Glide.with(getApplicationContext())
						.load(imageList.get(featuredImgCount))
						.into(featuredView);
				featuredText.setText(h2List.get(featuredImgCount));
				featuredText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						featuredImgCount--;
						if (featuredImgCount < 0) {
							featuredImgCount = imageList.size() - 1;
						}
						System.out.println("IMG COUNT = " + featuredImgCount);
						Intent intent = new Intent(getApplicationContext(), ViewPost.class);
						intent.putExtra("post_link", hrefList.get(featuredImgCount));
						intent.putExtra("image_link", imageList.get(featuredImgCount));
						System.out.println("IMG COUNT = " + featuredImgCount);
						startActivity(intent);
					}
				});

			}
		});
	}

	//Wrapper for passing arguements to doInBackground
	public class Wrapper
	{
		public Document htmlContent;
		public String category;
		public String categoryURL;
	}

	 public boolean isNetworkOnline() {
		    boolean status=false;
		    try{
		        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo netInfo = cm.getNetworkInfo(0);
		        if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
		            status= true;
		        }else {
		            netInfo = cm.getNetworkInfo(1);
		            if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
		                status= true;
		        }
		    }catch(Exception e){
		        e.printStackTrace();
		        return false;
		    }
	    return status;

    }

	private void addDrawerItems() {
	    mAdapter = new ArrayAdapter<String>(this, R.layout.nav_drawer_layout, midContentText);
	    mDrawerList.setAdapter(mAdapter);
	    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            Intent intent = new Intent(getApplicationContext(), ViewCategory.class);
	            intent.putExtra("cat_link", midContentLinks.get(position));
	            startActivity(intent);
	        }
	    });

	}

	@SuppressWarnings("deprecation")
	private void setupDrawer() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolBar, R.string.drawer_open, R.string.drawer_close) {

			    /** Called when a drawer has settled in a completely open state. */
			    public void onDrawerOpened(View drawerView) {
			    	super.onDrawerOpened(drawerView);
			        getSupportActionBar().setTitle("Navigation");
			        supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				    mDrawerList.bringToFront();
				    mDrawerLayout.requestLayout();
					syncState();
			    }

			    /** Called when a drawer has settled in a completely closed state. */
			    public void onDrawerClosed(View view) {
			    	super.onDrawerClosed(view);
			        getSupportActionBar().setTitle(mActivityTitle);
			        supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			    }

			};

	}

	@SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		//Menu
		switch (item.getItemId()) {
			case R.id.refresh:
				Intent intent = getIntent();
				finish();
				startActivity(intent);
				return true;
			case R.id.about:
				Intent aboutIntent = new Intent(getApplicationContext(), About.class);
				startActivity(aboutIntent);
				return true;
		}

		//Drawer
		return mDrawerToggle.onOptionsItemSelected(item);


	}


	@SuppressWarnings("deprecation")
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    mDrawerToggle.syncState();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void retryButton(){
		Toast.makeText(getApplicationContext(), "Network connection failed!", Toast.LENGTH_LONG).show();
		RelativeLayout rl = new RelativeLayout(MainActivity.this);
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
		linearlayout.addView(rl);
	}

	
}
