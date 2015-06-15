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
* UDP送信時の暗号化オプションを列挙
* @author Skyley Networks, Inc.
* @version 0.1
*/
public enum SKSecOption {
	/** 平文で送信 */
	PLAIN("0"),
	/** 送信先がセキュリティ有効で登録されていれば暗号化して送信、そうでなければ送信しない */
	SEC_OR_NO_TX("1"),
	/** 送信先がセキュリティ有効で登録されていれば暗号化して送信、そうでなければ平文で送信 */
	SEC_OR_PALIN("2");

	/** 暗号化オプションのraw value */
	private String optionString;

	/**
	 * コンストラクタ
	 * @param optionString 暗号化オプションのraw vallue
	 */
	private SKSecOption(String optionString) {
		this.optionString = optionString;
	}

	/**
	 * 暗号化オプションのraw stirngを取得
	 */
	public String toString() {
		return optionString;
	}
}
