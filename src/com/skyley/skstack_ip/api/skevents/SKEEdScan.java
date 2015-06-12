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

import java.util.HashMap;

/**
 * EEDSCANイベントに対応したクラス、SKEventを実装
 * @author Skyley Networks, Inc.
 * @version 0.1
*/
public class SKEEdScan implements SKEvent {
	/** "EEDSCAN"に続く受信文字列 */
	private String raw;
	/** （チャンネル・RSSI）を格納したマップ */
	private HashMap<Byte, Short> edLevel;

	/**
	 * コンストラクタ
	 * @param raw "EEDSCAN"に続く受信文字列
	 */
	public SKEEdScan(String raw) {
		this.raw = raw;
		edLevel = new HashMap<Byte, Short>();
	}

	/**
	 * （チャンネル・RSSI）を格納したマップを取得
	 * @return （チャンネル・RSSI）を格納したマップ
	 */
	public HashMap<Byte, Short> getEdLevel() {
		return edLevel;
	}

	/**
	 * 受信文字列を解析、パラメータを格納
	 */
	@Override
	public boolean parse(String raw) {
		// TODO 自動生成されたメソッド・スタブ
		if(this.raw == null) {
			return false;
		}

		String[] ary = this.raw.split(" ");
		int index = 0;
		int length = ary.length;
		try {
			while (index < length) {
				edLevel.put(Byte.parseByte(ary[index], 16), Short.parseShort(ary[index+1], 16));
				index += 2;
			}

			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

}
