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

package com.skyley.skstack_ip.api.skenums;

/**
* "EVENT"イベントのイベント番号を列挙
* @author Skyley Networks, Inc.
* @version 0.1
*/
public enum SKEventNumber {
	/** NSを受信した */
	NS_RECEIVED(0x01),
	/** NAを受信した */
	NA_RECEIVED(0x02),
	/** Echo Requestを受信した */
	ECHO_REQUEST_RECEIVED(0x05),
	/** EDスキャンが完了した */
	ED_SCAN_DONE(0x1F),
	/** Beaconを受信した */
	BEACON_RECEIVED(0x20),
	/** UDP送信処理が完了した */
	UDP_TX_DONE(0x21),
	/** アクティブスキャンが完了した */
	ACTIVE_SCAN_DONE(0x22),
	/** PANA接続が完了しなかった */
	PANA_CONNECT_FAIL(0x24),
	/** PANA接続が完了した */
	PANA_CONNECT_DONE(0x25),
	/** PANAセッションの終了要求を受信した */
	PANA_SESSION_CLOSE_REQUEST_RECEIVED(0x26),
	/** PANAセッションの終了に成功した */
	PANA_SESSION_CLOSE_DONE(0x27),
	/** PANAセッションの終了要求がタイムアウトした（セッションは終了） */
	PANA_SESSION_CLOSE_TIMEOUT(0x28),
	/** PANAセッションのライフタイムが期限切れになった */
	PANA_SESSION_LIFETIME_EXPIRED(0x29),
	/** 送信総和時間の制限が発動した */
	TX_LIMIT_START(0x32),
	/** 送信総和時間の制限が解除された */
	TX_LIMIT_END(0x33),
	/** 鍵要求メッセージを送信した（PaCで発生） */
	KEY_REQUEST_TX_DONE(0x40),
	/** 鍵要求に対する応答を受信した（PaCで発生） */
	KEY_REQUEST_RESPONSE_RECEIVED(0x41),
	/** 鍵要求に対する応答が受信できなかった（PaCで発生） */
	KEY_REQUEST_RESPONSE_NOT_RECEIVED(0x42),
	/** 鍵配布メッセージを受信した（PaCで発生） */
	KEY_DISTRIBUTION_RECEIVED(0x43),
	/** 鍵切り替えメッセージを受信した（PaCで発生） */
	KEY_UPDATE_RECEIVED(0x44),
	/** 現在使用中の暗号鍵と異なるキーインデックスの暗号文を受信した（PaCで発生） */
	KEY_INDEX_UNMATCHED(0x45),
	/** 鍵切り替えメッセージが受信できずタイムアウトした（PaCで発生） */
	KEY_UPDATE_TIMEOUT(0x46),
	/** 鍵配布メッセージを送信した（PAAで発生） */
	KEY_DISTRIBUTION_TX_DONE(0x50),
	/** 鍵配布に対する応答を受信した（PAAで発生） */
	KEY_DISTRIBUTION_RESPONSE_RECEIVED(0x51),
	/** 鍵配布に対する応答が受信できなかった（PAAで発生） */
	KEY_DISITRIBUTION_RESPONSE_NOT_RECEIVED(0x52),
	/** 鍵要求メッセージを受信した（PAAで発生） */
	KEY_REQUEST_RECEIVED(0x53),
	/** 自動鍵配信処理を開始した（PAAで発生） */
	KEY_AUTO_DISTRIBUTION_START(0x54),
	/** 自動鍵配信処理を終了した（PAAで発生） */
	KEY_AUTO_DISTRIBUTION_END(0x55),
	/** イニシャルモードを開始した */
	INITIAL_MODE_START(0x56),
	/** イニシャルモードが終了した */
	INITIAL_MODE_END(0x57);

	/** イベント番号 */
	private short number;

	/**
	 * コンストラクタ
	 * @param number イベント番号
	 */
	private SKEventNumber(int number) {
		this.number = (short)number;
	}

	/**
	 * イベント番号を取得
	 * @return イベント番号
	 */
	public short getNumber() {
		return number;
	}

	/**
	 * イベント番号からイベント名を取得
	 * @param number イベント番号
	 * @return numberに対応するイベント名
	 */
	public static String getEventName(short number) {
		for (SKEventNumber en : SKEventNumber.values()) {
			if (en.number == number) {
				return en.name();
			}
		}

		return "";
	}

}
