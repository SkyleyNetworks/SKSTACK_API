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
 * SKHanDeviceのERXUDP文字列を解析する
 * @author Skyley Networks, Inc.
 * @version 0.1
 */
public class SKERxUdpHanParser implements SKERxUdpParser {

	/**
	 * 文字列を解析し、パラメータを格納
	 */
	@Override
	public boolean parseUdp(String raw, SKERxUdp erxUdp) {
		// TODO 自動生成されたメソッド・スタブ
		if (raw == null) {
			return false;
		}

		try {
			String[] ary = raw.split(" ");
			if (ary.length != 10) {
				return false;
			}

			erxUdp.setSenderIP6Address(ary[1]);
			erxUdp.setDestIP6Address(ary[2]);
			erxUdp.setRPort(Integer.parseInt(ary[3], 16));
			erxUdp.setLPort(Integer.parseInt(ary[4], 16));
			erxUdp.setSenderLLA(ary[5]);
			erxUdp.setRSSI(Short.parseShort(ary[6], 16));
			if (ary[7].compareTo("1") == 0) {
				erxUdp.setSecured(true);
			}
			else {
				erxUdp.setSecured(false);
			}
			erxUdp.setDataLength(Integer.parseInt(ary[8], 16));
			erxUdp.setData(ary[9]);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

}
