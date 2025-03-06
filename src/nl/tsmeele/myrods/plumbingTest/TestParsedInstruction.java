package nl.tsmeele.myrods.plumbingTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataPIStr;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.irodsStructures.IrodsType;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.ParsedInstruction;

class TestParsedInstruction {
		private DataStruct context1 = null;
		private DataStruct context1sub = null;


	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	@BeforeEach
	void setUp() throws Exception {
		context1 = new DataStruct("d");
		context1sub = new DataStruct("dsub");
		context1sub.add(new DataString("foo", "bar"));
		context1sub.add(new DataInt("dataLen", 2));
		context1sub.add(new DataInt("rowLen", 3));
		context1sub.add(new DataPIStr("type", "StartupPack_PI"));
		context1.add(new DataString("var","nothing"));
		context1.add(new DataInt("dataSize", 5));
		context1.add(context1sub);
		context1.add(new DataPIStr("type2", "MsgHeader_PI"));
		context1.add(new DataString("type3", "MsgHeader_PI"));
		context1.add(new DataPtr("type4", new DataPIStr("type4", "MsgHeader_PI")));

	}

	@Test
	void testSyntax() throws MyRodsException {
		ParsedInstruction p = new ParsedInstruction(context1sub, "int i;");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.INT, p.getType());
		assertFalse(p.isPointer());
		assertEquals(1, p.getCardinality());
		assertEquals(0, p.getDimensions().size());
		
		p = new ParsedInstruction(context1sub, "str s[5];");
		assertEquals("s", p.getName());
		assertEquals(IrodsType.STR, p.getType());
		assertFalse(p.isPointer());
		assertEquals(5, p.getCardinality());
		assertEquals(0, p.getDimensions().size());
		
		p = new ParsedInstruction(context1sub, "int *i;");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.INT, p.getType());
		assertTrue(p.isPointer());
		assertEquals(1, p.getCardinality());
		assertEquals(0, p.getDimensions().size());
		
		p = new ParsedInstruction(context1sub, "int *i[5];");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.INT, p.getType());
		assertTrue(p.isPointer());
		assertEquals(5, p.getCardinality());
		assertEquals(0, p.getDimensions().size());
		
		p = new ParsedInstruction(context1sub, "int *i[5](7);");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.INT, p.getType());
		assertTrue(p.isPointer());
		assertEquals(5, p.getCardinality());
		assertEquals(7, (int)p.getDimensions().get(0));
		
		p = new ParsedInstruction(context1sub, "str *i[5](7)(3);");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.STR, p.getType());
		assertTrue(p.isPointer());
		assertEquals(5, p.getCardinality());
		assertEquals(2, (int)p.getDimensions().size());
		assertEquals(7, (int)p.getDimensions().get(0));
		assertEquals(3, (int)p.getDimensions().get(1));
				
	}

	@Test
	void testPI() throws MyRodsException {
		// lookup PI in local scope
		ParsedInstruction p = new ParsedInstruction(context1sub, "?type *foo[2];");
		assertEquals("StartupPack_PI", p.getName());
		assertEquals(IrodsType.STRUCT, p.getType());
		assertTrue(p.isPointer());
		assertEquals(2, p.getCardinality());
		assertEquals(0, (int)p.getDimensions().size());
		
		// lookup PI in parent scope
		p = new ParsedInstruction(context1sub, "?type2 *bar[2];");
		assertEquals("MsgHeader_PI", p.getName());
		assertEquals(IrodsType.STRUCT, p.getType());
		assertTrue(p.isPointer());
		assertEquals(2, p.getCardinality());
		assertEquals(0, (int)p.getDimensions().size());
		
		// type3 is not a DataPIStr but a DataString
		assertThrows(MyRodsException.class, () -> new ParsedInstruction(context1sub, "?type3 *foo[2];") );
		
		// indirect reference
		p = new ParsedInstruction(context1sub, "?type4 *inOutStruct;");
		assertEquals("MsgHeader_PI", p.getName());
		assertEquals(IrodsType.STRUCT, p.getType());
		assertTrue(p.isPointer());
		assertEquals(1, p.getCardinality());
		assertEquals(0, (int)p.getDimensions().size());
	}
	
		
	@Test
	void testLookup() throws MyRodsException {	
		// local scope lookup
		ParsedInstruction p = new ParsedInstruction(context1sub, "str i[dataLen];");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.STR, p.getType());
		assertFalse(p.isPointer());
		assertEquals(2, p.getCardinality());
		assertEquals(0, p.getDimensions().size());

		// parent scope lookup
		p = new ParsedInstruction(context1sub, "str i[dataSize];");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.STR, p.getType());
		assertFalse(p.isPointer());
		assertEquals(5, p.getCardinality());
		assertEquals(0, p.getDimensions().size());

		// iRODS predefined variable lookup
		p = new ParsedInstruction(context1sub, "str i[HEADER_TYPE_LEN];");
		assertEquals("i", p.getName());
		assertEquals(IrodsType.STR, p.getType());
		assertFalse(p.isPointer());
		assertEquals(128, p.getCardinality());
		assertEquals(0, p.getDimensions().size());
		
		//  foo is not of a number type
		assertThrows(MyRodsException.class, () -> new ParsedInstruction(context1sub, "str i[foo];"));
		//  bar is not in scope as a variable name 
		assertThrows(MyRodsException.class, () -> new ParsedInstruction(context1sub, "str i[bar];"));

		// context is missing
		assertThrows(MyRodsException.class, () -> new ParsedInstruction(null, "str i[2];"));
	}
	
}
