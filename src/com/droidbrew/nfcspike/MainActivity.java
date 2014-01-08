package com.droidbrew.nfcspike;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String TAG = "NFCSpike";
	public static final String MIME_TEXT_PLAIN = "text/plain";
    private TextView readNFCTextView;
    private NfcAdapter nfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		readNFCTextView = (TextView) findViewById(R.id.placeholder);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
			return;
		}

		handleIntent();
		
	}
	
	private void handleIntent(){
		Intent intent = getIntent();
		if(intent.getType() != null && intent.getType().equals("application/" + getPackageName())) {
			// Read the first record which contains the NFC data
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefRecord relayRecord = ((NdefMessage)rawMsgs[0]).getRecords()[0];
			String nfcData = new String(relayRecord.getPayload());

			// Display the data on the tag
			Toast.makeText(this, "new NFC tag was read", Toast.LENGTH_SHORT).show();

			Intent dataIntent = new Intent(MainActivity.this, ShowTagDataActivity.class);
			dataIntent.putExtra("data", nfcData);	
			MainActivity.this.startActivity(dataIntent);
			
			finish();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		handleIntent();
	}
	    
	public void writeTag(View v){
		Intent writeIntent = new Intent(MainActivity.this, WriteActivity.class);
		MainActivity.this.startActivity(writeIntent);
	}
	    
}
