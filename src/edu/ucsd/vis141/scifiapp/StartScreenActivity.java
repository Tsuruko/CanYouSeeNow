package edu.ucsd.vis141.scifiapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

/*********************************
 * UCSD VIS 141A project
 * SciFiAPP
 * 
 * Created By: Monica Liu
 * Last Modified 2/10/14
 * 
 * StartScreenActivity:
 *   Launch activity with basic layout.
 *   Has a button to begin the game.
 *   May later feature more game options
 *   
 ********************************/

public class StartScreenActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
        
		return true;
	}

	public void startGame(View view) {
		startActivity(new Intent(this, CameraActivity.class));
	}
}
