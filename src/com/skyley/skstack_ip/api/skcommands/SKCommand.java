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

import java.io.OutputStream;

/**
 * SKSTACK-IP API<br>
 * SKCommandクラス<br>
 * SKコマンド送信を扱う抽象クラス
 * @author Skyley Networks, Inc.
 * @version 0.1
 *
*/
public abstract class SKCommand {
	/** コマンド文字列 */
	protected String commandString;

	/**
	 * コマンド文字列を取得
	 * @return コマンド文字列
	 */
	public String getCommandString() {
		return commandString;
	}

	/**
	 * 引数チェック<br>
	 * 具体的な処理は具象クラスが実装
	 * @return 引数が値域の範囲内:true, 範囲外:false
	 */
	public abstract boolean checkArgs();

	/**
	 * コマンド文字列を組立<br>
	 * 具体的な処理は具象クラスが実装
	 */
	public abstract void buildCommand();

	/**
	 * コマンドを送信
	 * @param out デバイス接続先を示す出力ポート
	 * @return 送信に成功:true, 失敗:false
	 */
	public boolean sendCommand(OutputStream out) {
		try {
			byte[] commandByte = commandString.getBytes("US-ASCII");
			out.write(commandByte);
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * コマンド送信のテンプレートメソッド
	 * @param out デバイス接続先を示す出力ポート
	 * @return 送信に成功:true, 失敗:false
	 */
	public boolean issueCommand(OutputStream out) {
		if(!checkArgs())
		{
			return false;
		}

		buildCommand();

		if(!sendCommand(out)) {
			return false;
		}

		return true;
	}
}
