package nl.tsmeele.myrods.irodsDataTypes;

/**
 * Abstract class to represent arbitrary data structure compositions.
 * (Composite design pattern)
 * @author Ton Smeele
 *
 */
public abstract class Data {	
	private String variableName;
	private IrodsType type;
	private DataStruct parent = null;
	
	public Data(String variableName, IrodsType type) {
		this.variableName = variableName;
		this.type = type;
	}
	
	public String getName() {
		return variableName;
	}
	
	public IrodsType getType() {
		return type;
	}
	
	public void setType(IrodsType type) {
		this.type = type;
	}
	
	public DataStruct getParent() {
		return parent;
	}
	
	public void setParent(DataStruct parent) {
		this.parent = parent;
	}
	
}
