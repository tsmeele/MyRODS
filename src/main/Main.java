package main;

import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException{
		System.out.println("MyRods started");	
		
	//	DemoOnLocalVM demo = new DemoOnLocalVM("localVM.ini");
	//	demo.execute();
		
		AccScienceTest test = new AccScienceTest("acc.science.ini");
		test.execute();
		
		
	}


	

	
	
	
	

}
