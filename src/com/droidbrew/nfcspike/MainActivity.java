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
	            // Stop here, we definitely need NFC
	            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }
	        if (!nfcAdapter.isEnabled()) {
	            readNFCTextView.setText("NFC is disabled.");
	        } else {
	            readNFCTextView.setText(R.string.placeholder);
	        }
	        //handleIntent(getIntent());
	        
	        Intent intent = getIntent();
	        if(intent.getType() != null && intent.getType().equals("application/" + getPackageName())) {
	            // Read the first record which contains the NFC data
	            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	            NdefRecord relayRecord = ((NdefMessage)rawMsgs[0]).getRecords()[0];
	            String nfcData = new String(relayRecord.getPayload());
	     
	            // Display the data on the tag
	            Toast.makeText(this, nfcData, Toast.LENGTH_SHORT).show();
	     
	            // Do other stuff with the data...
	     
	            // Just finish the activity
	            finish();
	        }
	    }

//	    @Override
//	    protected void onResume() {
//	        super.onResume();
//
//	        setupForegroundDispatch(this, nfcAdapter);
//	    }
	    
//	    @Override
//	    protected void onPause() {
//
//	        stopForegroundDispatch(this, nfcAdapter);
//	        super.onPause();
//	    }
//	    
//	    @Override
//	    protected void onNewIntent(Intent intent) {
//
//	        handleIntent(intent);
//	    }
//	    
//
//	    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
//	        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
//	        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//	        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
//	        IntentFilter[] filters = new IntentFilter[1];
//	        String[][] techList = new String[][]{};
//	        // Notice that this is the same filter as in our manifest.
//	        filters[0] = new IntentFilter();
//	        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
//	        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
//	        try {
//	            filters[0].addDataType(MIME_TEXT_PLAIN);
//	        } catch (MalformedMimeTypeException e) {
//	            throw new RuntimeException("Check your mime type.");
//	        }
//	        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
//	    }
//	    
//
//	    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
//	        adapter.disableForegroundDispatch(activity);
//	    }
//	    
//	    private void handleIntent(Intent intent) {
//	        String action = intent.getAction();
//	        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//	            String type = intent.getType();
//	            if (MIME_TEXT_PLAIN.equals(type)) {
//	                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//	                new NdefReaderTask().execute(tag);
//	            } else {
//	                Log.d(TAG, "Wrong mime type: " + type);
//	            }
//	        } 
//	    }
//	  
//	    
//	    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
//	        @Override
//	        protected String doInBackground(Tag... params) {
//	            Tag tag = params[0];
//	            Ndef ndef = Ndef.get(tag);
//	            if (ndef == null) {
//	                // NDEF is not supported by this Tag.
//	                return null;
//	            }
//	            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
//	            NdefRecord[] records = ndefMessage.getRecords();
//	            for (NdefRecord ndefRecord : records) {
//	                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
//	                    try {
//	                        String read = readText(ndefRecord);
//	                        
//	                        return read;
//	                    } catch (UnsupportedEncodingException e) {
//	                        Log.e(TAG, "Unsupported Encoding", e);
//	                    }
//	                }
//	            }
//	            return null;
//	        }
//	        private String readText(NdefRecord record) throws UnsupportedEncodingException {
//	            /*
//	             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
//	             *
//	             * http://www.nfc-forum.org/specs/
//	             *
//	             * bit_7 defines encoding
//	             * bit_6 reserved for future use, must be 0
//	             * bit_5..0 length of IANA language code
//	             */
//	            byte[] payload = record.getPayload();
//	            // Get the Text Encoding
//	            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
//	            // Get the Language Code
//	            int languageCodeLength = payload[0] & 0063;
//	            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
//	            // e.g. "en"
//	            // Get the Text
//	            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
//	        }
//	        
//	        @Override
//	        protected void onPostExecute(String result) {
//	            if (result != null) {
//	                readNFCTextView.setText("Read content: " + result);
//	            }
//	        }
//	    }
	    
	    public void writeTag(View v){
	    	Intent writeIntent = new Intent(MainActivity.this, WriteActivity.class);
	    	MainActivity.this.startActivity(writeIntent);
	    }
	    
}
