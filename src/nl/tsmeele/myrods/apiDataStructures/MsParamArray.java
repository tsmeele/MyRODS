package nl.tsmeele.myrods.apiDataStructures;

import java.util.Iterator;
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
	
	/**
	 * Construct an MsParamArray from an API reply's message part.
	 * @param message	MsParamArray_PI formatted message
	 */
	public MsParamArray(DataStruct message) {
		super("MsParamArray_PI");
		addFrom(message);
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
	
	private class MsParamIterator implements Iterator<MsParam> {
		private int index;
		
		private MsParam get(int i) {
			DataArray array = getArray();
			if (array.size() < i) {
				return null;
			}
			return (MsParam) array.get(i);
		}
		
		@Override
		public boolean hasNext() {
			return get(index) != null;
		}

		@Override
		public MsParam next() {
			MsParam msParam = get(index);
			index++;
			return msParam;
		}
	}
	
	public Iterator<MsParam> msParamIterator() {
		return new MsParamIterator();
	}
	
}
