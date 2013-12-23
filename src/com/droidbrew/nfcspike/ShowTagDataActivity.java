package com.droidbrew.nfcspike;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class ShowTagDataActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_tag_data);
		
		Intent intent = getIntent();
        String dataValue = intent.getStringExtra("data");
		
		TextView readNFCTextView = (TextView) findViewById(R.id.placeholderData);
		readNFCTextView.setText(dataValue);
	}

}
