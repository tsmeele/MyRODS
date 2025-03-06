package nl.tsmeele.myrods.apiDataStructures;


import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class ExecMyRuleInp extends DataStruct {
	public static final String EXTERNALRULE = "@external rule ";

	// "ExecMyRuleInp_PI", "str myRule[META_STR_LEN]; struct RHostAddr_PI; struct KeyValPair_PI; str outParamDesc[LONG_NAME_LEN]; struct *MsParamArray_PI;",

	/* To prepare data for rule execution on the connected server, the following shows an example snippet:
	 *    RHostAddr rHostAdrdr = new RHostAddr("", "", 0, 0);
	 *    String myRule = "@external rule " + "{writeLine(\"stdout\",\"Hello *name, I am doing fine\";}"
	 *    MsParam inputVar1 = new MsParam("*name", "Foo");
  	 *    MsParamArray inputVars = new MsParamArray(0);
  	 *    inputVars.add(inputVar1);
	 */

	public ExecMyRuleInp(String myRule, RHostAddr rHostAddr, KeyValPair keyValPair, String outParamDesc, MsParamArray msParamArray) {
		super("ExecMyRuleInp_PI");
		add(new DataString("myRule", myRule));
		add(rHostAddr);
		add(keyValPair);
		add(new DataString("outParamDesc", outParamDesc));
		add(msParamArray);
	}

}
