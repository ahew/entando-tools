package com.ahew.entando.tools.system.utils.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ObjComparison<T> {
	
	public List<T> getAdded() {
		return added;
	}
	public void setAdded(List<T> added) {
		this.added = added;
	}
	public void addAdded(T item) {
		this.added.add(item);
	}
	
	public List<T> getModified() {
		return modified;
	}
	public void setModified(List<T> modified) {
		this.modified = modified;
	}
	public void addModified(T item) {
		this.modified.add(item);
	}
	
	public List<T> getDeleted() {
		return deleted;
	}
	public void setDeleted(List<T> deleted) {
		this.deleted = deleted;
	}
	public void addDeleted(T item) {
		this.deleted.add(item);
	}
	
	public List<T> getUnchanged() {
		return unchanged;
	}
	public void setUnchanged(List<T> unchanged) {
		this.unchanged = unchanged;
	}
	public void addUnchanged(T item) {
		this.unchanged.add(item);
	}
	
	private List<T> added = new ArrayList<T>();
	private List<T> modified = new ArrayList<T>();
	private List<T> deleted = new ArrayList<T>();
	private List<T> unchanged = new ArrayList<T>();
	
}
