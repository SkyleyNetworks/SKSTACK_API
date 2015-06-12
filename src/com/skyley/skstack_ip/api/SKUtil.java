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

package com.skyley.skstack_ip.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* 共通処理をまとめたユーティリティークラス
* @author Skyley Networks, Inc.
* @version 0.1
*/
public class SKUtil {

	/**
	 * 指定した時間だけスリープ
	 * @param interval スリープ時間（単位 ミリ秒）
	 */
	public static void pause(long interval) {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 指定した文字列が正しいIPv6アドレスの書式かチェック（連続する0の省略形は未対応）
	 * @param value 対象文字列
	 * @return 正しい書式のとき:true, 不正な書式のとき:false
	 */
	public static boolean isValidIP6Address(String value) {
		if (value == null) {
			return false;
		}
		else if (value == "") {
			return false;
		}

		String regex = "^[0-9a-fA-F]{32}$";
		String regex2 = "^[0-9a-fA-F]{4}:[0-9a-fA-F]{4}:[0-9a-fA-F]{4}:[0-9a-fA-F]{4}:"
						+ "[0-9a-fA-F]{4}:[0-9a-fA-F]{4}:[0-9a-fA-F]{4}:[0-9a-fA-F]{4}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);

		if (m.find()) {
			return true;
		}
		else {
			Pattern p2 = Pattern.compile(regex2);
			Matcher m2 = p2.matcher(value);
			if (m2.find()) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	/**
	 * IPv6アドレスの書式をコロン付、A-Fを大文字にする
	 * @param value IPv6アドレスの文字列表現
	 * @return コロン付、A-Fを大文字にしたIPv6アドレス文字列、不正な書式の場合は""
	 */
	public static String toFormattedIP6Address(String value) {
		if (!isValidIP6Address(value)) {
			return "";
		}

		String address = value.toUpperCase();

		if (address.indexOf(":") == -1) {
			StringBuilder sb = new StringBuilder();
			int beginIndex = 0;
			int endIndex = 4;

			for (int i = 0; i < 7; i++) {
				sb.append(address.substring(beginIndex, endIndex));
				sb.append(":");
				beginIndex += 4;
				endIndex += 4;
			}

			sb.append(address.substring(28, 32));
			return sb.toString();
		}
		else {
			return address;
		}
	}

	/**
	 * 指定した文字列が64bitアドレスの16進表現かチェック
	 * @param value 対象文字列
	 * @return valueが64bitアドレスの文字列表現であるとき:true, そうでないとき:false
	 */
	public static boolean isValidLongAddress(String value) {
		String regex = "^[0-9a-fA-F]{16}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}

	/**
	 * 指定した文字列が指定した長さの16進表現かチェック
	 * @param value 対象文字列
	 * @param len 長さ
	 * @return valueが長さlenの16進表現のとき：true, そうでないとき:false
	 */
	public static boolean isValidHexString(String value, int len) {
		String regex = "^[0-9a-fA-F]{" + Integer.toString(len) +"}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}

	/**
	 * 指定した文字列が指定した長さのASCII文字列かチェック
	 * @param value 対象文字列
	 * @param len 長さ
	 * @return valueが長さlenのASCII文字列のとき:true, そうでないとき:false
	 */
	public static boolean isValidAsciiString(String value, int len) {
		String regex = "^[\\x20-\\x7E]{" + Integer.toString(len) +"}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}

	/**
	 * 指定した値を指定した桁数の16進表現文字列として返す<br>
	 * 例）toPaddingHexString(1, 4) = "0001"
	 * @param value 対象の値
	 * @param len 16進桁数
	 * @return valueを桁数lenの16進表現とした文字列
	 */
	public static String toPaddingHexString(int value, int len) {
		String hexString = Integer.toHexString(value);

		if (hexString.length() >= len) {
			return hexString;
		}

		int num = len - hexString.length();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < num; i++) {
			sb.append("0");
		}

		sb.append(hexString);
		return sb.toString().toUpperCase();
	}

	/**
	 * 指定したASCIIコード16進文字列からASCII文字列を取得<br>
	 * 例）toAsciiString("534B535441434B2D4950") = "SKSTACK-IP"
	 * @param value ASCIIコード16進文字列
	 * @return valueに対応するASCII文字列
	 */
	public static String toAsciiString(String value) {
		int i, j;
		int len = value.length();

		if ((len % 2) != 0) {
			return value;
		}

		try {
			byte[] byteValue = new byte[len/2];
			j = 0;
			for (i = 0; i < byteValue.length; i++) {
				byteValue[i] = Byte.parseByte(value.substring(j, j+2), 16);
				j += 2;
			}

			return new String(byteValue, "US-ASCII");
		}
		catch (Exception e) {
			return value;
		}
	}

	/**
	 * バイト列(byte[])を16進表現文字列に変換
	 * @param value バイト列
	 * @return 16進表現文字列
	 */
	public static String toHexString(byte[] value) {
		if(value == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for(byte b : value) {
			sb.append(String.format("%02X", b));
		}

		return new String(sb);
	}

	/**
	 * 16進表現文字列をバイト列(byte[])に変換
	 * @param value 16進表現文字列
	 * @return バイト列(byte[])
	 */
	public static byte[] toByteArray(String value) {
		int i, j;
		int len;

		if (value == null) {
			return null;
		}
		else if (value == "") {
			return null;
		}

		len = value.length();
		if ((len % 2) != 0) {
			return null;
		}

		try {
			byte[] byteValue = new byte[len/2];
			j = 0;
			for (i = 0; i < byteValue.length; i++) {
				byteValue[i] = (byte) Integer.parseInt(value.substring(j, j+2), 16);
				j += 2;
			}

			return byteValue;
		}
		catch (Exception e) {
			return null;
		}
	}
}
