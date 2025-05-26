package nl.tsmeele.myrods.plumbingTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.tsmeele.myrods.plumbing.TagParser;

class TestTagParser {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testNoTags() {
		TagParser t = new TagParser("has no> real <tags at all");
		assertFalse(t.hasTag());
		t = new TagParser("");
		assertFalse(t.hasTag());
		t = new TagParser(null);
		assertFalse(t.hasTag());
	}
	
	@Test
	void testTagScope() {
		// tag followed by (end) tag
		TagParser t = new TagParser("<foo>bar</foo>tag");
		assertTrue(t.hasTag());
		assertEquals(t.getTagLabel(),"foo");
		assertEquals(t.getTagContent(), "bar");
		assertFalse(t.isEndTag());
		// last tag, also end tag
		t = new TagParser("</foo>bar");
		assertTrue(t.hasTag());
		assertEquals(t.getTagLabel(),"foo");
		assertEquals(t.getTagContent(), "bar");
		assertTrue(t.isEndTag());
		// tag without content
		t = new TagParser("<foo>");
		assertTrue(t.hasTag());
		assertEquals(t.getTagLabel(),"foo");
		assertEquals(t.getTagContent(), "");
		assertFalse(t.isEndTag());
		// whitespace
		t = new TagParser(" <foo> bar </foo>tag");
		assertTrue(t.hasTag());
		assertEquals(t.getTagLabel(),"foo");  
		assertEquals(t.getTagContent(), " bar "); 
		assertFalse(t.isEndTag());
	}
	
	
	

}
