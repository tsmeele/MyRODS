package nl.tsmeele.myrods.plumbingTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.plumbing.IrodsProtocolType;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.Packer;
import nl.tsmeele.myrods.plumbing.Unpacker;

class TestUnpacker {
	byte[] packedNative, packedXml, packedXml429;
	private static final String TYPE = "RO<D>'S_\"A&P`I";

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		// 	"MsgHeader_PI", "str type[HEADER_TYPE_LEN]; int msgLen; int errorLen; int bsLen; int intInfo;",

		DataStruct h = new DataStruct("MsgHeader_PI");
		h.add(new DataString("type", TYPE));
		h.add(new DataInt("msgLen", 0));
		h.add(new DataInt("errorLen", 0));
		h.add(new DataInt("bsLen", 0));
		h.add(new DataInt("intInfo", 5));

		packedNative = Packer.pack(IrodsProtocolType.NATIVE_PROT, h);
		packedXml = Packer.pack(IrodsProtocolType.XML_PROT, h);
		packedXml429 = Packer.pack(IrodsProtocolType.XML_PROT429, h);	
	}

	@Test
	void testUnpack() throws MyRodsException {
		DataStruct d1 = Unpacker.unpack(IrodsProtocolType.NATIVE_PROT, "MsgHeader_PI", packedNative);
		DataStruct d2 = Unpacker.unpack(IrodsProtocolType.XML_PROT, "MsgHeader_PI", packedXml);
		DataStruct d3 = Unpacker.unpack(IrodsProtocolType.XML_PROT429, "MsgHeader_PI", packedXml429);
		assertEquals("MsgHeader_PI", d1.getName());
		assertEquals("MsgHeader_PI", d2.getName());
		assertEquals("MsgHeader_PI", d3.getName());
		DataString n1 = (DataString) d1.lookupName("type");
		DataString n2 = (DataString) d1.lookupName("type");
		DataString n3 = (DataString) d1.lookupName("type");
		assertEquals(TYPE, n1.get());
		assertEquals(TYPE, n2.get());
		assertEquals(TYPE, n3.get());

	}

}
