package com.cfks.androidhack;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

public class NfcUtils {
	private static final String TAG = "NfcUtils";

	private static NfcAdapter mNfcAdapter;

    private NfcUtils() {}

	private static NfcUtils nfcUtils = null;

	private static boolean isOpen = false;

	/**
	 * 获取NFC的单例
	 * @return NfcUtils
	 */
    public static NfcUtils getInstance() {
        if (nfcUtils == null) {
            synchronized (NfcUtils.class) {
                if (nfcUtils == null) {
                    nfcUtils = new NfcUtils();
                }
            }
        }
        return nfcUtils;
    }

    /**
     * 在onStart中检测是否支持nfc功能
     * @param context 当前页面上下文
     */
    public void onStartNfcAdapter(Context context) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);//设备的NfcAdapter对象
        if (mNfcAdapter == null) {//判断设备是否支持NFC功能
            Toast.makeText(context, "设备不支持NFC功能!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {//判断设备NFC功能是否打开
            Toast.makeText(context, "请到系统设置中打开NFC功能!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "NFC is start");
    }

    /**
     * 在onResume中开启nfc功能
     * @param activity
     */
    public void onResumeNfcAdapter(final Activity activity) {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
//            mNfcAdapter.enableForegroundDispatch(this,mPendingIntent,null,null);//打开前台发布系统，使页面优于其它nfc处理.当检测到一个Tag标签就会执行mPendingItent
            if (!isOpen)
                mNfcAdapter.enableReaderMode(activity, new NfcAdapter.ReaderCallback() {
						@Override
						public void onTagDiscovered(final Tag tag) {
							//ByteArrayToHexString(tag.getId())即cardId
							Log.d(TAG, ByteArrayToHexString(tag.getId()));
							if (nfcListener != null)
								(activity).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            nfcListener.doing(tag);
                                        }
                                    });
						}
					},
					(NfcAdapter.FLAG_READER_NFC_A |
					NfcAdapter.FLAG_READER_NFC_B |
					NfcAdapter.FLAG_READER_NFC_F |
					NfcAdapter.FLAG_READER_NFC_V |
					NfcAdapter.FLAG_READER_NFC_BARCODE),
					null);
            isOpen = true;
            Log.d(TAG, "Resume");
        }
    }

    /**
     * 在onPause中关闭nfc功能
     * @param activity
     */
    public void onPauseNfcAdapter(Activity activity) {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            if (isOpen)
                mNfcAdapter.disableReaderMode(activity);
            isOpen = false;
        }
        Log.d("myNFC", "onPause");
    }

    private  NfcListener nfcListener;

    public void setNfcListener(NfcListener listener) {
        nfcListener = listener;
    }

    /**
     * 自定义的NFC接口
     */
    public interface NfcListener {
        /**
         * 用于扫到nfc后的后续操作
         */
        void doing(Tag tag);
    }

    public String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
			"B", "C", "D", "E", "F"};
        String out = "";


        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
}
