package com.droidbrew.nfcspike;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class WriteActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write);

	}

	public void startWritingProcess(View v){
		String nfcMessage = "Mark!!!!!";
		 
		// When an NFC tag comes into range, call the main activity which handles writing the data to the tag
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		 
		Intent nfcIntent = new Intent(this, WriteActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		nfcIntent.putExtra("nfcMessage", nfcMessage);
		PendingIntent pi = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);  
		 
		nfcAdapter.enableForegroundDispatch((Activity)this, pi, new IntentFilter[] {tagDetected}, null);
	}
	
	public void onNewIntent(Intent intent) {
	    // When an NFC tag is being written, call the write tag function when an intent is
	    // received that says the tag is within range of the device and ready to be written to
	    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    String nfcMessage = intent.getStringExtra("nfcMessage");
	 
	    if(nfcMessage != null) {
	        if(writeTag(this, tag, nfcMessage))
	        	Toast.makeText(this, "Write successfull", Toast.LENGTH_LONG).show();
	    }
	}
	
	public static boolean writeTag(Context context, Tag tag, String data) {     
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());
        
        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                                                new String("application/" + context.getPackageName()).getBytes(Charset.forName("US-ASCII")),
                                                null, data.getBytes());
        
        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[] {relayRecord, appRecord});

        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if(!ndef.isWritable()) {
                    Toast.makeText(context, "Error: nfcReadOnlyError", Toast.LENGTH_LONG).show();
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if(ndef.getMaxSize() < size) {
                    Toast.makeText(context, "Error: nfcBadSpaceError", Toast.LENGTH_LONG).show();
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);
                    
                    //DialogUtils.displayInfoDialog(context, R.string.nfcWrittenTitle, R.string.nfcWritten);
                    return true;
                } catch (TagLostException tle) {
                    Toast.makeText(context, "Error: nfcTagLostError", Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException ioe) {
                    Toast.makeText(context, "Error: nfcFormattingError", Toast.LENGTH_LONG).show();
                    return false;
                } catch (FormatException fe) {
                    Toast.makeText(context, "Error: nfcFormattingError", Toast.LENGTH_LONG).show();
                    return false;
                }
            // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if(format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        
                        //DialogUtils.displayInfoDialog(context, R.string.nfcWrittenTitle, R.string.nfcWritten);
                        return true;
                    } catch (TagLostException tle) {
                        Toast.makeText(context, "Error: nfcTagLostError", Toast.LENGTH_LONG).show();
                        return false;
                    } catch (IOException ioe) {
                        Toast.makeText(context, "Error: nfcFormattingError", Toast.LENGTH_LONG).show();
                        return false;
                    } catch (FormatException fe) {
                        Toast.makeText(context, "Error: nfcFormattingError", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(context, "Error: nfcNoNdefErrorTitle", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } catch(Exception e) {
        	Toast.makeText(context, "Error: nfcUnknownErrorTitle", Toast.LENGTH_LONG).show();
            
        }

        return false;
    }

}
