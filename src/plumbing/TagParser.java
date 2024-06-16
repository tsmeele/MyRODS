package plumbing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class TagParser manages an xml-like tagged string to facilitate iteration of tag elements.
 * The tags can be nested, does not support tag attributes.
 * @author Ton Smeele
 *
 */
public class TagParser {
	private Matcher m = null;
	private boolean foundTag = false;
	private boolean tagRead = false;
	private String tagLabel = null;
	private boolean endTag = false;
	private String tagContent = null;
	
	private Pattern tag = Pattern.compile(
			"\\s*<" +				// whitespace followed by tag-open delimiter
			"([/]?)" +				// capture group1: has "/" if tag is being closed, else ""
			"\\s*([^>]*)>" +	// capture group2: tag label (left whitespace stripped)
			"([^<]*)");				// capture group3: content in tag (upto next tag or endtag)
	
	
	public TagParser(String input) {
		if (input == null) {
			input = "";
		}
		this.m = tag.matcher(input);
		advanceOneTag();
	}
	
	
	public boolean isRead() {
		return tagRead;
	}
	
	public void setTagRead(boolean read) {
		tagRead = read;
	}
	
	public boolean hasTag() {
		return foundTag;
	}
	
	
	public String getTagLabel() {
		return tagLabel;
	}
	
	public boolean isEndTag() {
		return endTag;
	}
	
	public String getTagContent() {
		return tagContent;
	}
	
	public void advanceOneTag() {
		foundTag = m.find();
		if (foundTag) {
			endTag = m.group(1).equals("/");
			tagLabel = m.group(2);
			tagContent = m.group(3);
		}
		tagRead = false;
	}
	


	
}
