# Bytecode Call Hierarchy Scanner
A java library to scan jar files and find calls to a method

### Maven
	<dependency>
		<groupId>com.github.goblinbr</groupId>
		<artifactId>bytecode-callhierarchy-scanner</artifactId>
		<version>1.0.1</version>
	</dependency>

### Example
```java
import java.io.IOException;
import java.util.Set;

import com.github.goblinbr.bchscanner.JavaScanner;
import com.github.goblinbr.bchscanner.call.CallHierarchy;
import com.github.goblinbr.bchscanner.call.JavaMethod;

public class App {
	public static void main(String[] args) {
		App app = new App();
		System.out.println("-findDirectCallersOfIntegerGetInt-");
		app.findDirectCallersOfIntegerGetInt();
		System.out.println("-findDirectCallersOfShortAnyMethod-");
		app.findDirectCallersOfShortAnyMethod();
		System.out.println("-findAllClassesThatExtendsOrImplementsCloseable-");
		app.findAllClassesThatExtendsOrImplementsCloseable();
		System.out.println("-findCallHierarchyOfIntegerDecode-");
		app.findCallHierarchyOfIntegerDecode();
	}

	/**
	 * Find all callers of method getInt() of the class java.lang.Integer
	 */
	private void findDirectCallersOfIntegerGetInt() {
		try ( JavaScanner javaScanner = new JavaScanner("C:/Program Files (x86)/java/jre7/lib/rt.jar") ){
			Set<JavaMethod> callers = javaScanner.findCallersOfMethod("java/lang/Integer", "int intValue()");
			for( JavaMethod caller : callers ){
				System.out.println( "Class: " + caller.getPackageName() + "." + caller.getSimpleClassName() 
									+ " Method: " + caller.getMethodName()
									+ " Method description: " + caller.getMethodDesc() );
			}
			
			System.out.println( "Found " + callers.size() + " calls to Integer.intValue()" );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Find all callers of any method of the class java.lang.Short
	 */
	private void findDirectCallersOfShortAnyMethod() {
		try ( JavaScanner javaScanner = new JavaScanner("C:/Program Files (x86)/java/jre7/lib/rt.jar") ){
			Set<JavaMethod> callers = javaScanner.findCallersOfAnyMethod("java/lang/Short");
			System.out.println( "Found " + callers.size() + " calls to any method of Short" );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Find all classes/interfaces that implements or extends java.io.Closable
	 */
	private void findAllClassesThatExtendsOrImplementsCloseable() {
		try ( JavaScanner javaScanner = new JavaScanner("C:/Program Files (x86)/java/jre7/lib/rt.jar") ){
			Set<String> classNames = javaScanner.findAllClassesThatExtendsOrImplements("java/io/Closeable");
			for( String className : classNames ){
				System.out.println( "Name: " + className );
			}
			
			System.out.println( "Found " + classNames.size() + " classes that implements or extends Closeable" );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Find call hierarchy of method decode(String) of the class java.lang.Integer
	 */
	private void findCallHierarchyOfIntegerDecode() {
		try ( JavaScanner javaScanner = new JavaScanner("C:/Program Files (x86)/java/jre7/lib/rt.jar") ){
			CallHierarchy callHierarchy = javaScanner.findCallHierarchy("java/lang/Integer","Integer decode(String)");
			printCallHierarchy( callHierarchy, 0 );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printCallHierarchy(CallHierarchy callHierarchy, int level) {
		JavaMethod javaMethod = callHierarchy.getMethod();
		String levelPrefix = "";
		for( int i = 0; i < level; i++ ){
			levelPrefix += "|";
		}
		levelPrefix += "-";
		System.out.println( levelPrefix + " Class: " + javaMethod.getPackageName() + "." + javaMethod.getSimpleClassName() 
							+ " Method: " + javaMethod.getMethodName()
							+ " Method description: " + javaMethod.getMethodDesc() );
		
		for( CallHierarchy caller : callHierarchy.getCallers() ){
			printCallHierarchy( caller, level + 1 );
		}
	}
}
```
