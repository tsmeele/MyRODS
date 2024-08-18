package nl.tsmeele.myrods.irodsDataTypes;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class DataStruct implements the Composite class for class Data.
 * @author Ton Smeele
 *
 */
public class DataStruct extends Data implements Iterable<Data> {
	protected ArrayList<Data> list = new ArrayList<Data>();
	
	public DataStruct(String variableName) {
		super(variableName, IrodsType.STRUCT);
	}
	
	public void addFrom(DataStruct original) {
		for (Data d: original.list) {
			add(d);
		}
	}
	
	public void add(Data data) {
		if (data != null) {
			list.add(data);
			data.setParent(this);
		}
	}
	
	public int size() {
		return list.size();
	}
	
	public Data get(int i) {
		if (i < list.size()) {
			return list.get(i);
		}
		return null;
	}
	
	public void set(int i, Data d) {
		if (i < list.size()) {
			list.set(i, d);
		}
	}
	
	public Iterator<Data> iterator() {
		return list.iterator();
	}

	public Data lookupName(String name) {
		return lookupName(name, false);
	}
	
	public Data lookupName(String name, boolean returnStruct) {
		for (Data element : list) {
			boolean subClass = DataStruct.class.isAssignableFrom(element.getClass());
			if ( (!subClass || returnStruct) && element.getName().equals(name) ) {
				return element;
			}
			if (subClass) {
				Data subElement = ((DataStruct)element).lookupName(name, returnStruct);
				if (subElement != null) {
					return subElement;
				}
			}
		}
		return null;
	}
	
	// can also be used for DataCharArray, DataPiStr lookups
	public String lookupString(String name) {
		try {
			return ((DataString)lookupName(name, false)).get();
		} catch (Exception e) {}
		return null;
	}
	
	public Integer lookupInt(String name) {
		try {
			return ((DataInt)lookupName(name, false)).get();
		} catch (Exception e) {}
		return null;
	}
	
	public Long lookupLong(String name) {
		try {
			return ((DataInt64)lookupName(name, false)).get();
		} catch (Exception e) {}
		return null;
	}

	public Short lookupShort(String name) {
		try {
			return ((DataInt16)lookupName(name, false)).get();
		} catch (Exception e) {}
		return null;
	}
	
	public byte[] lookupByte(String name) {
		try {
			return ((DataBinArray)lookupName(name, false)).get();
		}catch (Exception e) {}
		return null;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName() + "{" );
		boolean first = true;
		for (Data element : list) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(element.toString());
		}
		sb.append("}");
		return sb.toString();
	}
	
}
