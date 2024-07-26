package nl.tsmeele.myrods.plumbingTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.tsmeele.myrods.irodsDataTypes.DataArray;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataCharArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataInt16;
import nl.tsmeele.myrods.irodsDataTypes.DataInt64;
import nl.tsmeele.myrods.irodsDataTypes.DataPIStr;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.plumbing.PackerNative;

class TestPackerNative {
	private PackerNative p = new PackerNative();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testPackSingleElement() {
		byte[] b = p.pack(null);
		assertEquals(0, b.length);
		b = p.pack(new DataString("var",null));
		assertEquals(1, b.length);
		b = p.pack(new DataString("var",""));
		assertEquals(1, b.length);
		b = p.pack(new DataString("var", "abc"));
		assertEquals(4, b.length);
		assertEquals(0, (int)b[3]);	// nullterminator
		b = p.pack(new DataString("var", "abc", 3));
		assertEquals(3, b.length);
		assertEquals(0, (int)b[2]);	// nullterminator
		b = p.pack(new DataString("var", "abcd", 3));
		assertEquals(3, b.length);
		b = p.pack(new DataPIStr("var", "pi"));
		assertEquals(3, b.length);
		assertEquals(0, (int)b[2]);	// nullterminator
		b = p.pack(new DataCharArray("var","abc",10));
		assertEquals(10, b.length);
		b = p.pack(new DataCharArray("var","abcdefghij",10));
		assertEquals(10, b.length);
		assertEquals(0, (int)b[9]);	// nullterminator
		b = p.pack(new DataCharArray("var","abcdefghijk",10));
		assertEquals(10, b.length);
		assertEquals(0, (int)b[9]);	// nullterminator
		b = p.pack(new DataInt16("var", (short) 3));
		assertEquals(2, b.length);
		b = p.pack(new DataInt("var",  3));
		assertEquals(4, b.length);
		b = p.pack(new DataInt64("var", (long) 3));
		assertEquals(8, b.length);
		b = p.pack(new DataBinArray("var", new byte[5]));
		assertEquals(5, b.length);
	}
	
	@Test
	void testPackComposed() {
		// array allocates the space of its elements 
		DataArray a = new DataArray("var");
		a.add(new DataString("var", "abc"));	// abc -> 4 bytes
		a.add(new DataString("var", "de"));		// de -> 3 bytes
		byte[] b = p.pack(a);
		assertEquals(7, b.length);
		
		// nullpointer represented by UTF-8 encoded null-terminated string : "%@#ANULLSTR$%"
		b = p.pack(new DataPtr("var", null));
		assertEquals(14, b.length);
		// other pointers allocate the space of the type they point to
		b = p.pack(new DataPtr("var", new DataInt("i",1)));
		assertEquals(4, b.length);
		b = p.pack(new DataPtr("var", new DataInt64("i",(long)1)));
		assertEquals(8, b.length);
		// theoretical case, not a nullpointer, it has an array, yet the array is empty
		b = p.pack(new DataPtr("var", new DataArray("var")));
		assertEquals(0, b.length);
		b = p.pack(new DataPtr("var", a));
		assertEquals(7, b.length);
		
		// struct allocates the space of its elements
		DataStruct s = new DataStruct("var");
		s.add(new DataInt("var", 5));			//  4 bytes
		s.add(new DataInt16("var", (short) 6));	//  2 bytes
		b = p.pack(s);
		assertEquals(6, b.length);
		s.add(a);
		b = p.pack(s);
		assertEquals(13, b.length);
	}

}
