package com.example.practice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FakeCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.hasExtra("contact_details")) {
            String contactDetails = intent.getStringExtra("contact_details");

            if (contactDetails != null && !contactDetails.isEmpty()) {
                Intent fakeCallIntent = new Intent(context, FakeCallActivity.class);
                fakeCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Forward contact details to FakeCallActivity
                fakeCallIntent.putExtra("contact_details", contactDetails);

                context.startActivity(fakeCallIntent);
            } else {
                Log.e("FakeCallReceiver", "Received empty contact details, ignoring fake call.");
            }
        } else {
            Log.e("FakeCallReceiver", "Intent missing 'contact_details' extra, ignoring fake call.");
        }
    }
}
