package com.ssthouse.moduo.control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.ssthouse.moduo.control.util.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * 手机短信Receiver
 * Created by ssthouse on 2016/2/16.
 */
public class SmsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                Timber.e("number:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
                        + msg.getTimestampMillis());
                Toast.show("number:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
                        + msg.getTimestampMillis());
                //在这里写自己的逻辑
                if (msg.getOriginatingAddress().equals("10086")) {
                    //TODO

                }

            }
        }
    }
}
