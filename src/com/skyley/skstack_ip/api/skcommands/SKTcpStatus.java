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

package com.skyley.skstack_ip.api.skcommands;

/**
* TCP処理ステータスを列挙
* @author Skyley Networks, Inc.
* @version 0.1
*/
public enum SKTcpStatus {
	/** 接続完了 */
	CONNECT_DONE((byte)1),
	/** 切断完了（接続失敗、データ送信タイムアウトによる切断を含む） */
	CLOSE_DONE((byte)3),
	/** 指定した送信元ポート番号が使用中 */
	LPORT_USED((byte)4),
	/** データ送信完了 */
	TX_DONE((byte)5);

	/** ステータスraw value */
	private byte status;

	/**
	 * コンストラクタ
	 * @param status ステータスraw value
	 */
	private SKTcpStatus(byte status) {
		this.status = status;
	}

	/**
	 * ステータスのraw valueを取得
	 * @return ステータスraw value
	 */
	public byte getStatus() {
		return status;
	}

	/**
	 * ステータスraw valueに対応する名前を取得
	 * @param value ステータスraw value
	 * @return valueに対応する名前
	 */
	public static String getName(byte value) {
		for (SKTcpStatus s : SKTcpStatus.values()) {
			if (s.status == value) {
				return s.name();
			}
		}

		return "";
	}
}
