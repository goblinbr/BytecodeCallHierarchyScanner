package com.sartor.bchscanner;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.Method;

import com.sartor.bchscanner.call.CallHierarchy;
import com.sartor.bchscanner.call.Caller;
import com.sartor.bchscanner.visitors.CallerClassVisitor;
import com.sartor.bchscanner.visitors.SuperClassVisitor;

/**
 * A class that scans jar files and finds calls to a method
 * 
 * @author Rodrigo de Bona Sartor
 */
public class JavaScanner implements Closeable {
	
	private JarFile jarFile;
	
	/**
	 * Create a new JavaScanner with the JarFile containing at <b>jarPath</b>
	 * @param jarPath OS path to the jar file that contains the class
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if access to the file is denied
     *         by the SecurityManager
	 */
	public JavaScanner(String jarPath) throws IOException {
		this.jarFile = new JarFile(jarPath);
	}
	
	public Set<Caller> findCallersOfAnyMethod( String className ) throws IOException {
		return findCallersOfMethod( className, null );
	}
	
	public Set<Caller> findCallersOfMethod( String className, String methodSignature ) throws IOException {
		String methodName = "";
		String methodDesc = "";
		if( methodSignature != null ){
			Method method = Method.getMethod(methodSignature);
			methodName = method.getName();
			methodDesc = method.getDescriptor();
		}
		
		return findCallersOfMethod( className, methodName, methodDesc );
	}
	
	private Set<Caller> findCallersOfMethod(String className, String methodName, String methodDesc) throws IOException {
		Set<String> classNames = findAllClassesThatExtendsOrImplements(className);
		
		CallerClassVisitor callerClassVisitor = new CallerClassVisitor( classNames, methodName, methodDesc );
		Enumeration<JarEntry> entries = this.jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			
			if (entry.getName().endsWith(".class")) {
				InputStream stream = new BufferedInputStream(this.jarFile.getInputStream(entry), 1024);
				ClassReader reader = new ClassReader(stream);

				reader.accept(callerClassVisitor, 0);

				stream.close();
			}
		}		
		return callerClassVisitor.getCallers();
	}

	public CallHierarchy findCallHierarchy( String className, String methodSignature ) throws IOException {
		String methodName = "";
		String methodDesc = "";
		if( methodSignature != null ){
			Method method = Method.getMethod(methodSignature);
			methodName = method.getName();
			methodDesc = method.getDescriptor();
		}
		
		CallHierarchy callHierarchy = new CallHierarchy(null, className, methodName, methodDesc);
		
		findCallHierarchy( callHierarchy );
		
		return callHierarchy;
	}
	
	private void findCallHierarchy(CallHierarchy callHierarchy) throws IOException {
		Set<Caller> callers = findCallersOfMethod( callHierarchy.getClassName(), callHierarchy.getMethodName(), callHierarchy.getMethodDesc() );
		for( Caller caller : callers ){
			if( !callHierarchy.containsCall( caller.getClassName(), caller.getMethodName(), caller.getMethodDesc() ) ){
				
				CallHierarchy callHierarchyCaller = new CallHierarchy(callHierarchy, caller.getClassName(), caller.getMethodName(), caller.getMethodDesc() );
				callHierarchy.addCaller( callHierarchyCaller );
			}
		}
		
		for( CallHierarchy caller : callHierarchy.getCallers() ){
			findCallHierarchy(caller);
		}
	}
	
	public CallHierarchy findCallHierarchyOfAnyMethod(String className) throws IOException {
		return findCallHierarchy(className, null);
	}
	
	public boolean extendsOrImplements( String className, String extendsImplementsName ) throws IOException{
		boolean ext = className.equals(extendsImplementsName);
		if( !ext ){
			Set<String> extendsList = findAllClassesThatExtendsOrImplements( extendsImplementsName );
			ext = extendsList.contains(className);
		}
		return ext;
	}
	
	public Set<String> findAllClassesThatExtendsOrImplements( String className ) throws IOException{
		SuperClassVisitor superClassVisitor = new SuperClassVisitor( className );
		int qtyClass;
		do{
			qtyClass = superClassVisitor.getClassNames().size();
			
			Enumeration<JarEntry> entries = this.jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				
				if (entry.getName().endsWith(".class")) {
					InputStream stream = new BufferedInputStream(this.jarFile.getInputStream(entry), 1024);
					ClassReader reader = new ClassReader(stream);

					reader.accept(superClassVisitor, 0);

					stream.close();
				}
			}
		} while( qtyClass != superClassVisitor.getClassNames().size() );
		
		return superClassVisitor.getClassNames();
	}

	public void close() throws IOException {
		if( this.jarFile != null ){
			this.jarFile.close();
		}
	}

	
}
