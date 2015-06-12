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
* コマンド送信に対するエラーコードを列挙
* @author Skyley Networks, Inc.
* @version 0.1
*/
public enum SKErrorCode {
	/** コマンド送信完了 */
	OK("OK"),
	/** 指定されたコマンドは未対応 */
	COMMAND_NAME_ERROR("FAIL ER04"),
	/** 指定されたコマンドの引数の数が合っていない */
	NUM_OF_ARGS_ERROR("FAIL ER05"),
	/** 指定されたコマンドの引数の値域が正しくない */
	OUT_OF_RANGE_ERROR("FAIL ER06"),
	/** UART入力エラーが発生 */
	UART_ERROR("FAIL ER09"),
	/** 指定されたコマンドは受け付けたが、実行結果が失敗 */
	EXEC_ERROR("FAIL ER10");

	/** エラーコードの文字列（{CRLF}は除く） */
	private String rawString;

	/**
	 * コンストラクタ
	 * @param rawString エラーコードの文字列（{CRLF}は除く）
	 */
	private SKErrorCode(String rawString) {
		this.rawString = rawString;
	}

	/**
	 * エラーコードの文字列を返す
	 */
	public String toString() {
		return rawString;
	}
}
