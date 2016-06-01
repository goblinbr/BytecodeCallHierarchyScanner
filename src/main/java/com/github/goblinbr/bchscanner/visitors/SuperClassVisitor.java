package com.github.goblinbr.bchscanner.visitors;

import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class SuperClassVisitor extends ClassVisitor {

	private Set<String> classNames;
	private String classNameToSearch;

	public SuperClassVisitor( String classNameToSearch ) {
		super(Opcodes.ASM5);
		this.classNameToSearch = classNameToSearch;
		this.classNames = new TreeSet<String>();
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if( this.classNames.contains(superName) || this.classNameToSearch.equals(name) ){
			this.classNames.add(name);
		}
		else {
			for( String interfaceName : interfaces ){
				if( this.classNames.contains(interfaceName) ){
					this.classNames.add(name);
					break;
				}
			}
		}
	}

	public Set<String> getClassNames() {
		return classNames;
	}
}