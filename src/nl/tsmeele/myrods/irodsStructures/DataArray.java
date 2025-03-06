package nl.tsmeele.myrods.irodsStructures;

/**
 * The class DataArray defines a general array type that contains elements of a single Data subtype.
 * @author Ton Smeele
 *
 */
public class DataArray extends DataStruct {
	
	public DataArray(String variableName) {
		super(variableName);
		setType(IrodsType.ARRAY);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean first = true;
		for (Data element : list) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(element.toString());
		}
		sb.append("]");
		return sb.toString();
	}
	
}
