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

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.skyley.skstack_ip.api.skenums.SKDeviceModel;
import com.skyley.skstack_ip.api.skenums.SKErrorCode;
import com.skyley.skstack_ip.api.skenums.SKEventType;
import com.skyley.skstack_ip.api.skevents.SKEvent;
import com.skyley.skstack_ip.api.skevents.SKEventFactory;

/**
 * 受信バッファを読み込み、必要な処理を行う。
 * @author Skyley Networks, Inc.
 * @version 0.1
*/
public class SKRxBufferReader implements Runnable {
	/** 受信バッファ、SKReceiverと共有する */
	private BlockingQueue<String> buffer;
	/** デバイス機種 */
	private SKDeviceModel model;
	/** デバイス接続先ポートの名称（"COM3など"） */
	private String portString;
	/** "Exxx"系のイベントリスナー */
	private SKEventListener listener;
	/** 受信バッファから読み取ったコマンド応答を格納するバッファ */
	private BlockingQueue<String> response;
	 /** 読み込み処理実行中を示すフラグ */
	private boolean isRunning;
	/** デバッグ情報のリスナー */
	private SKDebugListener debugListener;

	/**
	 * コンストラクタ
	 * @param buffer 受信バッファ、SKReceiverと共有する
	 * @param model デバイス機種
	 * @param portString デバイス接続先ポートの名称（"COM3"など）
	 */
	public SKRxBufferReader(BlockingQueue<String> buffer, SKDeviceModel model, String portString) {
		this.buffer = buffer;
		this.model = model;
		this.portString = portString;
		response = new ArrayBlockingQueue<String>(64);
		isRunning = false;
	}

	/**
	 * 登録されている"Exxxx"系のイベントリスナーを取得
	 * @return "Exxxx"系のイベントリスナー、SKEventListenerを実装したクラス
	 */
	public SKEventListener getSKEventListener() {
		return listener;
	}

	/**
	 * "Exxxx"系のイベントリスナーを登録
	 * @param listener "Exxxx"系のイベントリスナー、SKEventListenerを実装したクラス
	 */
	public void setSKEventListener(SKEventListener listener) {
		this.listener = listener;
	}

	/**
	 * "Exxxx"系のイベントリスナー登録を解除
	 */
	public void removeSKEventListener() {
		this.listener = null;
	}

	/**
	 * デバッグ情報のリスナーを登録
	 * @param listener デバッグ情報のリスナー、SKDebugListenerを実装したクラス
	 */
	public void setSKDebugLIstener(SKDebugListener listener) {
		debugListener = listener;
	}

	/**
	 * デバッグ情報のリスナー登録を解除
	 */
	public void removeSKDebugListener() {
		debugListener = null;
	}

	/**
	 * bufferから読み取った応答文字列を取得
	 * @param numOfLine 応答の行数（0の場合はエラーコードが返るまで取得）
	 * @param timeout タイムアウト（単位 ミリ秒）
	 * @return 応答文字列を格納した配列（1行1要素）、取得できなかった場合はnull
	 */
	public String[] getResponse(int numOfLine, long timeout) {
		if (numOfLine < 0) {
			return null;
		}
		else if (numOfLine == 0) {
			return getResponse(timeout);
		}

		String[] res = new String[numOfLine];

		try {
			for(int i = 0; i < numOfLine; i++) {
				res[i] = response.poll(timeout, TimeUnit.MILLISECONDS);
				if (res[i] == null) {
					return null;
				}
				else {
					for (SKErrorCode code : SKErrorCode.values()) {
						// エラーコードと合致した場合は、その時点まで読みとった内容を返す
						if (res[i].trim().startsWith(code.toString())) {
							return res;
						}
					}
				}
			}

			return res;
		}
		catch(Exception e) {
			return null;
		}
	}

	/**
	 * bufferから読み取った応答文字列を取得、エラーコードが返るか、読み取りがタイムアウトしたらreturnする
	 * @param timeout タイムアウト（単位 ミリ秒）
	 * @return 応答文字列を格納した配列（1行1要素）、取得できなかった場合はnull
	 */
	private String[] getResponse(long timeout) {
		String line;
		ArrayList<String> list = new ArrayList<String>();

		try {
			while (true) {
				line = response.poll(timeout, TimeUnit.MILLISECONDS);
				if (line == null) {
					return (String[]) list.toArray(new String[0]);
				}

				list.add(line);
				for (SKErrorCode code : SKErrorCode.values()) {
					// エラーコードと合致した場合は、その時点まで読みとった内容を返す
					if (line.trim().startsWith(code.toString())) {
						return (String[]) list.toArray(new String[0]);
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			return null;
		}
	}

	/**
	 * 読み込み処理を停止
	 */
	public void stop() {
		isRunning = false;
	}

	/**
	 * 読み込み処理を実行
	 */
	@Override
	public void run() {
		boolean isMatched;
		isRunning = true;
		String line;

		try {
			while(isRunning) {
				// bufferから1要素読み込み（1行1要素）
				line = buffer.poll(1, TimeUnit.SECONDS);
				isMatched = false;

				debugOut(line);

				// タイムアウトしたらbuufer.poll()に戻る
				if (line == null) {
					continue;
				}

				// 空のときはbuffer.poll()に戻る
				if (line == "") {
					continue;
				}

				// SKコマンドのエコーバックは無視、buffer.poll()に戻る
				if (line.startsWith("SK")) {
					continue;
				}

				// "OK", "FAIL ER10"などのエラーコードであれば、respoonseに格納
				// responseの内容はgetResponse()を通してSKDeviceに読み取られる
				for(SKErrorCode code : SKErrorCode.values()) {
					if(line.startsWith(code.toString())) {
						response.put(line);
						isMatched = true;
						continue;
					}
				}

				// エラーコードと一致した場合はbuffer.poll()に戻る
				if (isMatched) {
					continue;
				}

				// "ESREG", "ERXUDP"など"Exxxx"系イベントと判定すると、responseに格納、またはlistnerに通知
				for(SKEventType type : SKEventType.values()) {
					if(line.startsWith(type.name())) {
						// コマンド応答として扱う"Exxx"のときはresponseに格納
						if(type.isResponse()) {
							response.put(line);
						}
						else if (listener != null) {
							SKEventFactory ef = new SKEventFactory();
							SKEvent event = ef.createSKEvent(type, model, buffer, debugListener, portString);
							// イベントとして扱い、文字列を解析できた場合は、listenerに通知
							if(event.parse(line)) {
								listener.eventNotified(portString, type, event);
							}
						}

						isMatched = true;
						continue;
					}
				}

				// "Exxxx"系イベントと一致した場合はbuffer.poll()に戻る
				if (isMatched) {
					continue;
				}

				// SKコマンド、エラーコード、"Exxxx"以外の文字列をresponseに格納
				response.put(line);
			}
		}
		catch(Exception e) {
			isRunning = false;
			e.printStackTrace();
		}
	}

	/**
	 * デバッグ情報のリスナーが登録されていれば、受信したイベント文字列を通知
	 * @param line 受信したイベント文字列
	 */
	private void debugOut(String line) {
		if (debugListener != null && line != null) {
			debugListener.debugOut(portString, line);
		}
	}
}
