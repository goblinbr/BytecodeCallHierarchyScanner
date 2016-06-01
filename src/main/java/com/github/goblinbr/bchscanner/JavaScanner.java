package com.github.goblinbr.bchscanner;

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

import com.github.goblinbr.bchscanner.call.CallHierarchy;
import com.github.goblinbr.bchscanner.call.JavaMethod;
import com.github.goblinbr.bchscanner.visitors.CallerClassVisitor;
import com.github.goblinbr.bchscanner.visitors.SuperClassVisitor;

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
	
	/**
	 * Find callers of any method of the class <b>className</b> or any subclass
	 * @param className the full class name. ex: "java/lang/Integer"
	 * @return a Set with the callers or a empty Set if no one was found
	 * @throws IOException if an I/O error has occurred
	 */
	public Set<JavaMethod> findCallersOfAnyMethod( String className ) throws IOException {
		return findCallersOfMethod( className, null );
	}
	
	/**
	 * Find callers of <b>methodSignature</b> of the class <b>className</b> or any subclass
	 * @param className the full class name. ex: "java/lang/Integer"
	 * @param methodSignature method signature. ex: "java.lang.Integer methodName(java.lang.Integer)"
	 * @return a Set with the callers or a empty Set if no one was found
	 * @throws IOException if an I/O error has occurred
	 */
	public Set<JavaMethod> findCallersOfMethod( String className, String methodSignature ) throws IOException {
		String methodName = "";
		String methodDesc = "";
		if( methodSignature != null ){
			Method method = Method.getMethod(methodSignature);
			methodName = method.getName();
			methodDesc = method.getDescriptor();
		}
		
		return findCallersOfMethod( className, methodName, methodDesc );
	}
	
	private Set<JavaMethod> findCallersOfMethod(String className, String methodName, String methodDesc) throws IOException {
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

	/**
	 * Find call hierarchy of <b>methodSignature</b> of the class <b>className</b> or any subclass
	 * @param className the full class name. ex: "java/lang/Integer"
	 * @param methodSignature method signature. ex: "java.lang.Integer methodName(java.lang.Integer)"
	 * @return a CallHierarchy starting with the parameter method
	 * @throws IOException if an I/O error has occurred
	 */
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
		JavaMethod method = callHierarchy.getMethod();
		Set<JavaMethod> callers = findCallersOfMethod( method.getClassName(), method.getMethodName(), method.getMethodDesc() );
		for( JavaMethod caller : callers ){
			if( !callHierarchy.containsCall( caller ) ){
				
				CallHierarchy callHierarchyCaller = new CallHierarchy(callHierarchy, caller.getClassName(), caller.getMethodName(), caller.getMethodDesc() );
				callHierarchy.addCaller( callHierarchyCaller );
			}
		}
		
		for( CallHierarchy caller : callHierarchy.getCallers() ){
			findCallHierarchy(caller);
		}
	}
	
	/**
	 * Find call hierarchy of any method of the class <b>className</b> or any subclass
	 * @param className the full class name. ex: "java/lang/Integer"
	 * @return a CallHierarchy starting with the parameter class
	 * @throws IOException if an I/O error has occurred
	 */
	public CallHierarchy findCallHierarchyOfAnyMethod(String className) throws IOException {
		return findCallHierarchy(className, null);
	}

	/**
	 * Find all classes that extends or implements class/interface <b>className</b>
	 * @param className the full class name. ex: "java/lang/Integer"
	 * @return a Set with all classes names, <b>className</b> will be at the Set too,
	 *         it will always return a Set with at least <b>className</b> inside
	 * @throws IOException if an I/O error has occurred
	 */
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

	/**
	 * Closes the jar file
	 * @throws IOException if an I/O error has occurred
	 */
	public void close() throws IOException {
		if( this.jarFile != null ){
			this.jarFile.close();
		}
	}

	
}
