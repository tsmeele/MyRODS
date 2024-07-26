package nl.tsmeele.myrods.irodsDataTypes;

/**
 * The class DataPIStr is a data string type where the content identifies a packing instruction.
 * @author Ton Smeele
 *
 */
public class DataPIStr extends DataString {

	public DataPIStr(String variableName, String text) {
		super(variableName, text);
		setType(IrodsType.PISTR);
	}
	
	public DataPIStr(String variableName, String text, int maxSize) {
		super(variableName, text, maxSize);
		setType(IrodsType.PISTR);
	}
	
	
}
