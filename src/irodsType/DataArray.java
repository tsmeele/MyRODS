package irodsType;

/**
 * The class DataArray defines a general array type that contains elements of a single Data subtype.
 * @author Ton Smeele
 *
 */
public class DataArray extends DataStruct {
	
	public DataArray(String variableName) {
		super(variableName);
	}

	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.ARRAY;
	}
	
}
