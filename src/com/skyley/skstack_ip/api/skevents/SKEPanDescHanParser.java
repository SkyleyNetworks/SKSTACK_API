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
 * SKHanDeviceのEPANDESC文字列を解析する
 * @author Skyley Networks, Inc.
 * @version 0.1
 */
public class SKEPanDescHanParser implements SKEPanDescParser {

	/**
	 * 文字列を解析し、パラメータを格納
	 */
	@Override
	public boolean parsePanDesc(SKEPanDesc ePanDesc) {
		// TODO 自動生成されたメソッド・スタブ
		String raw = ePanDesc.getRaw();
		if (raw == null) {
			return false;
		}

		try {
			String[] ary = raw.split(",");
			int length = ary.length;
			if (length != 8 && length != 9) {
				return false;
			}

			String[] aryParam = new String[9];
			for (int i = 0; i < length; i++) {
				String[] ary2 = ary[i].trim().split(":");
				if (ary2.length != 2) {
					return false;
				}
				aryParam[i] = ary2[1];
			}

			ePanDesc.setChannel(Byte.parseByte(aryParam[0], 16));
			ePanDesc.setChannelPage(Byte.parseByte(aryParam[1], 16));
			ePanDesc.setPanID(Integer.parseInt(aryParam[2], 16));
			ePanDesc.setAddress(aryParam[3]);
			ePanDesc.setLQI(Short.parseShort(aryParam[4], 16));
			int index;
			if (length == 9) {
				ePanDesc.setPairID(aryParam[5]);
				index = 6;
			}
			else {
				index = 5;
			}
			ePanDesc.setHemsAddress(aryParam[index]);
			index++;
			if (aryParam[index].compareTo("1") == 0) {
				ePanDesc.setRelayDevice(true);
			}
			else {
				ePanDesc.setRelayDevice(false);
			}
			index++;
			if (aryParam[index].compareTo("1") == 0) {
				ePanDesc.setRelayEndPoint(true);
			}
			else {
				ePanDesc.setRelayEndPoint(false);
			}

			return true;
		}
		catch (Exception e) {
			return false;
		}

	}

}
