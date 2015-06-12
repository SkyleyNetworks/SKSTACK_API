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

package com.skyley.skstack_ip.api.skevents;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.skyley.skstack_ip.api.SKDebugListener;
import com.skyley.skstack_ip.api.skenums.SKDeviceModel;
import com.skyley.skstack_ip.api.skenums.SKEventType;

/**
* "Exxxx"系イベントに対応したクラスのインスタンスを生成
* @author Skyley Networks, Inc.
* @version 0.1
*/
public class SKEventFactory {

	/**
	 * typeに応じたクラスのインスタンスを生成
	 * @param type  イベント種類
	 * @param model デバイス機種
	 * @param buffer 受信バッファ
	 * @param listener デバッグ情報のリスナー
	 * @param port デバイスの接続先ポート名
	 * @return SKEventを実装したクラスのインスタンス
	 */
	public SKEvent createSKEvent(SKEventType type, SKDeviceModel model, BlockingQueue<String> buffer,
								 SKDebugListener listener, String port) {
		String res;

		switch(type) {
			case ERXUDP:
				return new SKERxUdp(model);

			case ERXTCP:
				return new SKERxTcp();

			case ETCP:
				return new SKETcp();

			case EPANDESC:
				StringBuilder sb = new StringBuilder();
				int num;

				switch (model) {
					case GENERAL:
						num = 6;
						break;

					case HAN_EXTENSION:
						num = 9;
						break;

					default:
						return new SKEPanDesc("", model);
				}

				try {
					for (int i = 0; i < num; i++) {
						res = buffer.poll(1, TimeUnit.SECONDS);
						debugOut(listener, port, res);

						if (res == null) {
							return new SKEPanDesc(sb.toString(), model);
						}
						else {
							sb.append(res.trim());
							sb.append(",");
						}
					}

					return new SKEPanDesc(sb.toString(), model);
				}
				catch (InterruptedException e) {
					return new SKEPanDesc("", model);
				}

			case EEDSCAN:
				try {
					res = buffer.poll(1, TimeUnit.SECONDS);
					debugOut(listener, port, res);
				}
				catch (InterruptedException e) {
					return new SKEEdScan("");
				}

				if (res == null) {
					return new SKEEdScan("");
				}
				else {
					return new SKEEdScan(res);
				}

			case EVENT:
				return new SKGeneralEvent();

			default:
				return null;
		}
	}

	/**
	 * デバッグ情報のリスナーが登録されていれば、受信した文字列を通知
	 * @param listener デバッグ情報のリスナー
	 * @param port デバイスの接続先ポート名
	 * @param line 受信した文字列
	 */
	private void debugOut(SKDebugListener listener, String port, String line) {
		if (listener != null && line != null) {
			listener.debugOut(port, line);
		}
	}

}
