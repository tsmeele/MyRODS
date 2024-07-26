package nl.tsmeele.myrods.plumbingTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

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
import nl.tsmeele.myrods.plumbing.PackerXml;
import nl.tsmeele.myrods.plumbing.PackerXml429;

class TestPackerXml {
	private PackerXml p = new PackerXml();
	private PackerXml429 p2 = new PackerXml429();


	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testSingleElement() {
		byte[] b = p.pack(null);
		assertEquals(0, b.length);
		b = p.pack(new DataString("var",null));
		assertEquals("<var></var>".length(), b.length);
		b = p.pack(new DataString("var",""));
		assertEquals("<var></var>".length(), b.length);
		b = p.pack(new DataString("var", "abc"));
		assertEquals("<var>abc</var>".length(), b.length);
		b = p.pack(new DataString("var", "abc", 3));
		assertEquals("<var>ab</var>".length(), b.length);
		b = p.pack(new DataString("var", "abcd", 3));
		assertEquals("<var>ab</var>".length(), b.length);
		b = p.pack(new DataPIStr("var", "pi"));
		assertEquals("<var>pi</var>".length(), b.length);
		b = p.pack(new DataCharArray("var","abc",10));
		assertEquals("<var>abc</var>".length(), b.length);
		b = p.pack(new DataCharArray("var","abcdefghij",10));
		assertEquals("<var>abcdefghi</var>".length(), b.length);
		b = p.pack(new DataCharArray("var","abcdefghijk",10));
		assertEquals("<var>abcdefghi</var>".length(), b.length);
		b = p.pack(new DataInt16("var", (short) 3));
		assertEquals("<var>3</var>".length(), b.length);
		b = p.pack(new DataInt("var",  3));
		assertEquals("<var>3</var>".length(), b.length);
		b = p.pack(new DataInt64("var", (long) 3));
		assertEquals("<var>3</var>".length(), b.length);
		byte[] bin = new byte[3];
		b = p.pack(new DataBinArray("var",  bin));
		String s = "<var>" + Base64.getEncoder().encodeToString(bin) + "</var>";
		assertEquals(s.length(), b.length);
	}
	@Test
	void testComposed() {
		// array allocates the space of its elements 
		DataArray a = new DataArray("var");
		a.add(new DataString("var", "abc"));	// abc -> 4 bytes
		a.add(new DataString("var", "de"));		// de -> 3 bytes
		byte[] b = p.pack(a);
		assertEquals("<var>abc</var><var>de</var>".length(), b.length);
		
		// nullpointer is indicated by absence of variable
		b = p.pack(new DataPtr("var", null));
		assertEquals(0, b.length);
		// other pointers allocate the space of the type they point to
		b = p.pack(new DataPtr("var", new DataInt("i",1)));
		assertEquals("<i>1</i>".length(), b.length);
		b = p.pack(new DataPtr("var", new DataInt64("i",(long)1)));
		assertEquals("<i>1</i>".length(), b.length);
		// theoretical case, not a nullpointer, it has an array, yet the array is empty
		b = p.pack(new DataPtr("var", new DataArray("var")));
		assertEquals(0, b.length);
		b = p.pack(new DataPtr("var", a));
		assertEquals("<var>abc</var><var>de</var>".length(), b.length);
		
		// struct allocates the space of its elements
		DataStruct s = new DataStruct("struct");
		s.add(new DataInt("var", 5));			//  4 bytes
		s.add(new DataInt16("var", (short) 6));	//  2 bytes
		b = p.pack(s);
		assertEquals("<struct><var>5</var><var>6</var></struct>".length(), b.length);
		s.add(a);
		b = p.pack(s);
		assertEquals("<struct><var>5</var><var>6</var><var>abc</var><var>de</var></struct>".length(), b.length);
	}
	
	@Test
	void testEscape() {
		// xml packing irods releases < 4.2.9
		byte[] b = p.pack(new DataString("var", "a&bc"));
		assertEquals("<var>a&amp;bc</var>".length(), b.length);
		b = p.pack(new DataString("var", "a<bc"));
		assertEquals("<var>a&lt;bc</var>".length(), b.length);
		b = p.pack(new DataString("var", "a>bc"));
		assertEquals("<var>a&gt;bc</var>".length(), b.length);
		b = p.pack(new DataString("var", "a'bc"));
		assertEquals("<var>a`bc</var>".length(), b.length);
		b = p.pack(new DataString("var", "a`bc"));
		assertEquals("<var>a&apos;bc</var>".length(), b.length);
		
		// xml packing irods releases >= 4.2.9
		b = p2.pack(new DataString("var", "a&bc"));
		assertEquals("<var>a&amp;bc</var>".length(), b.length);
		b = p2.pack(new DataString("var", "a<bc"));
		assertEquals("<var>a&lt;bc</var>".length(), b.length);
		b = p2.pack(new DataString("var", "a>bc"));
		assertEquals("<var>a&gt;bc</var>".length(), b.length);
		b = p2.pack(new DataString("var", "a'bc"));
		assertEquals("<var>a&apos;bc</var>".length(), b.length);
		b = p2.pack(new DataString("var", "a`bc"));
		assertEquals("<var>a`bc</var>".length(), b.length);
	}
	
	
}
