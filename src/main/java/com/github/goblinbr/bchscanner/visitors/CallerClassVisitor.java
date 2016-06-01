package com.github.goblinbr.bchscanner.visitors;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.goblinbr.bchscanner.call.JavaMethod;

public class CallerClassVisitor extends ClassVisitor {

	private final ThisMethodVisitor methodVisitor;
	
	public CallerClassVisitor( Collection<String> classesAndSuperClasses, String methodName, String methodDesc ) {
		super(Opcodes.ASM5);
		this.methodVisitor = new ThisMethodVisitor(classesAndSuperClasses, methodName, methodDesc);
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.methodVisitor.setActualClassName(name);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		this.methodVisitor.setActualMethodName(name);
		this.methodVisitor.setActualMethodDesc(desc);
		
		return this.methodVisitor;
	}
	
	public Set<JavaMethod> getCallers() {
		return this.methodVisitor.getCallers();
	}
	
	class ThisMethodVisitor extends MethodVisitor {
		
		private Set<JavaMethod> callers;
		private Collection<String> classesAndSuperClasses;
		private String methodName;
		private String methodDesc;
		
		private String actualClassName;
		private String actualMethodName;
		private String actualMethodDesc;
		
		public ThisMethodVisitor( Collection<String> classesAndSuperClasses, String methodName, String methodDesc ) {
			super(Opcodes.ASM5);
			this.callers = new TreeSet<JavaMethod>();
			this.classesAndSuperClasses = classesAndSuperClasses;
			this.methodName = methodName;
			this.methodDesc = methodDesc;
		}
		
		public void setActualClassName(String actualClassName) {
			this.actualClassName = actualClassName;
		}
		
		public void setActualMethodName(String actualMethodName) {
			this.actualMethodName = actualMethodName;
		}
		
		public void setActualMethodDesc(String actualMethodDesc) {
			this.actualMethodDesc = actualMethodDesc;
		}
		
		public Set<JavaMethod> getCallers() {
			return callers;
		}
		
		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			if ( this.classesAndSuperClasses.contains(owner) && (this.methodName == null || this.methodName.equals("") || name.equals(this.methodName) && desc.equals(this.methodDesc))) {
				JavaMethod caller = new JavaMethod(this.actualClassName, this.actualMethodName, this.actualMethodDesc);
				this.callers.add(caller);
			}
		}
	}
}
