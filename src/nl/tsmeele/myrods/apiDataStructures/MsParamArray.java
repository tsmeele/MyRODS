package nl.tsmeele.myrods.apiDataStructures;

import java.util.List;

import nl.tsmeele.myrods.irodsDataTypes.DataArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class MsParamArray extends DataStruct {

	// 	"MsParamArray_PI", "int paramLen; int oprType; struct *MsParam_PI[paramLen];",

	
	/**
	 * @param oprType	0 = input parameter
	 * @param msParams	list of parameters
	 */
	public MsParamArray(int oprType, List<MsParam> msParams) {
		super("MsParamArray_PI");
		init(oprType);
		for (MsParam param : msParams) {
			add(param);
		}
	}
	
	public MsParamArray(int oprType) {
		super("MsParamArray_PI");
		init(oprType);
	}
	
	private void init(int oprType) {
		add(new DataInt("paramLen", 0));
		add(new DataInt("oprType", oprType));
		DataArray array = new DataArray("msParam_PI");
		add(new DataPtr("msParam_PI", array));
	}
	
	
	public void add(MsParam msParam) {
		if (msParam == null) {
			return;
		}
		DataInt paramLen = (DataInt) get(0);
		DataArray array = (DataArray) ((DataPtr) get(2)).get();
		paramLen.set(paramLen.get() + 1);
		array.add(msParam);
	}
	
	public DataArray getArray() {
		return (DataArray) ((DataPtr) get(2)).get();
	}
	
}
