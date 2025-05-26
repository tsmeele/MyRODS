package nl.tsmeele.myrods.irodsStructures;

/**
 * The class DataString defines a null-terminated character array.
 * @author Ton Smeele
 *
 */
public class DataString extends Data {
	private String text;
	private int maxSize; // includes space for null-terminator

	public DataString(String variableName, String text) {
		super(variableName, IrodsType.STR);
		set(text);
		}
	
	public DataString(String variableName, String text, int maxSize) {
		super(variableName, IrodsType.STR);
		set(text, maxSize);
	}
	
	
	public String get() {
		if (text.length() < maxSize) {
			return text;
		}
		return text.substring(0, maxSize);
	}
	
	public void set(String text) {
		if (text == null) {
			text = "";
		}
		set(text, text.length() + 1);
	}
	
	public void set(String text, int maxSize) {
		if (text == null) {
			text = "";
		}
		this.maxSize = maxSize;	
		if (text.length() < maxSize) {
			this.text = text;
		} else {
			this.text = text.substring(0, maxSize - 1);
		}
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	@Override
	public String toString() {
		return getName() + ":\"" + text.replace("\"", "\\\"") + "\"";
	}

}
