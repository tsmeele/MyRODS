package main;

import java.io.IOException;
import java.util.Iterator;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.api.RcExecMyRule;
import nl.tsmeele.myrods.apiDataStructures.ExecMyRuleInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Kw;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.MsParam;
import nl.tsmeele.myrods.apiDataStructures.MsParamArray;
import nl.tsmeele.myrods.apiDataStructures.RHostAddr;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/** Demonstrator class to show the execution of a client rule with input and output variables.
 *  Precondition: authenticated user session established
 * @author Ton Smeele
 *
 */
public class ExeRule {
	private final String EXTERNALRULE = "@external rule ";
	private ServerConnection irodsSession;
	
	public ExeRule(ServerConnection irodsSession) {
		this.irodsSession = irodsSession;
	}
	
	public void execute() throws MyRodsException, IOException {
		// prepare common arguments
		RHostAddr rHostAddr = new RHostAddr("", "", 0, 0);
		KeyValPair condInput = new KeyValPair();
		condInput.put(Kw.INSTANCE_NAME_KW, "irods_rule_engine_plugin-irods_rule_language-instance");
		
		// prepare rule-specific arguments
		String myRule = EXTERNALRULE + 
		        "{ writeLine(\"serverLog\", \"Hello this is a message from *inp using a rule\");\n" +
				" writeLine(\"stdout\", \"The answer is listed in an output variable\");\n" +
				" *outp = 42;}";
		String outParamDesc = "ruleExecOut";
		// we can add additional output parameters, using % symbol as a separator 
		outParamDesc = outParamDesc.concat("%*outp");
		// build an array of our input variables
		MsParam msParam = new MsParam("*inp", "Foo");
		MsParamArray inputVars = new MsParamArray(0);
		inputVars.add(msParam);
		
		System.out.println("RULE TO EXECUTE:\n" + myRule + "\n");
		System.out.println("INPUTVARS:");
		Iterator<MsParam> it = inputVars.msParamIterator();
		while (it.hasNext()) {
			printMsParam(it.next());
			System.out.println();
		}
		
		// execute the rule
		ExecMyRuleInp ruleInp = new ExecMyRuleInp(myRule, rHostAddr, condInput, outParamDesc, inputVars);
		RcExecMyRule rcExecMyRule = new RcExecMyRule(ruleInp);
		Message reply = rcExecMyRule.sendTo(irodsSession);
		Log.debug(reply.toString());
		MsParamArray msArray = (MsParamArray) reply.getMessage();
		
		// show returned parameters
		System.out.println("OUTPUTVARS:");
		it = msArray.msParamIterator();
		while (it.hasNext()) {
			printMsParam(it.next());
		}
	}

	
	private void printMsParam(MsParam msParam) {
		System.out.println("Name: " + msParam.getParamName());
		String type = msParam.getParamType();
		System.out.println("Type: " + type);
		DataStruct content = msParam.getParamContent();
		if (type.equals("ExecCmdOut_PI")) {
			DataBinArray ruleExecOut = (DataBinArray) content.lookupName("buf");
			System.out.println("Contents of ruleExecOut:\n\"" + ruleExecOut.getAsString() + "\"");
		} else {
			if (content != null) {
				System.out.println("Content: " + content);
			} else {
				System.out.println("Content: <null>");
			}
		}
	}
	
	
	
}
