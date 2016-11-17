package com.ahew.entando.tools.system.utils.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.ahew.entando.tools.system.dao.common.DBUtils;
import com.ahew.entando.tools.system.exception.ApsSystemException;
import com.ahew.entando.tools.system.model.i18n.I18nLabel;
import com.ahew.entando.tools.system.model.i18n.I18nRecord;
import com.ahew.entando.tools.system.utils.CsvUtils;
import com.ahew.entando.tools.system.utils.FileUtils;
import com.ahew.entando.tools.system.utils.model.ObjComparison;

public class I18nHelper {
	
	public void printComparison(ObjComparison<I18nLabel> comparison, Appendable out) {
		try {
			String lineTerminator = ";";
			comparison.getDeleted().stream().forEach(label -> {
				this.appendDeleteQuery(label.getKeyCode(), lineTerminator, out);
			});
			comparison.getModified().stream().forEach(label -> {
				this.appendDeleteInsertQueries(label, lineTerminator, out);
			});
			comparison.getAdded().stream().forEach(label -> {
				this.appendDeleteInsertQueries(label, lineTerminator, out);
			});
		} catch (Exception e) {
			throw new RuntimeException("Error building label queries", e);
		}
	}
	
	public void printComparison(ObjComparison<I18nRecord> comparison, Appendable out, Predicate<I18nRecord> filter) {
		try {
			String lineTerminator = ";";
			out.append("---- INIZIO - Cancellate ----\n");
			this.filterAndExecute(comparison.getDeleted().stream(), record -> {
				this.appendDeleteQuery(record, lineTerminator, out);
			}, filter);
			out.append("---- FINE   - Cancellate ----\n");
			out.append("---- INIZIO - Modificate ----\n");
			this.filterAndExecute(comparison.getModified().stream(), record -> {
				this.appendDeleteQuery(record, lineTerminator, out);
				this.appendInsertQuery(record, lineTerminator, out);
			}, filter);
			out.append("---- FINE   - Modificate ----\n");
			out.append("---- INIZIO - Aggiunte   ----\n");
			this.filterAndExecute(comparison.getAdded().stream(), record -> {
				this.appendDeleteQuery(record, lineTerminator, out);
				this.appendInsertQuery(record, lineTerminator, out);
			}, filter);
			out.append("---- FINE   - Aggiunte   ----\n");
		} catch (Exception e) {
			throw new RuntimeException("Error building label queries", e);
		}
	}
	
	private <T> void filterAndExecute(Stream<T> stream, Consumer<T> consumer, Predicate<T> filter) {
		if (filter!=null) {
			stream = stream.filter(filter);
		}
		stream.forEach(consumer);
	}
	
	public ObjComparison<I18nLabel> compareLabels(Map<String, I18nLabel> originary, Map<String, I18nLabel> modified) {
		ObjComparison<I18nLabel> comparison = new ObjComparison<I18nLabel>();
		List<String> modifiedKeys = new ArrayList<String>(modified.keySet());
		originary.entrySet().stream().forEach(entry -> {
			String key = entry.getKey();
			I18nLabel value = entry.getValue();
			if (modifiedKeys.remove(key)) {
				I18nLabel modifiedLabel = modified.get(key);
				if (!value.equals(modifiedLabel)) {
					comparison.addModified(modifiedLabel);
				} else {
					comparison.addUnchanged(modifiedLabel);
				}
			} else {
				comparison.addDeleted(value);
			}
		});
		modifiedKeys.stream().forEach(key -> {
			I18nLabel addedLabel = modified.get(key);
			comparison.addAdded(addedLabel);
		});
		return comparison;
	}
	
	public ObjComparison<I18nRecord> compareLabelRecords(Map<String, I18nLabel> originary, Map<String, I18nLabel> modified) {
		ObjComparison<I18nRecord> comparison = new ObjComparison<I18nRecord>();
		List<String> modifiedKeys = new ArrayList<String>(modified.keySet());
		for (Entry<String, I18nLabel> entry : originary.entrySet()) {
			String key = entry.getKey();
			I18nLabel originalLabel = entry.getValue();
			if (modifiedKeys.remove(key)) {// Trovato
				I18nLabel modifiedLabel = modified.get(key);
				Set<String> foundLangs = new HashSet<String>();
				manageLangEntries(key, modifiedLabel, null, record -> {
					String langCode = record.getLangCode();
					String langValue = record.getValue();
					String originalValue = originalLabel.getValue(langCode);
					foundLangs.add(langCode);
					if (originalValue==null) {
						comparison.addAdded(record);
					} else if (!langValue.equals(originalValue)) {
						comparison.addModified(record);
					} else {
						comparison.addUnchanged(record);
					}
				});
				manageLangEntries(key, originalLabel, 
						originalEntry -> !foundLangs.contains(originalEntry.getKey()), 
						record -> comparison.addDeleted(record)
				);
			} else {
				manageLangEntries(key, originalLabel, null, record -> comparison.addDeleted(record));
			}
		}
		for (String key : modifiedKeys) {
			I18nLabel label = modified.get(key);
			manageLangEntries(key, label, null, record -> comparison.addAdded(record));
		};
		return comparison;
	}
	
	private void manageLangEntries(String key, I18nLabel label, Predicate<Entry<String, String>> filter, Consumer<I18nRecord> consumer) {
		this.filterAndExecute(label.getValues().entrySet().stream(), 
				langEntry -> consumer.accept(new I18nRecord(key, langEntry.getKey(), langEntry.getValue())), 
				filter
		);
	}
	
	public void saveLabelsToXls(Collection<I18nLabel> labels, String filePath) throws ApsSystemException {
		// TODO
	}
	
	public void saveLabelsToCsv(Collection<I18nLabel> labels, String filePath) throws Exception {
		this.saveLabelsToCsv(labels, new File(filePath));
	}
	
	public void saveLabelsToCsv(Collection<I18nLabel> labels, File file) throws Exception {
		FileUtils.writeToFile(file, out -> this.saveLabelsToCsv(labels, out));
	}
	
	public void saveLabelsToCsv(Collection<I18nLabel> labels, Appendable out) {
		this.saveLabelsToConsumer(labels, out, label -> this.createCsvRecords(label));
	}
	
	public void saveLabelsToQuery(Collection<I18nLabel> labels, String filePath) throws Exception {
		this.saveLabelsToQuery(labels, new File(filePath));
	}
	
	public void saveLabelsToQuery(Collection<I18nLabel> labels, File file) throws Exception {
		FileUtils.writeToFile(file, out -> this.saveLabelsToQuery(labels, out));
	}
	
	public void saveLabelsToQuery(Collection<I18nLabel> labels, Appendable out) {
		this.saveLabelsToConsumer(labels, out, label -> this.createQuery(label, ";"));
	}
	
	public Map<String, I18nLabel> extractLabelsFromCsv(String filePath) throws Exception {
		return this.extractLabelsFromCsv(new File(filePath));
	}
	
	public Map<String, I18nLabel> extractLabelsFromCsv(File file) throws Exception {
		Map<String, I18nLabel> labels = new TreeMap<String, I18nLabel>();
		FileUtils.readFromFile(file, reader -> this.extractLabelsFromCsv(reader, labels));
		return labels;
	}
	
	public Map<String, I18nLabel> extractLabelsFromCsv(BufferedReader reader) {
		Map<String, I18nLabel> labels = new TreeMap<String, I18nLabel>();
		this.extractLabelsFromCsv(reader, labels);
		return labels;
	}
	
	public Map<String, I18nLabel> extractLabelsFromCsv(BufferedReader reader, Map<String, I18nLabel> labels) {
		try {
			String currentLine = null;
			I18nLabel lastLabel = null;
			StringBuilder csvText = new StringBuilder();
			while ((currentLine = reader.readLine()) != null) {
				csvText.append(currentLine).append("\n");
			}
			List<List<String>> lines = CsvUtils.parseText(csvText.toString());
			for (List<String> params : lines) {
				if (params!=null && !params.isEmpty()) {
					if (params.size() != 3) {
						throw new Exception("Malformed File");
					}
					String keyCode = params.get(0);
					String langCode = params.get(1);
					String value = params.get(2);
					if (lastLabel==null || !lastLabel.getKeyCode().equals(keyCode)) {
						lastLabel = new I18nLabel();
						lastLabel.setKeyCode(keyCode);
						labels.put(keyCode, lastLabel);
					}
					lastLabel.addValue(langCode, value);
				}
			}
//			while ((currentLine = reader.readLine()) != null) {
//				List<String> params = CsvUtils.parseLine(currentLine);
//				if (params!=null && !params.isEmpty()) {
//					if (params.size() != 3) {
//						throw new Exception("Malformed File");
//					}
//					String keyCode = params.get(0);
//					String langCode = params.get(1);
//					String value = params.get(2);
//					if (lastLabel==null || !lastLabel.getKeyCode().equals(keyCode)) {
//						lastLabel = new I18nLabel();
//						lastLabel.setKeyCode(keyCode);
//						labels.put(keyCode, lastLabel);
//					}
//					lastLabel.addValue(langCode, value);
//				}
//			}
			return labels;
		} catch (Exception e) {
			throw new RuntimeException("Error building label queries", e);
		}
	}
	
	private void saveLabelsToConsumer(Collection<I18nLabel> labels, Appendable out, Function<I18nLabel, String> function) {
		labels.stream().forEach(label -> {
			try {
				String query = function.apply(label);
				out.append(query);
			} catch (Exception e) {
				throw new RuntimeException("Error building label queries", e);
			}
		});
	}
	
	private String createCsvRecords(I18nLabel label) {
		StringBuilder out = new StringBuilder();
		String keyCode = label.getKeyCode();
		label.getValues().entrySet().stream().forEach(entry -> {
			String line = CsvUtils.createCsvRecord(keyCode, entry.getKey(), entry.getValue());
			out.append(line);
			out.append("\n");
		});
		return out.toString();
	}
	
	private String createQuery(I18nLabel label, String lineTerminator) {
		Appendable out = new StringBuilder();
		this.appendDeleteInsertQueries(label, lineTerminator, out);
		return out.toString();
	}
	
	private void appendLineTerminator(Appendable sb, String lineTerminator) throws Exception {
		if (lineTerminator!=null) {
			sb.append(lineTerminator);
		}
		sb.append("\n");
	}
	
	public void appendDeleteInsertQueries(I18nLabel label, String lineTerminator, Appendable out) {
		this.appendDeleteQuery(label.getKeyCode(), lineTerminator, out);
		this.appendInsertQueries(label, lineTerminator, out);
	}
	
	public void appendInsertQueries(I18nLabel label, String lineTerminator, Appendable out) {
		String keyCode = label.getKeyCode();
		for (Entry<String, String> entry : label.getValues().entrySet()) {
			this.appendInsertQuery(keyCode, entry.getKey(), entry.getValue(), lineTerminator, out);
		}
	}
	
	public void appendDeleteQuery(String keyCode, String lineTerminator, Appendable out) {
		try {
			out.append("DELETE FROM localstrings WHERE keycode = '").append(DBUtils.escapeStringValue(keyCode)).append("'");
			this.appendLineTerminator(out, lineTerminator);
		} catch (Exception e) {
			throw new RuntimeException("Error appending DELETE query", e);
		}
	}
	
	public void appendDeleteQuery(I18nRecord record, String lineTerminator, Appendable out) {
		try {
			out.append("DELETE FROM localstrings WHERE keycode = '").append(DBUtils.escapeStringValue(record.getKeyCode()))
				.append("' AND langcode = '").append(DBUtils.escapeStringValue(record.getLangCode())).append("'");
			this.appendLineTerminator(out, lineTerminator);
		} catch (Exception e) {
			throw new RuntimeException("Error appending DELETE query for Lang", e);
		}
	}
	
	public void appendInsertQuery(I18nRecord record, String lineTerminator, Appendable out) {
		this.appendInsertQuery(record.getKeyCode(), record.getLangCode(), record.getValue(), lineTerminator, out);
	}
	
	public void appendInsertQuery(String keyCode, String langCode, String value, String lineTerminator, Appendable out) {
		try {
			out.append("INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('")
				.append(DBUtils.escapeStringValue(keyCode)).append("', '")
				.append(DBUtils.escapeStringValue(langCode)).append("', '")
				.append(DBUtils.escapeStringValue(value)).append("')");
			this.appendLineTerminator(out, lineTerminator);
		} catch (Exception e) {
			throw new RuntimeException("Error appending INSERT query", e);
		}
	}
	
}
