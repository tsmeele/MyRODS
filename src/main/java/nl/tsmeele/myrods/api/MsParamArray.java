package nl.tsmeele.myrods.api;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import nl.tsmeele.myrods.irodsStructures.Data;
import nl.tsmeele.myrods.irodsStructures.DataArray;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.irodsStructures.RodsCall;

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
	 * Construct an MsParamArray from a generic DataStruct.
	 * This constructor is typically used to interpret an API reply.
	 * @param dataStruct	MsParamArray_PI formatted data structure
	 */
	public MsParamArray(DataStruct dataStruct) {
		super("MsParamArray_PI");
		addFrom(dataStruct);
		if (this.size() == 0) return;
		int paramLen = this.lookupInt("paramLen");
		// convert members from generic DataStruct to MsParam
		if (paramLen < 2) {
			// array is not present in zero/single item case
			DataPtr paramPtr = (DataPtr) get(2); 
			DataStruct param = null;
			if (paramPtr != null) { 
				param = RodsCall.convertToOutputClass((DataStruct)paramPtr.get());
				paramPtr.set(0, param);
			}
		} else {
			DataArray array = (DataArray) get(2);
			for (Data member : array) {
				DataPtr paramPtr = (DataPtr) member;
				DataStruct param = 
					RodsCall.convertToOutputClass((DataStruct)paramPtr.get());
				paramPtr.set(0, param);
			}
		}
		
	}
	
	private void init(int oprType) {
		add(new DataInt("paramLen", 0));
		add(new DataInt("oprType", oprType));
		DataArray array = new DataArray("msParam_PI");
		add(array);
	}
		
	public void add(MsParam msParam) {
		if (msParam == null) {
			return;
		}
		DataInt paramLen = (DataInt) get(0);
		paramLen.set(paramLen.get() + 1);
		getArray().add(new DataPtr("msParam_PI", msParam));
	}
	
	public DataArray getArray() {
		return (DataArray) get(2);
	}
	
	private class MsParamIterator implements Iterator<MsParam> {
		private int nItems;
		private int index;
		
		public void setItems(int n) {
			nItems = n;
		}
		
		@Override
		public boolean hasNext() {
			return nItems > index;
		}

		@Override
		public MsParam next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			DataPtr p = null;
			if (nItems < 2) {
				// array is not present in zero/single item case
				p = (DataPtr) get(2);
			} else {
				p = (DataPtr) getArray().get(index);
			}
			MsParam msParam = (MsParam) p.get();
			index++;
			return msParam;
		}
	}
	
	public Iterator<MsParam> msParamIterator() {
		MsParamIterator it = new MsParamIterator();
		it.setItems(lookupInt("paramLen"));
		return (Iterator<MsParam>) it;
	}
	
}
