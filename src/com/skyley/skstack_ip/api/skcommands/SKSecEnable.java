﻿/****
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

import com.skyley.skstack_ip.api.SKUtil;

/**
* SKSECENABLEコマンドに対応したクラス、SKCommandを継承
* @author Skyley Networks, Inc.
* @version 0.1
*/
public class SKSecEnable extends SKCommand {
	/** セキュリティ適用/無効フラグ */
	private int mode;
	/** 対象IPv6アドレス */
	private String ip6Address;
	/** 対象IPv6アドレスに対応する64bitアドレス */
	private String macAddress;

	/**
	 * コンストラクタ
	 * @param mode セキュリティ適用/無効フラグ
	 * @param ip6Address 対象IPv6アドレス
	 */
	public SKSecEnable(int mode, String ip6Address) {
		this.mode = mode;
		this.ip6Address = ip6Address;
	}

	/**
	 * コンストラクタ
	 * @param mode セキュリティ適用/無効フラグ
	 * @param ip6Address 対象IPv6アドレス
	 * @param macAddress 対象IPv6アドレスに対応する64bitアドレス
	 */
	public SKSecEnable(int mode, String ip6Address, String macAddress) {
		this.mode = mode;
		this.ip6Address = ip6Address;
		this.macAddress = macAddress;
	}

	/**
	 * 引数チェック
	 */
	@Override
	public boolean checkArgs() {
		// TODO 自動生成されたメソッド・スタブ
		if (mode != 0 && mode !=1) {
			return false;
		}

		if (!SKUtil.isValidIP6Address(ip6Address)) {
			return false;
		}

		if (mode == 0 && macAddress == null) {
			macAddress = "1234567890123456"; // dummy
		}

		return SKUtil.isValidLongAddress(macAddress);
	}

	/**
	 * コマンド文字列組み立て
	 */
	@Override
	public void buildCommand() {
		// TODO 自動生成されたメソッド・スタブ
		commandString = "SKSECENABLE " + Integer.toString(mode, 16) + " " + ip6Address + " " + macAddress + "\r\n";
	}

}
