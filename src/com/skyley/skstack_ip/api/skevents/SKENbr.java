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

import com.skyley.skstack_ip.api.SKUtil;

/**
 * ENBRイベントに対応したクラス、SKEventを実装
 * @author Skyley Networks, Inc.
 * @version 0.1
*/
public class SKENbr implements SKEvent {
	/** ネイバーテーブルに登録されているIPv6アドレス */
	private String ip6Address;
	/** このデバイスに対してMACセキュリティが有効か示すフラグ */
	private boolean security;
	/** このデバイスに対応するMAC層64bitアドレス */
	private String macAddress;
	/** このデバイス宛に送信する際に利用されるリレーデバイスのMACアドレス<br>
	 *  （すべて0の場合は、リレーデバイスを利用せず直接通信）
	*/
	private String nextHop;
	/** このデバイスに対応する暗号フレームのフレームカウンタ */
	private long frameCounter;
	/** このデバイスのauthentication counter値 */
	private short authCounter;
	/** このデバイスとのPANAセッションID */
	private long sessionID;
	/** このデバイスの key id avp値 */
	private long keyIDAvp;
	/** このデバイスとのPANA認証で適用されるPSK値 */
	private String psk;

	/**
	 * IPv6アドレスを取得
	 * @return IPv6アドレス
	 */
	public String getIP6Address() {
		return ip6Address;
	}

	/**
	 * セキュリティフラグを取得
	 * @return セキュリティフラグ
	 */
	public boolean isSecured() {
		return security;
	}

	/**
	 * MACアドレスを取得
	 * @return MACアドレス
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * リレーデバイスのMACアドレスを取得
	 * @return リレーデバイスのMACアドレス
	 */
	public String getNextHop() {
		return nextHop;
	}

	/**
	 * フレームカウンタを取得
	 * @return フレームカウンタ
	 */
	public long getFrameCounter() {
		return frameCounter;
	}

	/**
	 * authentication counter値を取得
	 * @return authentication counter値
	 */
	public short getAuthCounter() {
		return authCounter;
	}

	/**
	 * PANAセッションIDを取得
	 * @return PANAセッションID
	 */
	public long getSessionID() {
		return sessionID;
	}

	/**
	 * key id avp値を取得
	 * @return key id avp値
	 */
	public long getKeyIDAvp() {
		return keyIDAvp;
	}

	/**
	 * PSK値を取得
	 * @return PSK値
	 */
	public String getPsk() {
		return psk;
	}

	/**
	 * 受信文字列を解析、パラメータを格納
	 */
	@Override
	public boolean parse(String raw) {
		// TODO 自動生成されたメソッド・スタブ
		String[] ary = raw.split(",");
		if (ary.length < 9) {
			return false;
		}

		if (SKUtil.isValidIP6Address(ary[0])) {
			ip6Address = ary[0];
		}
		else {
			return false;
		}

		if (ary[1].startsWith("security:")) {
			String sec = ary[1].substring(9);
			if (sec.compareTo("1") == 0) {
				security = true;
			}
			else if (sec.compareTo("0") == 0) {
				security = false;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}

		if (ary[2].startsWith("mac addr:")) {
			macAddress = ary[2].substring(9);
		}
		else {
			return false;
		}

		if (ary[3].startsWith("next hop:")) {
			nextHop = ary[3].substring(9);
		}
		else {
			return false;
		}

		try {
			if (ary[4].startsWith("frame counter:")) {
				frameCounter = Long.parseLong(ary[4].substring(14), 16);
			}
			else {
				return false;
			}

			if (ary[5].startsWith("auth cnt:")) {
				authCounter = Short.parseShort(ary[5].substring(9), 16);
			}
			else {
				return false;
			}

			if (ary[6].startsWith("session id:")) {
				sessionID = Long.parseLong(ary[6].substring(11), 16);
			}
			else {
				return false;
			}

			if (ary[7].startsWith("key id avp:")) {
				keyIDAvp = Long.parseLong(ary[7].substring(11), 16);
			}
			else {
				return false;
			}
		}
		catch (NumberFormatException e) {
			return false;
		}

		if (ary[8].startsWith("psk:")) {
			psk = ary[8].substring(4);
		}
		else {
			return false;
		}

		return true;
	}

}
