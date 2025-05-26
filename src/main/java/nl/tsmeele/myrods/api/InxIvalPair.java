package nl.tsmeele.myrods.api;

/** Class that holds a map with SELECTed columns (for use with a query).
 * The key is the column id, the value specifies an operator to be applied to output (e.g. sum)
 * @author Ton Smeele
 *
 */
public class InxIvalPair extends IrodsMap {
	// "InxIvalPair_PI", "int iiLen; int *inx(iiLen); int *ivalue(iiLen);",

	public InxIvalPair() {
		super("InxIvalPair_PI", "iiLen", "inx", "ivalue");
	}

}
