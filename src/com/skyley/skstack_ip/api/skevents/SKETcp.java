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

/**
* ETCPイベントに対応したクラス、SKEventを実装
* @author Skyley Networks, Inc.
* @version 0.1
*/
public class SKETcp implements SKEvent {
	/** TCP処理ステータス */
	private byte status;
	/** イベント対象となったハンドル番号 */
	private byte handle;
	/** 接続先/接続元IPv6アドレス */
	private String ip6Address;
	/** 相手側接続ポート番号 */
	private int rport;
	/** 時端末接続ポート番号 */
	private int lport;

	/**
	 * TCP処理ステータスを取得
	 * @return TCP処理ステータス
	 */
	public byte getStatus() {
		return status;
	}

	/**
	 * ハンドル番号を取得
	 * @return ハンドル番号
	 */
	public byte getHandle() {
		return handle;
	}

	/**
	 * 接続先/接続元IPv6アドレスを取得
	 * @return 接続先/接続元IPv6アドレス
	 */
	public String getIP6Address() {
		return ip6Address;
	}

	/**
	 * 相手側接続ポート番号を取得
	 * @return 相手側接続ポート番号
	 */
	public int getRPort() {
		return rport;
	}

	/**
	 * 自端末接続ポート番号を取得
	 * @return 自端末ポート番号
	 */
	public int getLPort() {
		return lport;
	}

	/**
	 * 受信文字列を解析、パラメータを格納
	 */
	@Override
	public boolean parse(String raw) {
		// TODO 自動生成されたメソッド・スタブ
		if (raw == null) {
			return false;
		}

		String[] ary = raw.split(" ");
		try {
			if (ary.length == 3) {
				status = Byte.parseByte(ary[1], 16);
				handle = Byte.parseByte(ary[2], 16);
				return true;
			}
			else if (ary.length == 6) {
				status = Byte.parseByte(ary[1], 16);
				handle = Byte.parseByte(ary[2], 16);
				ip6Address = new String(ary[3]);
				rport = Integer.parseInt(ary[4], 16);
				lport = Integer.parseInt(ary[5], 16);
				return true;
			}
			else {
				return false;
			}
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

}
