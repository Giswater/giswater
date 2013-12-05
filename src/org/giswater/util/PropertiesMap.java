/*
 * This file is part of Giswater
 * Copyright (C) 2013PrincesaMonoayaM-2009s Associats
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author:
 *   David Erill <daviderill79@gmail.com>
 */
package org.giswater.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedHashMap;


/**
 * This class is an enhanced version of java.util.Properties. It can take in any
 * map as an input to achieve desired functionality of that map i.e.
 * LinkedHashMap preserves order.
 * 
 * @author hughja01
 */
public class PropertiesMap {

	private static final String KEY_COMMENT_HEADER = "HEADER_6913cjh0-jhgc3-11gda-8cghj6-07002jhg00c9a66";
	private static final String LINE_SEPARATOR = "\n\r";
	private static final String KEY_VALUE_SEPARATOR = "=:";
	private static final String SEPARATOR = "=:\r\n";
	private static final String COMMENT = "#!";
	private static final String HEX_DIGITS = "0123456789ABCDEF";
	private static final String SPECIAL_SAVE_CHARS = "=: \t\r\n\f#!";
	private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',	'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private BufferedReader _reader;
	private LinkedHashMap<String, String> _prop;
	private LinkedHashMap<Object, String> _comments = new LinkedHashMap<Object, String>();

	
	/**
	 * Creates a new instance of PropertiesMap using the provided Map. All
	 * properties will be stored in that map and follows its rules of usage.
	 */
	public PropertiesMap(LinkedHashMap<String, String> map) {
		_prop = map;
	}

	/**
	 * Creates a new instance of PropertiesMap using java.util.Properties as an
	 * internal map
	 */
	public PropertiesMap() {
		// _prop = new Properties();
		_prop = new LinkedHashMap<String, String>();
	}

	/**
	 * Analogous to java.util.Properties.store. Will preserve comments by
	 * default.
	 * 
	 * @param outputStream
	 * @throws java.io.IOException
	 */
	public void store(OutputStream outputStream) throws IOException {
		this.store(outputStream, true);
	}

	/**
	 * Analogous to java.util.Properties. User can choose to retain comments in
	 * file, or not. Comments intersparced in file will only be written if this
	 * class with instantiated with a LinkedHashMap. The header comments do not
	 * have to meet this requirement.
	 * 
	 * @param outputStream
	 * @param preserveComments
	 * @throws java.io.IOException
	 */
	public void store(OutputStream outputStream, boolean preserveComments) throws IOException {
		
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
		Iterator<String> propIt = _prop.keySet().iterator();
		//Iterator<Object> comIt = _comments.keySet().iterator();
		Object header = _comments.get(KEY_COMMENT_HEADER);

		// Write Header.
		if (preserveComments && header != null) {
			out.write(header.toString());
		}

		// Write Properties and intersparced comments.
		while (propIt.hasNext()) {
			Object key = propIt.next();
			Object val = _prop.get(key);
			Object comVal = null;

			// Write comments that are intersparced in file
			if (preserveComments && _prop instanceof LinkedHashMap && _comments.containsKey(key)&& (comVal = _comments.get(key)) != null) {
				// out.write(comVal.toString());
				out.write("\r\n" + comVal.toString());				
			}

			out.write(saveConvert(key.toString(), true));
			out.write("=");
			if (val != null) {
				out.write(saveConvert(val.toString(), false));
			}
			out.write(System.getProperties().getProperty("line.separator", "\r\n"));
		}
		out.flush();

	}

	/*
	 * Converts unicodes to encoded uxxxx and writes out any of the characters
	 * in specialSaveChars with a preceding slash
	 */
	private String saveConvert(String theString, boolean escapeSpace) {
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len * 2);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			switch (aChar) {
			case ' ':
				if (x == 0 || escapeSpace){
					outBuffer.append('\\');
				}
				outBuffer.append(' ');
				break;
			case '\\':
				outBuffer.append('\\');
				outBuffer.append('\\');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			default:
				if ((aChar < 0x0020) || (aChar > 0x007e)) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				} else {
					if (SPECIAL_SAVE_CHARS.indexOf(aChar) != -1)
						outBuffer.append('\\');
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Convert a nibble to a hex character
	 * 
	 * @param nibble
	 *            the nibble to convert.
	 */
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	/**
	 * Loads Properties in the same way java.util.Properties does. Note: In
	 * order to preserve order this class should be instantiated with a
	 * LinkedHashMap
	 * 
	 * @param inputStream
	 * @throws java.io.IOException
	 */
	public void load(InputStream inputStream) throws IOException {
		_reader = new BufferedReader(new InputStreamReader(inputStream));

		String key = KEY_COMMENT_HEADER;
		String comment = processComment();

		if (!comment.equals("")){
			_comments.put(key, comment);
		}

		comment = "";

		while (!isAtEndOfStream()) {
			// we are at the beginning of a line.
			// check whether it is a comment and if it is, preserve it
			comment = processComment();

			skipWhiteSpace();

			if (!isAtEndOfLine()) {
				// this line does not consist only of whitespace. the next word
				// is the key
				key = readQuotedLine(SEPARATOR, false);
				skipWhiteSpace();

				// if the next char is a key-value sep, read it and skip the
				// following spaces
				int nextChar = peek();
				if (nextChar > 0 && KEY_VALUE_SEPARATOR.indexOf((char) nextChar) >= 0) {
					_reader.skip(1);
					skipWhiteSpace();
				} else {
					key = "";
					continue;
				}
				// Read the value
				String value = readQuotedLine(LINE_SEPARATOR, true);
				_prop.put(key, value);

				// Associate Comment with key below it
				// No point in keeping it if it isn't an ordered map
				if (!comment.equals("") && _prop instanceof LinkedHashMap)
					_comments.put(key, comment);
			}

			skipCharacters(LINE_SEPARATOR);
		}

	}

	private String processComment() throws IOException {
		int nextChar = peek();
		StringBuffer sb = new StringBuffer();
		String eol = "";
		while (!isAtEndOfStream()) {
			// we are at the beginning of a line.
			// check whether it is a comment and if it is, skip it
			if (COMMENT.indexOf(((char) nextChar)) >= 0) {
				String comment = readAnyBut(LINE_SEPARATOR);
				eol = readTheseCharacters(LINE_SEPARATOR);
				sb.append(comment + eol);
				skipWhiteSpace();
			} else {
				return sb.toString();
			}
			nextChar = peek();
		}
		return "";
	}

	private boolean isAtEndOfLine() throws IOException {
		int nextChar = peek();
		if (nextChar < 0)
			return true;

		return (LINE_SEPARATOR.indexOf((char) nextChar) >= 0);
	}

	private int peek() throws IOException {
		_reader.mark(1);
		int nextChar = _reader.read();
		_reader.reset();
		return nextChar;
	}

	private boolean isAtEndOfStream() throws IOException {
		int nextChar = peek();
		return (nextChar < 0);
	}

	private String readQuotedLine(String terminators, boolean includeWhiteSpace) throws IOException {
		StringBuffer buf = new StringBuffer();

		while (true) {
			// see what the next char is
			int nextChar = peek();

			// if at end of stream or the char is one of the terminators, stop
			if (nextChar < 0
					|| terminators.indexOf((char) nextChar) >= 0
					|| (!includeWhiteSpace && Character
							.isWhitespace((char) nextChar)))
				break;

			try {
				// read the char (and possibly unquote it)
				char ch = readQuotedChar();
				buf.append(ch);
			} catch (Exception e) {
				// simply ignore -- no character was read
			}
		}

		return buf.toString();
	}

	private char readQuotedChar() throws IOException {
		int nextChar = _reader.read();
		if (nextChar < 0)
			throw new IOException();
		char ch = (char) nextChar;

		// if the char is not the quotation char, simply return it
		if (ch != '\\')
			return ch;

		// the character is a quotation character. unquote it
		nextChar = _reader.read();

		// if at the end of the stream, stop
		if (nextChar < 0)
			throw new IOException();

		ch = (char) nextChar;
		switch (ch) {
		case 'u': // Handle Unicode escapes
			char res = 0;
			for (int i = 0; i < 4; i++) {
				nextChar = _reader.read();
				if (nextChar < 0)
					throw new IllegalArgumentException(
							"Malformed \\uxxxx encoding.");
				char digitChar = (char) nextChar;
				int digit = HEX_DIGITS
						.indexOf(Character.toUpperCase(digitChar));
				if (digit < 0)
					throw new IllegalArgumentException(
							"Malformed \\uxxxx encoding.");
				res = (char) (res * 16 + digit);
			}
			return res;

		case '\r':
			// if the next char is \n, read it and fall through
			nextChar = peek();
			if (nextChar == '\n')
				_reader.read();
		case '\n':
			skipWhiteSpace();
			throw new IOException();

		case 't':
			return '\t';
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		default:
			return ch;
		}
	}

	private String readTheseCharacters(String readMe) throws IOException {
		int iChar;
		StringBuffer sb = new StringBuffer();
		while ((iChar = peek()) != -1) {
			if (readMe.indexOf((char) iChar) >= 0)
				sb.append((char) _reader.read());
			else
				return sb.toString();
		}
		return sb.toString();
	}

	private void skipCharacters(String skipMe) throws IOException {
		int iChar;
		while ((iChar = peek()) != -1) {
			if (skipMe.indexOf((char) iChar) >= 0)
				_reader.skip(1);
			else
				break;
		}
	}

	private void skipWhiteSpace() throws IOException {
		int iChar;
		while ((iChar = peek()) != -1) {
			if (Character.isWhitespace((char) iChar)
					&& LINE_SEPARATOR.indexOf((char) iChar) < 0) {
				_reader.skip(1);
			} else
				break;
		}
	}

	private String readAnyBut(String dontSkipMe) throws IOException {
		int nextChar = peek();
		StringBuffer sb = new StringBuffer();
		while ((nextChar = peek()) != -1) {
			if (dontSkipMe.indexOf((char) nextChar) >= 0)
				return sb.toString();

			int thisChar = _reader.read();
			if (thisChar > 0)
				sb.append((char) thisChar);

		}
		return sb.toString();
	}

	/**
	 * Returns a copy of comments to be used in Properties file.
	 * 
	 * @return new Map of Comments stored internally
	 */
	public LinkedHashMap<Object, String> getPropComments() {
		return new LinkedHashMap<Object, String>(_comments);
	}

	/**
	 * Method to set comments to be used in Properties file. To allow for
	 * comments to be placed above a property in a file the key name must match
	 * the key of the property it is supposed to be placed above. Multilined
	 * comments will have the # character added if one does not exist Objects
	 * value will be in this map will be obtained from the obj.toString()
	 * method. This class must be intantiated with a LinkedHashMap as its
	 * internal map.
	 * 
	 * @param comments
	 */
	public void setPropComments(LinkedHashMap<String, String> comments) {
		
		Iterator<String> it = comments.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			Object obj = comments.get(key);
			String com = (obj != null) ? obj.toString() : "";
			BufferedReader reader = new BufferedReader(new StringReader(com));
			StringBuffer buf = new StringBuffer();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("#") || !line.startsWith("!")) {
						buf.append("#");
					}
					buf.append(line);
					buf.append(System.getProperties().getProperty(
							"line.separator", "\r\n"));
				}
				_comments.put(key, buf.toString());
			} catch (Exception e) {/* Not prone to error */
			}
		}
	}

	/**
	 * 
	 * @return the header stored in the class
	 */
	public String getHeader() {
		Object obj = _comments.get(KEY_COMMENT_HEADER);
		return ((obj != null)) ? obj.toString() : "";
	}

	/**
	 * 
	 * @param str
	 *            String value of the Properties file header. Each item in array
	 *            indicates a line of the header. This does not need to be
	 *            proceded by a '#' or '!' character.
	 */
	public void setHeader(String[] str) {
		StringBuffer header = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			if (!str[i].startsWith("#") || !str[i].startsWith("!")) {
				header.append("#");
			}
			header.append(str[i]);
		}
		_comments.put(KEY_COMMENT_HEADER, header.toString());
	}

	public void put(String key, String value) {
		setProperty(key, value);
	}

	public void setProperty(String key, Object value) {
		if (_prop.containsKey(key)) {
			String aux = value.toString();
			_prop.put(key, aux);
		}

	}

	public String get(String key) {
		return getProperty(key);
	}
	
	public String get(String key, String defaultValue) {
		return getProperty(key, defaultValue);
		
	}

	
	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns null
	 * if the property is not found.
	 * 
	 * @param key
	 *            the property key.
	 * @return the value in this property list with the specified key value.
	 * @see #setProperty
	 * @see #defaults
	 */
	
	public String getProperty(String key) {
		Object oval = _prop.get(key);
		String sval = (oval instanceof String) ? (String) oval : null;
		if (sval != null){
			if (sval.trim().equals("")){
				return "";
			} else{
				return sval.trim();
			}
		}
		else{
			return "";
		}
	}
	

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns the
	 * default value argument if the property is not found.
	 * 
	 * @param key
	 *            the hashtable key.
	 * @param defaultValue
	 *            a default value.
	 * 
	 * @return the value in this property list with the specified key value.
	 * @see #setProperty
	 * @see #defaults
	 */
	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
		return (val == "") ? defaultValue : val;
	}

	/**
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> getProperties() {
		return _prop;
	}


}