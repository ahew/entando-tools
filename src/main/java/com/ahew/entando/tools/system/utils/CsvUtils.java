package com.ahew.entando.tools.system.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringEscapeUtils;

public class CsvUtils {

	public static String createCsvRecord(String... params) {
		StringBuilder sb = new StringBuilder();
		if (params != null && params.length > 0) {
			sb.append(escapeCsvValue(params[0]));
			for (int i = 1; i < params.length; i++) {
				sb.append(",").append(escapeCsvValue(params[i]));
			}
		}
		return sb.toString();
	}
	
	public static String escapeCsvValue(String value) {
		// TODO Verificare qui
		value = StringEscapeUtils.escapeCsv(value);
		return value;
	}
	
	public static List<List<String>> parseText(String csvText) throws Exception {
		List<List<String>> lines = new ArrayList<List<String>>();
		CSVParser parser = CSVParser.parse(csvText, CSVFormat.INFORMIX_UNLOAD_CSV);
		List<CSVRecord> records = parser.getRecords();
		for (CSVRecord record : records) {
			List<String> line = new ArrayList<>();
			for (int i=0; i<record.size(); i++) {
				line.add(record.get(i));
			}
			lines.add(line);
		}
		return lines;
	}
	
	public static List<String> parseLine(String csvLine) throws Exception {
//		return parseLine(csvLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
		List<String> result = new ArrayList<>();
		CSVParser parser = CSVParser.parse(csvLine, CSVFormat.INFORMIX_UNLOAD_CSV);
		List<CSVRecord> records = parser.getRecords();
		if (records.size()==1) {
			CSVRecord record = records.get(0);
			for (int i=0; i<record.size(); i++) {
				result.add(record.get(i));
			}
		} else if (records.size()>1) {
			throw new Exception("Too much records");
		}
		return result;
	}
	
	public static List<String> parseLine(String csvLine, char separators) {
		return parseLine(csvLine, separators, DEFAULT_QUOTE);
	}
	
	public static List<String> parseLine(String csvLine, char separators, char customQuote) {
		List<String> result = new ArrayList<>();
		// if empty, return!
		if (csvLine == null || csvLine.isEmpty()) {
			return result;
		}
		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}
		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}
		
		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;
		char[] chars = csvLine.toCharArray();
		for (char ch : chars) {
			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {
					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}
				}
			} else {
				if (ch == customQuote) {
					inQuotes = true;
					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}
					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}
				} else if (ch == separators) {
					result.add(purgedValue(curVal.toString()));
					curVal = new StringBuffer();
					startCollectChar = false;
				} else if (ch == '\r') {
					// ignore LF characters
					continue;
				} else if (ch == '\n') {
					// the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}
		}
		result.add(purgedValue(curVal.toString()));
		return result;
	}
	
	private static String purgedValue(String value) {
		if (value!=null && value.length()>1 && value.charAt(0)==DEFAULT_QUOTE && value.charAt(value.length()-1)==DEFAULT_QUOTE) {
			value = value.substring(1, value.length()-1);
		}
		return value;
	}
	
	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';
	
}
