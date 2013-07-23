package com.bionx.res.about;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bionx.res.R;
import com.bionx.res.activity.GetStarted;

public class InfoCenter extends Activity {
	
	private Button mbutton;
	private Button mbutton1;
	private Button mbutton2;
	private Button mbutton3;
	private Button mbutton4;
	private Button mbutton5;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.git: {
			Intent url1 = new Intent("android.intent.action.VIEW",
					Uri.parse("https://github.com/rgocal"));
			startActivity(url1);
			return true;
		}
		case R.id.play: {
			Intent url2 = new Intent(
					"android.intent.aimport com.bionx.res.activity.GetStarted;ction.VIEW",
					Uri.parse("https://play.google.com/store/apps/details?id=com.bionx.res#?t=W251bGwsMSwxLDIxMiwiY29tLmJpb254LnJlcyJd"));
			startActivity(url2);
			return true;
		}
		case R.id.nx: {
			final Context context = this;
			Intent url3 = new Intent(context, GetStarted.class);
			startActivity(url3);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_info);
        
      //Buttons
      		mbutton = (Button) findViewById(R.id.button);
      		mbutton1 = (Button) findViewById(R.id.button1);
      		mbutton2 = (Button) findViewById(R.id.button2);
      		mbutton3 = (Button) findViewById(R.id.button3);
      		mbutton4 = (Button) findViewById(R.id.button4);
      		mbutton5 = (Button) findViewById(R.id.button5);
      		
      	// Launch Intents
    	mbutton.setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			Intent button = new Intent("android.intent.action.VIEW", Uri
    					.parse("https://www.paypal.com/us/cgi-bin/webscr?cmd=_flow&SESSION=z-_W_QaMEamwGCWDi8730MaAoG03PUtKCYaTfQAFQSS8JoLpyK0syEq9hvm&dispatch=5885d80a13c0db1f8e263663d3faee8d4e181b3aff599f99a338772351021e7d"));
    			startActivity(button);
    			}
    		});
    	
    	mbutton1.setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			Intent button1 = new Intent("android.intent.action.VIEW", Uri
    					.parse("https://www.facebook.com/pages/Bionx/133692896729322"));
    			startActivity(button1);
    		}
    	});
    	
    	mbutton2.setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			Intent button2 = new Intent("android.intent.action.VIEW", Uri
    					.parse("https://plus.google.com/u/0/100201209156317802250"));
    			startActivity(button2);
    		}
    	});
    	
    	mbutton3.setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			Intent button3 = new Intent("android.intent.action.VIEW", Uri
    					.parse("https://twitter.com/Rgocal"));
    			startActivity(button3);
    		}
    	});
    	
    	mbutton4.setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			Intent button4 = new Intent(getBaseContext(),
    					License.class);
    			startActivity(button4);
    		}
    	});
    	
    	mbutton5.setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			Intent button5 = new Intent(getBaseContext(),
    					DashChangelog.class);
    			startActivity(button5);
    		}
    	});
    }
}