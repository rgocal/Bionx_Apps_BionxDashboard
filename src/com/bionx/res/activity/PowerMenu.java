package com.bionx.res.activity;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bionx.res.R;




public class PowerMenu extends Activity {
	private static final String TAG = "PowerMenu";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power_menu);

        Button rebootRecovery = (Button) findViewById(R.id.btnRebootRecovery);
        Button btnRebootNormal = (Button) findViewById(R.id.btnRebootNormal);
        Button btnShutdown = (Button) findViewById(R.id.btnShutdown);
        Button btnBootloader = (Button) findViewById(R.id.btnBootloader);
        Button btnHotBoot = (Button) findViewById(R.id.btnHotBoot);

        final Builder builder = new AlertDialog.Builder(PowerMenu.this);   
        builder.setTitle("Info");
        builder.setPositiveButton("Ok", null);   
        
        // Reboot Recovery
        rebootRecovery.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	
            	Context context = getApplicationContext();
    			try {
    				Process p = Runtime.getRuntime().exec("su");
    				OutputStream os = p.getOutputStream();
    				os.write("reboot recovery\n".getBytes());
    				os.flush();
    				Toast.makeText(context, "Rebooting to recovery...", Toast.LENGTH_LONG).show();
    			} catch (IOException e) {
    				Log.e(TAG, "Unable to reboot into recovery mode:", e);
    				Toast.makeText(context, "Unable to reboot to recovery", Toast.LENGTH_LONG).show();
    			}
                }  });
        
        // Reboot
        btnRebootNormal.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
 
            	Context context = getApplicationContext();
    			try {
    				Process p = Runtime.getRuntime().exec("su");
    				OutputStream os = p.getOutputStream();
    				os.write("toolbox reboot\n".getBytes());	
    				os.flush();
    				Toast.makeText(context, "Rebooting...", Toast.LENGTH_LONG).show();
    			} catch (IOException e) {
    				Log.e(TAG, "Unable to reboot :", e);
    				Toast.makeText(context, "Unable to reboot", Toast.LENGTH_LONG).show();
    			}
                }  
        	});
        
        // Reboot
        btnShutdown.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
 
            	Context context = getApplicationContext();
    			try {
    				Process p = Runtime.getRuntime().exec("su");
    				OutputStream os = p.getOutputStream();
    				os.write("toolbox reboot -p\n".getBytes());	
    				os.flush();
    				Toast.makeText(context, "Shutting down...", Toast.LENGTH_LONG).show();
    			} catch (IOException e) {
    				Log.e(TAG, "Unable to reboot :", e);
    				Toast.makeText(context, "Unable to shutdown", Toast.LENGTH_LONG).show();
    			}
                }  
        	});
        
        // Bootloader
        btnBootloader.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
 
            	Context context = getApplicationContext();
    			try {
    				Process p = Runtime.getRuntime().exec("su");
    				OutputStream os = p.getOutputStream();
    				os.write("toolbox reboot bootloader\n".getBytes());	
    				os.flush();
    				Toast.makeText(context, "Booting to bootloader...", Toast.LENGTH_LONG).show();
    			} catch (IOException e) {
    				Log.e(TAG, "Unable to reboot :", e);
    				Toast.makeText(context, "Unable to shutdown", Toast.LENGTH_LONG).show();
    			}
                }  
        	});
        
     // HotBoot
        btnHotBoot.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
 
            	Context context = getApplicationContext();
    			try {
    				Process p = Runtime.getRuntime().exec("su");
    				OutputStream os = p.getOutputStream();
    				os.write("busybox killall system_server\n".getBytes());	
    				os.flush();
    				Toast.makeText(context, "Hot!", Toast.LENGTH_LONG).show();
    			} catch (IOException e) {
    				Log.e(TAG, "Unable to reboot :", e);
    				Toast.makeText(context, "Unable to shutdown", Toast.LENGTH_LONG).show();
    			}
                }  
        	});
	}
}