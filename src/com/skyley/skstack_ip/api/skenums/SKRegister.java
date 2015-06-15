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
 * 仮想レジスタを列挙
 * @author Skyley Netowkrs, Inc.
 * @version 0.1
 */
public enum SKRegister {
	/** IEEE 64bit MACアドレス */
	LONG_ADDRESS("S01"),
	/** 論理チャンネル番号 */
	CHANNEL("S02"),
	/** PAN ID */
	PAN_ID("S03"),
	/** MAC層セキュリティフレームカウンタ(Read only) */
	SEC_FRAME_COUNTER("S07"),
	/** Paring ID（ASCII文字列） */
	PARING_ID("S0A"),
	/** paring ID（バイト列） */
	PARING_ID_BYTE("S0B"),
	/** ビーコン応答制御フラグ */
	BEACON_RESPONSE("S15"),
	/** PANAセッションライフタイム（単位 秒） */
	PANA_SESSION_LIFETIME("S16"),
	/** PANA自動再認証フラグ */
	PANA_AUTO_AUTH("S17"),
	/** PAA鍵更新周期（単位 秒） */
	PAA_KEY_UPDATE_PERIOD("S1C"),
	/** 鍵切り替えメッセージ送信回数 */
	PAA_KEY_UPDATE_MESSAGE_NUM("S1D"),
	/** リレーデバイスモード */
	RELAY_DEVICE_MODE("S1E"),
	/** リレーデバイスアドレス（Read Only） */
	RELAY_DEVICE_ADDRESS("S1F"),
	/** MAC層ブロードキャストに対するセキュリティ制御フラグ */
	MAC_BROADCAST_SEC("SA0"),
	/** ICMPメッセージ受信処理制御フラグ */
	ICMP_RX("SA1"),
	/** 送信出力レベル */
	TX_POWER_LEVEL("SA2"),
	/** Ack待ち時間（単位 10usec） */
	ACK_WAITING_TIME("SA5"),
	/** データホワイトニング制御フラグ */
	DATA_WHITENING("SA7"),
	/** 低速データレートフラグ */
	LOW_DATA_RATE_FLAG("SA8"),
	/** CCCA閾値（高データレート） */
	CCA_THRESHOLD_HIGH_RATE("SF7"),
	/** CCCA閾値（低データレート） */
	CCA_THRESHOLD_LOW_RATE("SF8"),
	/** DSN多重チェック制御フラグ */
	DSN_MULTIPLE_CHECK("SF9"),
	/** 送信時間制限の制御フラグ */
	TX_LIMIT("SFA"),
	/** 送信時間制限中フラグ(Read only) */
	TX_LIMIT_WORKING("SFB"),
	/** 無線送信の積算時間（単位 ミリ秒、Read only） */
	CUMULARIVE_TX_TIME("SFD"),
	/** エコーバックフラグ */
	COMMAND_ECHO_BACK("SFE"),
	/** オートロードフラグ */
	AUTO_LOAD("SFF");

	/** レジスタ番号、Sで始まる文字列 */
	private String regNumber;

	/**
	 * コンストラクタ
	 * @param regNumber レジスタ番号、Sで始まる文字列
	 */
	private SKRegister(String regNumber) {
		this.regNumber =regNumber;
	}

	/**
	 * レジスタ番号を返す
	 */
	public String toString() {
		return regNumber;
	}
}
