package com.sartor.bchscanner.call;

import java.util.ArrayList;
import java.util.List;

public class CallHierarchy implements Comparable<CallHierarchy> {
	
	private JavaMethod method;
	
	private CallHierarchy callee;
	private List<CallHierarchy> callers;

	public CallHierarchy(CallHierarchy callee, String className, String methodName, String methodDesc) {
		this.callee = callee;
		this.method = new JavaMethod(className, methodName, methodDesc);
		this.callers = new ArrayList<CallHierarchy>();
	}
	
	public void addCaller(CallHierarchy caller) {
		this.callers.add(caller);
	}
	
	public CallHierarchy getCallee() {
		return callee;
	}
	
	public List<CallHierarchy> getCallers() {
		return callers;
	}
	
	public JavaMethod getMethod() {
		return method;
	}
	
	@Override
	public int hashCode() {
		return this.method.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == CallHierarchy.class && obj.hashCode() == this.hashCode();
	}

	public int compareTo(CallHierarchy o) {
		return this.method.compareTo(o.method);
	}
	
	@Override
	public String toString() {
		String spaces = "";
		CallHierarchy c = this.callee;
		while( c != null ){
			spaces += " ";
			c = c.getCallee();
		}
		return spaces + this.method.toString();
	}

	public boolean containsCall(JavaMethod method) {
		boolean contains = this.method.equals(method);
		if( !contains && callee != null ){
			contains = callee.containsCall(method);
		}
		return contains;
	} 
}
