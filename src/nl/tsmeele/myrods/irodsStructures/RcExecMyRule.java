package nl.tsmeele.myrods.irodsStructures;


import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.ExecMyRuleInp;

/**
 * Executes a caller supplied rule.
 * @author Ton Smeele
 *
 */
public class RcExecMyRule extends RodsApiCall {
	
	public RcExecMyRule(ExecMyRuleInp execMyRuleInp)  {
		super(Api.EXEC_MY_RULE_AN);
		msg.setMessage(execMyRuleInp);
		
		// see lib/api/src/rcExecMyRule.cpp  the library call executes more actions:
		//   performs an rcDataObjPut/get followed by rcOprComplete after the initial API call
		//   to transport the rule to the chosen server
	}

	@Override
	public String unpackInstruction() {
		return "MsParamArray_PI";
	}

	
}
