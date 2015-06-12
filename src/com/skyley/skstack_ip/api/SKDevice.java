/****
Copyright (c) 2015, Skyley Networks, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
* Neither the name of the Skyley Networks, Inc. nor the names of its contributors 
  may be used to endorse or promote products derived from this software 
  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Skyley Networks, Inc. BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
****/

package com.skyley.skstack_ip.api;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.skyley.skstack_ip.api.skcommands.SKAddNbr;
import com.skyley.skstack_ip.api.skcommands.SKClose;
import com.skyley.skstack_ip.api.skcommands.SKCommand;
import com.skyley.skstack_ip.api.skcommands.SKConnect;
import com.skyley.skstack_ip.api.skcommands.SKErase;
import com.skyley.skstack_ip.api.skcommands.SKInfo;
import com.skyley.skstack_ip.api.skcommands.SKJoin;
import com.skyley.skstack_ip.api.skcommands.SKLoad;
import com.skyley.skstack_ip.api.skcommands.SKPing;
import com.skyley.skstack_ip.api.skcommands.SKRegDev;
import com.skyley.skstack_ip.api.skcommands.SKRejoin;
import com.skyley.skstack_ip.api.skcommands.SKReset;
import com.skyley.skstack_ip.api.skcommands.SKRmDev;
import com.skyley.skstack_ip.api.skcommands.SKRmKey;
import com.skyley.skstack_ip.api.skcommands.SKSave;
import com.skyley.skstack_ip.api.skcommands.SKScan;
import com.skyley.skstack_ip.api.skcommands.SKSecEnable;
import com.skyley.skstack_ip.api.skcommands.SKSend;
import com.skyley.skstack_ip.api.skcommands.SKSendTo;
import com.skyley.skstack_ip.api.skcommands.SKSetKey;
import com.skyley.skstack_ip.api.skcommands.SKSetPsk;
import com.skyley.skstack_ip.api.skcommands.SKSetPwd;
import com.skyley.skstack_ip.api.skcommands.SKSetRbid;
import com.skyley.skstack_ip.api.skcommands.SKSregGet;
import com.skyley.skstack_ip.api.skcommands.SKSregSet;
import com.skyley.skstack_ip.api.skcommands.SKStart;
import com.skyley.skstack_ip.api.skcommands.SKTable;
import com.skyley.skstack_ip.api.skcommands.SKTcpPort;
import com.skyley.skstack_ip.api.skcommands.SKTerm;
import com.skyley.skstack_ip.api.skcommands.SKUdpPort;
import com.skyley.skstack_ip.api.skcommands.SKVer;
import com.skyley.skstack_ip.api.skenums.SKDeviceModel;
import com.skyley.skstack_ip.api.skenums.SKErrorCode;
import com.skyley.skstack_ip.api.skenums.SKEventType;
import com.skyley.skstack_ip.api.skenums.SKRegister;
import com.skyley.skstack_ip.api.skenums.SKScanMode;
import com.skyley.skstack_ip.api.skenums.SKSecOption;
import com.skyley.skstack_ip.api.skevents.SKEAddr;
import com.skyley.skstack_ip.api.skevents.SKEHandle;
import com.skyley.skstack_ip.api.skevents.SKEInfo;
import com.skyley.skstack_ip.api.skevents.SKENeighbor;
import com.skyley.skstack_ip.api.skevents.SKEPong;
import com.skyley.skstack_ip.api.skevents.SKESreg;
import com.skyley.skstack_ip.api.skevents.SKEVer;

/**
 * SKSTACK-IPを実装したデバイス1台に対応するクラス、
 * アプリケーションはこのクラスをインスタンス化し、メソッドを呼び出す。<br>
 * 他のクラスはアプリケーションからは直接使わない。
 * @author Skyley Networks, Inc.
 * @version 0.1
*/
public class SKDevice {
	/** デバイス機種 */
	private SKDeviceModel model;
	/** デバイス接続先シリアルポートの名称（"COM3"など） */
	private String portString;
	/** portStringに対応するCommPort */
	private CommPort commPort;
	 /** portStringに対応するSerialPort */
	private SerialPort port;
	/** SKコマンドのタイムアウト（ミリ秒） */
	private long commandTimeout;
	/** 受信バッファ（receiver, readerで共有） */
	private BlockingQueue<String> buffer;
	/** 受信文字列をbufferに格納するクラス */
	private SKReceiver receiver;
	/** bufferを読み取り、処理するクラス */
	private SKRxBufferReader reader;
	/** デバッグ情報のリスナー */
	private SKDebugListener debugListener;

	/**
	 * コンストラクタ
	 * model(デバイス機種)はGENERALとする。
	 */
	public SKDevice() {
		model = SKDeviceModel.GENERAL;
	}

	/**
	 * デバイスと接続
	 * @param portString 接続先ポート名
	 * @return true 接続に成功:true, 失敗:false
	 */
	public boolean connect(String portString) {
		try {
			CommPortIdentifier comID = CommPortIdentifier.getPortIdentifier(portString);
			commPort = comID.open("hoge",2000);
			port = (SerialPort)commPort;

			port.setSerialPortParams(115200,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

			this.portString = portString;
			buffer = new ArrayBlockingQueue<String>(64);
			receiver = new SKReceiver(port, buffer);
			reader = new SKRxBufferReader(buffer, model, portString);

			commandTimeout = 2000;

			Thread trx = new Thread(receiver);
			Thread tread = new Thread(reader);
			trx.start();
			tread.start();

			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 *  デバイスとの接続を切断
	 */
	public void close() {
		receiver.stop();
		reader.stop();
		port.close();
		commPort.close();
	}

	/**
	 * デバイスに登録されている"Exxxx"系イベントのリスナーを取得
	 * @return "Exxxx"イベントのリスナー、SKEventListenerを実装したクラス
	 */
	public SKEventListener getSKEventListener() {
		if (reader == null) {
			return null;
		}

		return reader.getSKEventListener();
	}

	/**
	 * "Exxxx"系イベントのリスナーを登録
	 * @param listener SKEventListenerを実装したクラス
	 */
	public void setSKEventListener(SKEventListener listener) {
		if (reader == null) {
			return;
		}

		reader.setSKEventListener(listener);
	}

	/**
	 * "Exxxx"系イベントのリスナー登録を削除
	 */
	public void removeSKEventListner() {
		reader.removeSKEventListener();
	}

	/**
	 * デバイスに登録されているデバッグ情報のリスナーを取得
	 * @return デバッグ情報のリスナー
	 */
	public SKDebugListener getSKDebugListener() {
		return debugListener;
	}

	/**
	 * デバッグ情報のリスナーを登録
	 * @param listener デバッグ情報のリスナー、SKDebugListenerを実装したクラス
	 */
	public void setSKDebugListener(SKDebugListener listener) {
		if (reader == null) {
			return;
		}

		debugListener = listener;
		reader.setSKDebugLIstener(listener);
	}

	/**
	 * デバッグ情報のリスナー登録を解除
	 */
	public void removeSKDebugListener() {
		debugListener = null;
		reader.removeSKDebugListener();
	}

	/**
	 * コマンドタイムアウトを取得
	 * @return タイムアウト（単位 ミリ秒）
	 */
	public long getCommandTimeout() {
		return commandTimeout;
	}

	/**
	 * コマンドタイムアウトを設定
	 * @param value タイムアウト（単位 ミリ秒）
	 */
	public void setCommandTimeout(long value) {
		commandTimeout = value;
	}

	/**
	 * デバイス接続先ポート名を取得
	 * @return デバイス接続先ポート名
	 */
	public String getPortString() {
		return portString;
	}

	/**
	 * IPv6アドレスを取得
	 * @return IPv6アドレスの文字列表現（"FE80:0000:0000:0000:1034:5678:ABCD:EF01"など）、取得に失敗した場合は""
	 */
	public String getIP6Address() {
		SKInfo skinfo = new SKInfo();
		String[] res = sendCommandAndGetResponse(skinfo, 2);
		if (res == null) {
			return "";
		}

		SKEInfo einfo = new SKEInfo();
		if (einfo.parse(res[0])) {
			return einfo.getIP6Address();
		}
		else {
			return "";
		}
	}

	/**
	 * ショートアドレスを取得
	 * @return ショートアドレス、取得に失敗した場合は-1
	 */
	public int getShortAddress() {
		SKInfo skinfo = new SKInfo();
		String[] res = sendCommandAndGetResponse(skinfo, 2);
		if (res == null) {
			return -1;
		}

		SKEInfo einfo = new SKEInfo();
		if (einfo.parse(res[0])) {
			return einfo.getShortAddress();
		}
		else {
			return -1;
		}
	}

	/**
	 * デバイスのIEEE 64bitアドレスを取得
	 * @return 64bitアドレスの16進表現（"12345678ABCDEF01"など）、取得に失敗した場合は-1
	 */
	public String getLongAddress() {
		return getRegisterValue(SKRegister.LONG_ADDRESS);
	}

	/** IEEE 64bitアドレスを設定
	 * @param longAddress 64bitアドレスの16進表現
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setLongAddress(String longAddress) {
		if (!SKUtil.isValidLongAddress(longAddress)) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.LONG_ADDRESS, longAddress);
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * デバイスが使用する周波数の論理チャンネル番号を取得
	 * @return チャンネル番号、取得に失敗した場合は-1
	 */
	public byte getChannel() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.CHANNEL), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 論理チャンネル番号を設定
	 * @param channel 論理チャンネル番号（値域:33-60）
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setChannel(byte channel) {
		if (channel < 33 || channel > 60) {
			return false;
		}

		int ch = Byte.toUnsignedInt(channel);
		SKSregSet sksreg = new SKSregSet(SKRegister.CHANNEL, Integer.toHexString(ch));
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * デバイスのPAN IDを取得
	 * @return PAN ID、取得に失敗した場合は-1
	 */
	public int getPanID() {
		try {
			return Integer.parseInt(getRegisterValue(SKRegister.PAN_ID), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * PAN IDを設定
	 * @param panID PAN ID（値域:0-0xFFFF）
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setPanID(int panID) {
		if (panID < 0 || panID > 0xFFFF) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.PAN_ID, Integer.toHexString(panID));
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * MAC層セキュリティフレームカウンタを取得
	 * @return MAC層セキュリティフレームカウンタ、取得に失敗した場合は-1
	 */
	public long getSecFrameCounter() {
		try {
			return Long.parseLong(getRegisterValue(SKRegister.SEC_FRAME_COUNTER), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 拡張ビーコン要求に設定するPairing IDを取得
	 * @return Paring ID（ASCII8文字）、取得に失敗した場合は""
	 */
	public String getParingID() {
		return getRegisterValue(SKRegister.PARING_ID);
	}

	/**
	 * Paring IDを設定
	 * @param paringID Paring ID（ASCII8文字）
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setParingID(String paringID) {
		if (!SKUtil.isValidAsciiString(paringID, 8)) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.PARING_ID, paringID);
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * ビーコン応答の制御フラグを取得
	 * @return ON(応答する):1, OFF(応答しない):0, 取得に失敗:-1
	 */
	public byte getBeaconResponse() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.BEACON_RESPONSE), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * ビーコン応答の制御フラグをON（応答する）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setBeaconResponseOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.BEACON_RESPONSE, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * ビーコン応答の制御フラグをOFF（応答しない）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setBeaconResponseOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.BEACON_RESPONSE, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * PANAセッションライフタイムを取得
	 * @return セッションライフタイム（単位 秒）、取得に失敗した場合は-1
	 */
	public long getPANASessionLifetime() {
		try {
			return Long.parseLong(getRegisterValue(SKRegister.PANA_SESSION_LIFETIME), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * PANAセッションライムタイムを設定
	 * @param time セッションライフタイム（単位 秒、値域:60-0xFFFFFFFF）
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setPANASessionLifetime(long time) {
		if (time < 60 || time > 0xFFFFFFFFL) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.PANA_SESSION_LIFETIME, Integer.toHexString((int) time));
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * PANAセッションの自動再認証フラグを取得
	 * @return ON(自動で行う):1, OFF(行わない):0, 取得に失敗:-1
	 */
	public byte getPANAAutoAuth() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.PANA_AUTO_AUTH), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * PANAセッションの自動再認証フラグをON（自動で行う）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setPANAAutoAuthOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.PANA_AUTO_AUTH, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * PANAセッションの自動再認証フラグをOFF（自動で行わない）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setPANAAutoAuthOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.PANA_AUTO_AUTH, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * MAC層ブロードキャストに対するセキュリティ制御フラグを所得
	 * @return ON(有効):1, OFF(無効):0, 取得に失敗:-1
	 */
	public byte getMACBroadcastSec() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.MAC_BROADCAST_SEC), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * MAC層ブロードキャストに対するセキュリティ制御フラグをON（有効）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setMACBroadcastSecOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.MAC_BROADCAST_SEC, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * MAC層ブロードキャストに対するセキュリティ制御フラグをOFF（無効）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setMACBroadcastSecOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.MAC_BROADCAST_SEC, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * ICMPメッセージ受信制御フラグを取得
	 * @return ON(受信する):1, OFF(破棄する):0, 取得に失敗:-1
	 */
	public byte getICMPRx() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.ICMP_RX), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * ICMPメッセージ受信制御フラグをON（受信する）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setICMPRxOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.ICMP_RX, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * ICMPメッセージ受信制御フラグをOFF（破棄する）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setICMPRxOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.ICMP_RX, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * 無線の送信出力レベルを取得
	 * @return 送信出力レベル（0-4の5段階）、取得に失敗した場合は-1
	 */
	public byte getTxPowerLevel() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.TX_POWER_LEVEL), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 無線の送信出力レベルを設定
	 * @param level 送信出力レベル(値域:0-4)
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setTxPowerLevel(byte level) {
		if (level < 0 || level > 4) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.TX_POWER_LEVEL, Integer.toHexString(level));
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * Ack要求時の待ち時間を取得
	 * @return 待ち時間（単位 10usec）、取得に失敗した場合は-1
	 */
	public int getAckWaitingTime() {
		try {
			return Integer.parseInt(getRegisterValue(SKRegister.ACK_WAITING_TIME), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Ack要求時の待ち時間を設定
	 * @param time 待ち時間（単位 10usec, 値域:500-5000）
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setAckWaitingTime(short time) {
		if (time < 500 || time > 5000) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.ACK_WAITING_TIME, Integer.toHexString(time));
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * データホワイトニング制御フラグを取得
	 * @return ON(行う):1, OFF(行わない):0, 取得に失敗:-1
	 */
	public byte getDataWhitening() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.DATA_WHITENING), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * データホワイトニング制御フラグをON（行う）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setDataWhiteningOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.DATA_WHITENING, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * データホワイトニング制御フラグをOFF（行わない）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setDataWhiteningOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.DATA_WHITENING, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * データレート低速フラグを取得
	 * @return ON(50kbps):1, OFF(100kbps):0, 取得に失敗:-1
	 */
	public byte getLowDataRateFlag() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.LOW_DATA_RATE_FLAG), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * データレート低速フラグをON（50kbps）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setLowDataRateOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.LOW_DATA_RATE_FLAG, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * データレート低速フラグをOFF（100kbps）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setLowDataRateOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.LOW_DATA_RATE_FLAG, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * CCA閾値（100kbps）を取得
	 * @return CCA閾値（100kbps）、取得に失敗した場合は-1
	 */
	public short getCCAThresholdForHighRate() {
		try {
			return Short.parseShort(getRegisterValue(SKRegister.CCA_THRESHOLD_HIGH_RATE), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * CCA閾値（100kbps）を設定
	 * @param value  CCA閾値（100kbps）(値域:0-0xFF)
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setCCAThresholdForHighRate(short value) {
		if (value < 0 || value > 0xFF) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.CCA_THRESHOLD_HIGH_RATE, Integer.toHexString(value));
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * CCA閾値（50kbps）を取得
	 * @return CCA閾値（50kbps）、取得に失敗した場合は-1
	 */
	public short getCCAThresholdForLowRate() {
		try {
			return Short.parseShort(getRegisterValue(SKRegister.CCA_THRESHOLD_LOW_RATE), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * CCA閾値（50kbps）を設定
	 * @param value  CCA閾値（50kbps）(値域:0-0xFF)
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setCCAThresholdForLowRate(short value) {
		if (value < 0 || value > 0xFF) {
			return false;
		}

		SKSregSet sksreg = new SKSregSet(SKRegister.CCA_THRESHOLD_LOW_RATE, Integer.toHexString(value));
		return sendCommandAndWaitOK(sksreg);
	}


	/**
	 * DSN多重チェック制御フラグを取得
	 * @return ON(チェックする):1, OFF(チェックしない):0, 取得に失敗:-1
	 */
	public byte getDSNMultipleCheck() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.DSN_MULTIPLE_CHECK), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * DSN多重チェック制御フラグをON（チェックする）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setDSNMultipleCheckOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.DSN_MULTIPLE_CHECK, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * DSN多重チェック制御フラグをOFF（チェックしない）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setDSNMultipleCheckOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.DSN_MULTIPLE_CHECK, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * 送信総和時間による送信制限制御フラグを取得
	 * @return ON(制限する):1, OFF(制限しない):0, 取得に失敗:-1
	 */
	public byte getTxLimit() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.TX_LIMIT), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 送信総和時間による送信制限制御フラグをON（制限する）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setTxLimitOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.TX_LIMIT, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * 送信総和時間による送信制限制御フラグをOFF（制限しない）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setTxLimitOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.TX_LIMIT, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * 送信制限中フラグを取得
	 * @return ON(制限中):1, OFF(制限中でない):0, 取得に失敗:-1
	 */
	public byte getTxLimitWorking() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.TX_LIMIT_WORKING), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 無線区間に送出されたデータの積算時間を取得
	 * @return 積算時間（単位 ミリ秒）、取得に失敗した場合は-1
	 */
	public int getCumulativeTxTime() {
		try {
			return Integer.parseUnsignedInt(getRegisterValue(SKRegister.CUMULARIVE_TX_TIME), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * コマンド入力のエコーバックフラグを取得
	 * @return ON(エコーバックする):1, OFF(しない):0, 取得に失敗:-1
	 */
	public byte getCommandEchoback() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.COMMAND_ECHO_BACK), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * コマンド入力のエコーバックフラグをON（エコーバックする）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setCommandEchobackOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.COMMAND_ECHO_BACK, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * コマンド入力のエコーバックフラグをOFF（エコーバックしない）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setCommandEchobackOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.COMMAND_ECHO_BACK, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * オートロード制御フラグを取得
	 * @return ON(有効):1, OFF(無効):0, 取得に失敗:-1
	 */
	public byte getAutoLoad() {
		try {
			return Byte.parseByte(getRegisterValue(SKRegister.AUTO_LOAD), 16);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * オートロード制御フラグをON（有効）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setAutoLoadOn() {
		SKSregSet sksreg = new SKSregSet(SKRegister.AUTO_LOAD, "1");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * オートロード制御フラグをOFF（無効）に設定
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setAutoLoadOff() {
		SKSregSet sksreg = new SKSregSet(SKRegister.AUTO_LOAD, "0");
		return sendCommandAndWaitOK(sksreg);
	}

	/**
	 * PAA(PANA認証サーバ)として動作開始
	 * @return 動作開始に成功:true, 失敗:false
	 */
	public boolean startPAA() {
		SKStart skstart = new SKStart();
		return sendCommandAndWaitOK(skstart);
	}

	/**
	 * Pac(PANA認証クライアント)としてPANA接続シーケンスを開始
	 * @param ip6Address 接続先IPv6アドレス
	 * @return 開始に成功:true,失敗:false
	 */
	public boolean joinPAA(String ip6Address) {
		SKJoin skjoin = new SKJoin(ip6Address);
		return sendCommandAndWaitOK(skjoin);
	}

	/**
	 * 現在接続中の相手に対してPANA再認証シーケンスを開始
	 * @return 開始に成功:true, 失敗:false
	 */
	public boolean rejoinPAA() {
		SKRejoin skrejoin = new SKRejoin();
		return sendCommandAndWaitOK(skrejoin);
	}

	/**
	 * PANAセッションの終了を要請（PAA, Pacどちらからでも可）
	 * @return 要請に成功:true, 失敗:false
	 */
	public boolean termPAA() {
		SKTerm skterm = new SKTerm();
		return sendCommandAndWaitOK(skterm);
	}

	/**
	 * UDPでデータを送信
	 * @param handle 送信元UDPハンドル
	 * @param ip6Address 宛先IPv6アドレス
	 * @param port 宛先ポート番号
	 * @param sec 暗号化オプション
	 * @param data 送信データ
	 * @return 送信処理開始に成功:true, 失敗:false
	 */
	public boolean sendUDP(byte handle, String ip6Address, int port, SKSecOption sec, byte[] data) {
		SKSendTo sksendto = new SKSendTo(handle, ip6Address, port, sec, data);
		return sendCommandAndWaitOK(sksendto);
	}

	/**
	 * TCPの接続要求を発行<br>
	 * 接続処理の結果はETCPイベントで通知
	 * @param ip6Address 接続先IPv6アドレス
	 * @param rport 接続先ポート番号（値域:1-65534）
	 * @param lport 接続元ポート番号（値域:1-65534）
	 * @return 接続要求発行に成功:true, 失敗:false
	 */
	public boolean connectTCP(String ip6Address, int rport, int lport) {
		SKConnect skconnect = new SKConnect(ip6Address, rport, lport);
		return sendCommandAndWaitOK(skconnect);
	}

	/**
	 * 指定したハンドル番号に対応するTCPコネクションを介してデータを送信<br>
	 * 送信処理の結果はETCPイベントで通知
	 * @param handle ハンドル番号
	 * @param data 送信データ
	 * @return 送信処理開始に成功:true, 失敗:false
	 */
	public boolean sendTCP(byte handle, byte[] data) {
		SKSend sksend = new SKSend(handle, data);
		return sendCommandAndWaitOK(sksend);
	}

	/**
	 * 指定したハンドル番号に対応するTCPコネクションの切断要求を発行<br>
	 * 切断処理の結果はETCPイベントで通知
	 * @param handle ハンドル番号
	 * @return 切断要求発行に成功:true, 失敗:false
	 */
	public boolean closeTCP(byte handle) {
		SKClose skclose = new SKClose(handle);
		return sendCommandAndWaitOK(skclose);
	}

	/**
	 * 指定したIPv6アドレス宛にICMP Echo Requestを送信<br>
	 * Echo Replyを受信するとEPONGイベントで通知
	 * @param dstIP6Address 送信先IPv6アドレス
	 * @return 送信処理開始に成功:true, 失敗:false
	 */
	public boolean sendPing(String dstIP6Address) {
		SKPing skping = new SKPing(dstIP6Address);
		String[] res = sendCommandAndGetResponse(skping, 2);
		if (res == null) {
			return false;
		}

		SKEPong epong = new SKEPong();
		if (epong.parse(res[1])) {
			return SKUtil.toFormattedIP6Address(dstIP6Address).compareTo(epong.getSenderAddress()) == 0 ? true : false;
		}
		else {
			return false;
		}
	}

	/**
	 * 指定したチャンネルに対してスキャン実行
	 * @param mode スキャンモード
	 * @param mask スキャンするチャンネルのビットマップフラグ（最下位ビットがチャンネル33に対応）
	 * @param duration 各チャンネルのスキャン時間（値域:0-14）、スキャン時間は次の式で計算<br>
	 * 0.01 sec * (2^{duration} + 1)
	 * @return スキャン開始に成功:true, 失敗:false
	 */
	public boolean scanChannel(SKScanMode mode, String mask, byte duration) {
		SKScan skscan = new SKScan(mode, mask, duration);
		return sendCommandAndWaitOK(skscan);
	}

	/**
	 * セキュリティを適用するため、指定したIPアドレスをデバイスに登録
	 * @param ip6Address 登録対象のIPv6アドレス
	 * @return 登録に成功:true, 失敗:false
	 */
	public boolean registerIPAddress(String ip6Address) {
		SKRegDev skregdev = new SKRegDev(ip6Address);
		return sendCommandAndWaitOK(skregdev);
	}

	/**
	 * 指定したIPアドレスのエントリーをネイバーテーブル、ネイバーキャッシュから削除
	 * @param ip6Address 削除したいエントリーのIPv6アドレス
	 * @return 削除に成功:true, 失敗:false
	 */
	public boolean  removeIPAddress(String ip6Address) {
		SKRmDev skrmdev = new SKRmDev(ip6Address);
		return sendCommandAndWaitOK(skrmdev);
	}

	/**
	 * 指定したキーインデックスに対する暗号化キーをMAC層セキュリティコンポーネントに登録
	 * @param index キーインデックス
	 * @param key 128bit NWK暗号化キー（ASCII32文字）
	 * @return 登録に成功:true, 失敗:false
	 */
	public boolean setEncryptionKey(short index, String key) {
		SKSetKey sksetkey = new SKSetKey(index, key);
		return sendCommandAndWaitOK(sksetkey);
	}

	/**
	 * 指定したキーインデックスに対する暗号化キーをMAC層セキュリティコンポーネントから削除
	 * @param index 削除対象のキーインデックス
	 * @return 削除に成功:true, 失敗:false
	 */
	public boolean removeEncryptionKey(short index) {
		SKRmKey skrmkey = new SKRmKey(index);
		return sendCommandAndWaitOK(skrmkey);
	}

	/**
	 * 指定したIPアドレスに対するMAC層セキュリティを有効化
	 * @param ip6Address セキュリティを適用する対象のIPv6アドレス
	 * @param macAddress 対象IPv6アドレスに対応する64bitアドレス（16進表現16文字）
	 * @return 有効化に成功:true, 失敗:false
	 */
	public boolean enableMACSecurity(String ip6Address, String macAddress) {
		SKSecEnable sksecenable = new SKSecEnable(1, ip6Address, macAddress);
		return sendCommandAndWaitOK(sksecenable);
	}

	/**
	 * 指定したIPアドレスに対するMAC層セキュリティを無効化
	 * @param ip6Address セキュリティを無効にする対象のIPv6アドレス
	 * @return 無効化に成功:true, 失敗:false
	 */
	public boolean disableMACSecruity(String ip6Address) {
		SKSecEnable sksecenable = new SKSecEnable(0, ip6Address);
		return sendCommandAndWaitOK(sksecenable);
	}

	/**
	 * PANA認証に用いるPSKを登録
	 * @param key PSKバイト列（16進表現32文字）
	 * @return 登録に成功:true, 失敗:false
	 */
	public boolean setPSK(String key) {
		SKSetPsk sksetpsk = new SKSetPsk(key);
		return sendCommandAndWaitOK(sksetpsk);
	}

	/**
	 * 指定したパスワードからPSKを生成して登録
	 * @param password パスワード
	 * @return 登録に成功:true, 失敗;false
	 */
	public boolean setPSKFromPassword(String password) {
		SKSetPwd sksetpwd = new SKSetPwd(password);
		return sendCommandAndWaitOK(sksetpwd);
	}

	/**
	 * 指定されたIDから各Route-B IDを生成して設定<br>
	 * Paring IDとしてIDの下位8文字が設定される
	 * @param id ID, 32桁のASCII文字列
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setRouteBID(String id) {
		SKSetRbid sksetribd = new SKSetRbid(id);
		return sendCommandAndWaitOK(sksetribd);
	}

	/**
	 * 指定したIPアドレスと64bitアドレス情報をIP層ネイバーキャッシュにReachable状態で登録<br>
	 * これにより、アドレス要請を省略して直接IPパケットを送出可となる。
	 * @param ip6Address 登録するIPv6アドレス
	 * @param macAddress 登録するIPv6アドレスに対応する64bitアドレス（16進表現16文字）
	 * @return 登録に成功:true, 失敗:false
	 */
	public boolean registerNeighborCache(String ip6Address, String macAddress) {
		SKAddNbr skaddnbr = new SKAddNbr(ip6Address, macAddress);
		return sendCommandAndWaitOK(skaddnbr);
	}

	/**
	 * UDPの待ち受けポートを指定<br>
	 * 設定したポートは、saveConfig()で保存した後、電源再投入時にオートロードした場合に有効となる。
	 * @param handle 対応するハンドル番号（値域:1-6）
	 * @param port ハンドル番号に割り当てられる待ち受けポート番号（値域:0-0xFFFF）<br>
	 * 0を指定した場合、そのハンドルは未使用となりポートは着信しない。0xFFFFも予約番号で着信しない。
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setUDPPort(byte handle, int port) {
		SKUdpPort skudpport = new SKUdpPort(handle, port);
		return sendCommandAndWaitOK(skudpport);
	}

	/**
	 * TCPの待ち受けポートを指定<br>
	 * 設定したポートは、saveConfig()で保存した後、電源再投入時にオートロードした場合に有効となる。
	 * @param index 対応するハンドル番号（値域:1-4）
	 * @param port ハンドル番号に割り当てられる待ち受けポート番号（値域:0-0xFFFF）<br>
	 * 0を指定した場合、そのハンドルは未使用となりポートは着信しない。0xFFFFも予約番号で着信しない。
	 * @return 設定に成功:true, 失敗:false
	 */
	public boolean setTCPPort(byte index, int port) {
		SKTcpPort sktcpport = new SKTcpPort(index, port);
		return sendCommandAndWaitOK(sktcpport);
	}

	/**
	 * 現在の仮想レジスタの内容を不揮発性メモリに保存
	 * @return 保存に成功:true, 失敗:false
	 */
	public boolean saveConfig() {
		SKSave sksave = new SKSave();
		return sendCommandAndWaitOK(sksave);
	}

	/**
	 * 不揮発性メモリに保存されている仮想レジスタの内容をロード
	 * @return ロードに成功:true, 失敗:false
	 */
	public boolean loadConfig() {
		SKLoad skload = new SKLoad();
		return sendCommandAndWaitOK(skload);
	}

	/**
	 * レジスタ保存用の不揮発性メモリエリアを初期化し、未保存状態に戻す
	 * @return 初期化に成功:true, 失敗:false
	 */
	public boolean eraseConfig() {
		SKErase skerase = new SKErase();
		return sendCommandAndWaitOK(skerase);
	}

	/**
	 * SKSTACK IPのファームウェアバージョンを取得
	 * @return x.x.x形式のバージョン番号、取得に失敗した場合は""
	 */
	public String getVersion() {
		SKVer skver = new SKVer();
		String[] res = sendCommandAndGetResponse(skver, 2);
		if (res == null) {
			return "";
		}

		SKEVer ever = new SKEVer();
		if (ever.parse(res[0])) {
			return ever.getVersion();
		}
		else {
			return "";
		}
	}

	/**
	 * プロトコル・スタックの内部状態を初期化<br>
	 * ただし、64bitアドレスのみ、直近の値が再利用される。
	 * @return 初期化に成功:true, 失敗:false
	 */
	public boolean resetStack() {
		SKReset skreset = new SKReset();
		return sendCommandAndWaitOK(skreset);
	}

	/**
	 * デバイスで利用可能なIPアドレス一覧を取得
	 * @return SKEAddrの配列、取得に失敗した場合はnull
	 */
	public SKEAddr[] getIP6AddressTable() {
		int i;
		int length;
		ArrayList<String> list = getTableList(SKEventType.EADDR, (byte)1, 10);

		if (list == null) {
			return null;
		}

		length = list.size();
		SKEAddr[] aryEaddr = new SKEAddr[length];
		for (i = 0; i < length; i++) {
			aryEaddr[i] = new SKEAddr();
			if (!aryEaddr[i].parse(list.get(i))) {
				return null;
			}
		}

		return aryEaddr;
	}

	/**
	 * ネイバーキャッシュ一覧を取得
	 * @return SKENeighborの配列、取得に失敗した場合はnull
	 */
	public SKENeighbor[] getNeighborTable() {
		int i;
		int length;
		ArrayList<String> list = getTableList(SKEventType.ENEIGHBOR, (byte)2, 10);

		if (list == null) {
			return null;
		}

		length = list.size();
		SKENeighbor[] aryEneighbor = new SKENeighbor[length];
		for (i = 0; i < length; i++) {
			aryEneighbor[i] = new SKENeighbor();
			if (!aryEneighbor[i].parse(list.get(i))) {
				return null;
			}
		}

		return aryEneighbor;
	}

	/**
	 * TCPハンドル状態一覧を取得
	 * @return SKEHandleの配列、取得に失敗した場合はnull
	 */
	public SKEHandle[] getTCPHandleTable() {
		int i;
		int length;
		ArrayList<String> list = getTableList(SKEventType.EHANDLE, (byte)0x0F, 8);

		if (list == null) {
			return null;
		}

		length = list.size();
		SKEHandle[] aryEhandle = new SKEHandle[length];
		for (i = 0; i < length; i++) {
			aryEhandle[i] = new SKEHandle();
			if (!aryEhandle[i].parse(list.get(i))) {
				return null;
			}
		}

		return aryEhandle;
	}

	/**
	 * SKTABLEコマンドを発行し、応答を格納したリストを返す
	 * @param type 応答するイベント種類（"Exxxx")
	 * @param mode 取得するテーブルの種類
	 * @param numResLine 応答の最大行数
	 * @return 応答を格納したリスト（"Exxxx"、"OK"を除く）, 取得できなかった場合はnull
	 */
	private ArrayList<String> getTableList(SKEventType type, byte mode, int numResLine) {
		int i;
		int length;

		SKTable sktable = new SKTable(mode);
		String[] res = sendCommandAndGetResponse(sktable, numResLine);

		if (res == null) {
			return null;
		}

		if (!res[0].startsWith(type.name())) {
			return null;
		}

		length = res.length;
		ArrayList<String> resList = new ArrayList<String>();
		for (i = 1; i < length; i++) {
			if (res[i].compareTo(SKErrorCode.OK.toString()) == 0) {
				break;
			}
			else {
				resList.add(res[i]);
			}
		}

		return resList;
	}

	/**
	 * SKSREGコマンドを発行し、仮想レジスタの値を取得<br>
	 * @param register 対象の仮想レジスタ
	 * @return 仮想レジスタの値（文字列）,取得できなかった場合は""
	 */
	private String getRegisterValue(SKRegister register) {
		SKSregGet sksreg = new SKSregGet(register);
		String[] res = sendCommandAndGetResponse(sksreg, 2);

		if (res == null) {
			return "";
		}
		else {
			SKESreg esreg = new SKESreg();
			if (esreg.parse(res[0])) {
				return esreg.getValue();
			}
			else {
				return "";
			}
		}
	}

	/**
	 * SKコマンドを発行し、"OK"を待機
	 * @param command 発行するSKコマンド
	 * @return "OK"が応答されたらtrue, それ以外はfalse
	 */
	private boolean sendCommandAndWaitOK(SKCommand command) {
		boolean sendOK;

		try {
			OutputStream out = port.getOutputStream();
			sendOK = command.issueCommand(out);
			out.close();

			debugOut(command);

			if(sendOK) {
				String[] res = reader.getResponse(1, commandTimeout);
				if (res == null) {
					return false;
				}

				if (res[0] == null) {
					return false;
				}

				if (res[0].startsWith(SKErrorCode.OK.toString())) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		catch (IOException e) {
			return false;
		}
	}

	/**
	 * SKコマンドを発行し、応答を取得
	 * @param command 発行するSKコマンド
	 * @param numOfLine 応答の行数
	 * @return 応答文字列を格納した配列（1行1要素）、取得できなかった場合はnull
	 */
	private String[] sendCommandAndGetResponse(SKCommand command, int numOfLine) {
		boolean sendOK;
		String[] res;

		try {
			OutputStream out = port.getOutputStream();
			sendOK = command.issueCommand(out);
			out.close();

			debugOut(command);

			if(sendOK) {
				res = reader.getResponse(numOfLine, commandTimeout);
				return res;
			}
			else {
				return null;
			}
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * デバッグ情報のリスナーが登録されていれば、コマンド文字列を通知
	 * @param command SKCommandのインスタンス
	 */
	private void debugOut(SKCommand command) {
		if (debugListener != null) {
			debugListener.debugOut(portString, command.getCommandString());
		}
	}

}
