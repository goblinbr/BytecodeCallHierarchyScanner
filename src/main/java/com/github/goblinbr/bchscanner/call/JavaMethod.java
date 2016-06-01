package com.github.goblinbr.bchscanner.call;

public class JavaMethod implements Comparable<JavaMethod> {
	
	private String className;
	private String methodName;
	private String methodDesc;

	public JavaMethod(String className, String methodName, String methodDesc) {
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
	
	public String getSimpleClassName() {
		String simpleName = this.className;
		int index = this.className.lastIndexOf("/");
		if( index >= 0 ){
			simpleName = this.className.substring( index + 1 );
		}
		return simpleName;
	}
	
	public String getPackageName() {
		String packageName = "";
		int index = this.className.lastIndexOf("/");
		if( index >= 0 ){
			packageName = this.className.substring( 0, index ).replace("/", ".");
		}
		return packageName;
	}
	
	@Override
	public int hashCode() {
		return (className + methodName + methodDesc).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == JavaMethod.class && obj.hashCode() == this.hashCode();
	}

	public int compareTo(JavaMethod o) {
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
		return this.className + ( ( this.methodName.equals("") ) ? "" : "." + this.methodName + this.methodDesc );
	}
}
