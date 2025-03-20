package main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class Main {

	public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		System.out.println("MyRods started");	
		
		DemoOnLocalVM demo = new DemoOnLocalVM("localVM.ini");
		demo.execute();
		
//		AccScienceTest test = new AccScienceTest("acc.science.ini");
//		test.execute();
		

		
		
		
	}


	

	
	
	
	

}
