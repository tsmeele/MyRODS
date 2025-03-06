package nl.tsmeele.myrods.irodsStructures;

/**
 * DataPtr is a class that represents a pointer. 
 * It can reference either another Data element, or hold nothing (nullpointer).
 * @author Ton Smeele
 *
 */
public class DataPtr extends DataStruct {
	
	public DataPtr(String variableName) {
		super(variableName);
		setType(IrodsType.POINTER);
	}
	
	public DataPtr(String variableName, Data data) {
		super(variableName);
		setType(IrodsType.POINTER);
		if (data != null) {
			add(data);
		}
	}

	public Data get() {
		return get(0);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ptr:");
		if (get(0) == null) {
			sb.append("<null>");
		} else {
			sb.append(get(0).toString());
		}
		return sb.toString();
	}
	
}
