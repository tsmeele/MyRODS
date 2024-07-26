package nl.tsmeele.myrods.apiDataStructures;

/** Class that holds a map with WHERE conditions (for use with general queries).
 * The key is the id of a column and the value specifies the related precondition
 * @author Ton Smeele
 *
 */
public class InxValPair extends IrodsMap {
	// "InxValPair_PI", "int isLen; int *inx(isLen); str *svalue[isLen];",

	
	public InxValPair() {
		super("InxValPair_PI", "isLen", "inx", "svalue");
	}

}
