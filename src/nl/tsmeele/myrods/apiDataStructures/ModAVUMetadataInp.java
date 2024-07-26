package nl.tsmeele.myrods.apiDataStructures;

import java.util.List;

import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class ModAVUMetadataInp extends DataStruct {
	
	// "ModAVUMetadataInp_PI", "str *arg0; str *arg1; str *arg2; str *arg3; str *arg4; str *arg5; str *arg6; str *arg7;  str *arg8;  
	//                          str *arg9; struct KeyValPair_PI;",

	
    // arg0 - option add, rm, rmw, rmi, cp, mod, or set       NB: option adda is deprecated
    // arg1 - item type -d,-D,-c,-C,-r,-R,-u,-U       (both lowercase and uppercase variants are acceptable)
    // arg2 - item name
    // arg3 - attr name
    // arg4 - attr value
    // arg5 - attr unit
	// note: arg6-8 may be specified in arbitrary sequence, the required prefix indicates the type of the argument
    // arg6 - new attr name (for mod or set)     specify as "n:" + name
    // arg7 - new attr value (for mod or set)    specify as "v:" + value
    // arg8 - new attr unit (for mod or set)     specify as "u:" + unit
    // arg9 - unused

	// KeyValPair supported keywords:
	//        ADMIN_KW  - use admin privs

	public ModAVUMetadataInp(List<String> args, KeyValPair keyValPair ) {
		super("ModAVUMetadataInp_PI");
		for (int i = 0; i < 10; i++) {
			String name = "arg" + String.valueOf(i);
			String arg = null;
			if (i < args.size()) {
				arg = args.get(i);
			}
			add(makeData(name, arg));
		}
		add(keyValPair);
	}
	
	private DataPtr makeData(String name, String value) {
		if (value == null) {
			return new DataPtr(name, null);
		}
		return new DataPtr(name, new DataString(name, value));
	}

}
