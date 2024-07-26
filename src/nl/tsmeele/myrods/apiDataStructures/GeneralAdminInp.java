package nl.tsmeele.myrods.apiDataStructures;

import java.util.List;

import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class GeneralAdminInp extends DataStruct {

	
	public GeneralAdminInp(List<String> args) {
		super("GeneralAdminInp_PI");
		// "generalAdminInp_PI", "str *arg0; str *arg1; str *arg2; str *arg3; str *arg4; str *arg5; str *arg6; str *arg7;  str *arg8;  str *arg9;",

		for (int i = 0; i < 10; i++) {
			String name = "arg" + String.valueOf(i);
			String arg = null;
			if (i < args.size()) {
				arg = args.get(i);
			}
			add(makeData(name, arg));
		}
	}
	
	private DataPtr makeData(String name, String value) {
		if (value == null) {
			return new DataPtr(name, null);
		}
		return new DataPtr(name, new DataString(name, value));
	}

}
