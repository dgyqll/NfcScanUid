package com.etouse.nfcscanuid;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView tvUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvUid = (TextView) findViewById(R.id.tv_uid);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcAdapter == null) {
            Toast.makeText(MainActivity.this,"设备不支持NFC",Toast.LENGTH_LONG).show();
            return;
        }
        if (nfcAdapter!=null&&!nfcAdapter.isEnabled()) {
            Toast.makeText(MainActivity.this,"请在系统设置中先启用NFC功能",Toast.LENGTH_LONG).show();
            return;
        }
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        resolveIntent(intent);
    }

    void resolveIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            processTag(intent);
        }
    }

    public void processTag(Intent intent) {//处理tag
        String uid = "";
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//		str+="Tech List:"+tagFromIntent.getTechList()[0]+"\n";//打印卡的技术列表
        byte[] aa = tagFromIntent.getId();
        uid += bytesToHexString(aa);//获取卡的UID
        tvUid.setText(uid);
    }

    //字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    null, null);
    }


}
