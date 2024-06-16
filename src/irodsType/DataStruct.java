package irodsType;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class DataStruct implements the Composite class for class Data.
 * @author Ton Smeele
 *
 */
public class DataStruct extends Data {
	protected ArrayList<Data> list = new ArrayList<Data>();
	
	public DataStruct(String variableName) {
		super(variableName);
	}
	
	public DataStruct(String variableName, String nullFlag) {
		super(variableName);
		if (nullFlag == null) {
			nullValue = true;
		}
	}

	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.STRUCT;
	}
	
	public void add(Data data) {
		list.add(data);
	}
	
	public Iterator<Data> iterator() {
		return list.iterator();
	}

	@Override
	public byte[] packNative() {
		byte[] total = new byte[packNativeSize()];
		int index = 0;
		for (Data element : list) {
			byte[] serialized = element.packNative();
			for (int i = 0; i < serialized.length; i++) {
				total[index] = serialized[i];
				index++;
			}
		}
		return total;
	}

	@Override
	public int packNativeSize() {
		int total = 0;
		for (Data element:list) {
			total += element.packNativeSize();
		}
		return total;
	}

	@Override
	public String toXmlString() {
		String out = "";
		for (Data element : list) {
			out = out.concat(element.packXml());
		}
		return out;
	}

	public Data lookupName(String name) {
		for (Data element : list) {
			if (element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		String out = getType().toString() + "[";
		for (Data element : list) {
			out = out.concat(" " + element.toString());
		}
		return out + "]";
	}
	
}
