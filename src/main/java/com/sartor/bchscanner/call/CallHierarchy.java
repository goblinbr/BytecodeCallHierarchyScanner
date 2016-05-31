package com.sartor.bchscanner.call;

import java.util.ArrayList;
import java.util.List;

public class CallHierarchy implements Comparable<CallHierarchy> {
	
	private String className;
	private String methodName;
	private String methodDesc;
	
	private CallHierarchy callee;
	private List<CallHierarchy> callers;

	public CallHierarchy(CallHierarchy callee, String className, String methodName, String methodDesc) {
		this.callee = callee;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
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
	
	public String getClassName() {
		return className;
	}
	
	public String getMethodDesc() {
		return methodDesc;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	@Override
	public int hashCode() {
		return (className + methodName + methodDesc).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == CallHierarchy.class && obj.hashCode() == this.hashCode();
	}

	public int compareTo(CallHierarchy o) {
		int comp = this.className.compareTo(o.className);
		if( comp == 0 ){
			comp = this.methodName.compareTo(o.methodName);
			if( comp == 0 ){
				comp = this.methodDesc.compareTo(o.methodDesc);
			}
		}
		return comp;
	}
	
	@Override
	public String toString() {
		String spaces = "";
		CallHierarchy c = this.callee;
		while( c != null ){
			spaces += " ";
			c = c.getCallee();
		}
		return spaces + this.className + ( ( this.methodName.equals("") ) ? "" : "." + this.methodName + this.methodDesc );
	}

	public boolean containsCall(String className, String methodName, String methodDesc) {
		boolean contains = false;
		if( this.className.equals(className) && this.methodName.equals(methodName) && this.methodDesc.equals(methodDesc) ){
			contains = true;
		}
		else if( this.callee != null ){
			contains = callee.containsCall(className, methodName, methodDesc);
		}
		return contains;
	} 
}
