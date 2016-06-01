package com.sartor.bchscanner;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sartor.bchscanner.JavaScanner;
import com.sartor.bchscanner.call.CallHierarchy;
import com.sartor.bchscanner.call.JavaMethod;

public class JavaScannerTest {

	private static JavaScanner javaScanner;
	
	@BeforeClass
	public static void before() {
		try {
			javaScanner = new JavaScanner("TestBytecodeCallHierarchy.jar");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void after() {
		try {
			javaScanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findAllClassesThatExtendsOrImplementsAClassShouldReturnTwo(){
		try {
			Assert.assertEquals(2,javaScanner.findAllClassesThatExtendsOrImplements("com/sartor/testbytecode/AClassImplementsDInterface").size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findAllClassesThatExtendsOrImplementsDInterfaceShouldReturnFour(){
		try {
			Assert.assertEquals(4,javaScanner.findAllClassesThatExtendsOrImplements("com/sartor/testbytecode/DInterface").size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findAllClassesThatExtendsOrImplementsBClassShouldReturnOne(){
		try {
			Assert.assertEquals(1,javaScanner.findAllClassesThatExtendsOrImplements("com/sartor/testbytecode/BClassExtendsAClass").size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findAllClassesThatExtendsOrImplementsInvalidClassShouldReturnZero(){
		try {
			Assert.assertEquals(0,javaScanner.findAllClassesThatExtendsOrImplements("com/sartor/testbytecode/InvalidClass").size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findCallersOfDInterfaceGetAShouldReturnFour(){
		try {
			Set<JavaMethod> callers = javaScanner.findCallersOfMethod("com/sartor/testbytecode/DInterface", "int getA()");
			Assert.assertEquals(4,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findCallersOfGClassUseEClassShouldReturnOne(){
		try {
			Set<JavaMethod> callers = javaScanner.findCallersOfMethod("com/sartor/testbytecode/GClassUsesAClassAndEClass", "Integer useEClass(Integer)");
			Assert.assertEquals(1,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findCallersOfInvalidClassShouldReturnZero(){
		try {
			Set<JavaMethod> callers = javaScanner.findCallersOfMethod("com/sartor/testbytecode/InvalidClass", "Integer useEClass(Integer)");
			Assert.assertEquals(0,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findCallersOfInvalidMethodShouldReturnZero(){
		try {
			Set<JavaMethod> callers = javaScanner.findCallersOfMethod("com/sartor/testbytecode/GClassUsesAClassAndEClass", "Integer invalidMethod(Integer)");
			Assert.assertEquals(0,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findCallersOfClassGClassAnyMethodShouldReturnOne(){
		try {
			Set<JavaMethod> callers = javaScanner.findCallersOfAnyMethod("com/sartor/testbytecode/GClassUsesAClassAndEClass");
			Assert.assertEquals(1,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findCallersOfClassDInterfaceAnyMethodShouldReturnFive(){
		try {
			Set<JavaMethod> callers = javaScanner.findCallersOfAnyMethod("com/sartor/testbytecode/DInterface");
			Assert.assertEquals(5,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
	
	@Test
	public void findCallHierarchyOfClassDInterfaceShouldReturnFive(){
		try {
			CallHierarchy callHierarchy = javaScanner.findCallHierarchyOfAnyMethod("com/sartor/testbytecode/DInterface");
			List<CallHierarchy> callers = callHierarchy.getCallers();
			Assert.assertEquals(5,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}

	@Test
	public void findCallersOfJClassCreateIClassShouldReturnOne(){
		try {
			Set<JavaMethod> callers = javaScanner.findCallersOfMethod("com/sartor/testbytecode/JClassUsesIClass", "com.sartor.testbytecode.IClass createIClass(com.sartor.testbytecode.IClass,com.sartor.testbytecode.IClass)");
			Assert.assertEquals(1,callers.size());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Should not throw an exception");
		}
	}
}
