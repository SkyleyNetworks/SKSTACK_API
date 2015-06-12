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

import java.io.OutputStream;

import com.skyley.skstack_ip.api.SKUtil;
import com.skyley.skstack_ip.api.skenums.SKSecOption;

/**
* SKSENDTOコマンドに対応したクラス、SKCommandを継承
* @author Skyley Networks, Inc.
* @version 0.1
*/
public class SKSendTo extends SKCommand {
	/** UDPハンドル */
	private byte handle;
	/** 宛先IPv6アドレス */
	private String ip6Address;
	/** 宛先ポート番号 */
	private int port;
	/** 暗号化オプション */
	private SKSecOption sec;
	/** 送信データ */
	private byte[] data;

	/**
	 * コンストラクタ
	 * @param handle 送信元UDPハンドル
	 * @param ip6Address 宛先IPv6アドレス
	 * @param port 宛先ポート番号
	 * @param sec 暗号化オプション
	 * @param data 送信データ
	 */
	public SKSendTo(byte handle, String ip6Address, int port, SKSecOption sec, byte[] data) {
		this.handle = handle;
		this.ip6Address = ip6Address;
		this.port = port;
		this.sec = sec;
		this.data = data;
	}

	/**
	 * 引数チェック
	 */
	@Override
	public boolean checkArgs() {
		// TODO 自動生成されたメソッド・スタブ
		if (!SKUtil.isValidIP6Address(ip6Address)) {
			return false;
		}

		if (handle < 1 || handle > 6) {
			return false;
		}

		if (port < 0 || port > 0xFFFF) {
			return false;
		}

		if (data == null) {
			return false;
		}

		return true;
	}

	/**
	 * コマンド文字列組み立て
	 */
	@Override
	public void buildCommand() {
		// TODO 自動生成されたメソッド・スタブ
		String portString, lenString;

		portString = SKUtil.toPaddingHexString(port, 4);
		lenString = SKUtil.toPaddingHexString(data.length, 4);

		StringBuilder sb = new StringBuilder();
		sb.append("SKSENDTO ");
		sb.append(Integer.toHexString(handle));
		sb.append(" ");
		sb.append(ip6Address);
		sb.append(" ");
		sb.append(portString);
		sb.append(" ");
		sb.append(sec.toString());
		sb.append(" ");
		sb.append(lenString);
		sb.append(" ");

		//sb.append(data);
		//sb.append("\r\n");
		commandString = sb.toString();
	}

 	public boolean sendCommand(OutputStream out) {
		try {
			byte[] commandByte = commandString.getBytes("US-ASCII");
			out.write(commandByte);
			out.write(data);

			commandString = commandString + SKUtil.toHexString(data) + "\r\n";
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
