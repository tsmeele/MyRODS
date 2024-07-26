package nl.tsmeele.myrods.irodsDataTypes;


/**
 * The class DataCharArray defines a fixed size array of chars.
 * @author Ton Smeele
 *
 */
public class DataCharArray extends DataString {

	public DataCharArray(String variableName, String text, int allocSize) {
		super(variableName, text, allocSize);
		setType(IrodsType.CHAR);
	}
	
	public String get() {
		String text = super.get();
		int allocSize = super.getMaxSize();
		if (text.length() < allocSize) {
			return text;
		}
		// allow 1 space for nullterminator
		return text.substring(0, allocSize - 1);
	}

	
	@Override
	public String toString() {
		return getName() + ":'" + get().replace("'", "\\'") + "'";
	}
}
