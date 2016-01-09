package com.pratyaksha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.pratyaksha.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class RSSService extends Service{
	
	public Context context = this;
	public Runnable runnable = null;
	static String RSSURL = "http://www.pratyaksha-mitra.com/feed/";
	String CACHE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PRTKSH_CACHE/";  
	File directory = new File(CACHE_FOLDER);
	List<String> linkList = new ArrayList<String>();
	List<String> dateList = new ArrayList<String>();
	List<String> titleList = new ArrayList<String>();
	NotificationManager notificationManager;
	Notification notification;
	PowerManager.WakeLock wl;
	File rssFile = new File(CACHE_FOLDER, "rss.xml");
	File logFile = new File(CACHE_FOLDER, "rssLog.txt");

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override 
	public void onCreate(){
		//Toast.makeText(getApplicationContext(), "Started Background RSS Service", Toast.LENGTH_SHORT).show();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RSSService");
		if(!logFile.exists()){
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class fetchFeedStream extends AsyncTask<String, Void, Document>{

		@Override
		protected Document doInBackground(String... params) {
			// TODO Auto-generated method stub
			Document doc = null;
			try {
				doc = Jsoup.connect(params[0]).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Pratyaksha_RSSService", "Error fetching RSS FEED");
			}
			return doc;
		}
		
		@Override
		protected void onPostExecute(Document doc){
			try {
				parseFeed(doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//wl.release();
		}
		
	}
	
	public void parseFeed(Document doc) throws IOException {
		StringBuilder text = new StringBuilder();
		Document previousRss;
		try{
			if(rssFile.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(rssFile));
				String line;
				while ((line = br.readLine()) != null) {
					text.append(line);
				}
				br.close();
				previousRss = Jsoup.parse(text.toString(), "", Parser.xmlParser());
				Element previousItems = previousRss.select("item").first();
				Element currentItems = doc.select("item").first();

				String currLink = currentItems.select("link").text().replaceAll("\\s", "");
				String prevLink = previousItems.select("link").text();
				Log.d("RSSService", "Current: " + currLink);
				Log.d("RSSService", "Previous: " + prevLink);

				String title = currentItems.select("title").text();

				if (currLink.equals(prevLink) == false) {
					addNotification(getApplicationContext(), currLink, title);
					FileWriter fw = new FileWriter(rssFile, false);
					fw.write(doc.toString());
					fw.close();
				}

			}
			else{
				rssFile.createNewFile();
				//Write RSS document to file for future reference
				FileWriter fw = new FileWriter(rssFile, false);
				fw.write(doc.toString());
				fw.close();
			}
			//Write Log
			FileWriter log = new FileWriter(logFile, true);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			Date now = new Date();
			log.write("Time: "+formatter.format(now) + "\n");
			log.close();

		} catch(NullPointerException e){
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void addNotification(Context context, String URL, String title){
		Intent notificationIntent = new Intent(context, ViewPost.class);
		notificationIntent.putExtra("post_link", URL);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

			notification = new Notification.Builder(context)
					.setContentTitle("प्रत्यक्ष ")
					.setContentText(title)
					.setTicker(title)
					.setSmallIcon(R.drawable.ic_stat__notif)
					.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
					.setWhen(System.currentTimeMillis())
					.setContentIntent(contentIntent)
					.build();

		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
//		if (alarmRunning == false)
//			alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
//			alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * 1),
//				PendingIntent.getService(this, 0, new Intent(this, RSSService.class), 0)
//		);
//	}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid){

		//Acquire the lock
		System.out.println("+++ Acquiring Lock +++");

//		if(!wl.isHeld())
//			wl.acquire();
		new fetchFeedStream().execute(RSSURL);
		//Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		
		return Service.START_STICKY;
	}

}


