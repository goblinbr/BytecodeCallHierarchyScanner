package com.sartor.bchscanner.call;

public class Caller implements Comparable<Caller> {
	private String className;
	private String methodName;
	private String methodDesc;

	public Caller(String className, String methodName, String methodDesc) {
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
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
		return obj != null && obj.getClass() == Caller.class && obj.hashCode() == this.hashCode();
	}

	public int compareTo(Caller o) {
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
		return this.className + "." + this.methodName + this.methodDesc;
	}
}
