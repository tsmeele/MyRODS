package nl.tsmeele.myrods.plumbing;

import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataCharArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataInt16;
import nl.tsmeele.myrods.irodsDataTypes.DataInt64;
import nl.tsmeele.myrods.irodsDataTypes.DataPIStr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;

/** Interface for unpack protocol strategies.
 * The Unpacker class will use these methods whenever a variable of a specified type is expected
 * and needs to be unpacked.
 * @author Ton Smeele
 *
 */
public interface InputBuffer {
	
	/** endUnpack is called after all data has been unpacked, to allow the unpack protocol strategy
	 * to wrap up and make sure that all data in the inputbuffer indeed has been processed.
	 * @throws MyRodsException
	 */
	public abstract void endUnpack() throws MyRodsException;	

	public abstract void startUnpackStruct(String packInstruction) throws MyRodsException;
	public abstract void endUnpackStruct(String packInstruction) throws MyRodsException;
	
	/** unpackNullPointer scans the inputbuffer for a nullpointer as the next element. 
	 * If found, it reads and unpacks the nullpointer and returns true.
	 * If not found, the inputbuffer position is unchanged.k
	 * @param name	variable to unpack
	 * @return		true if nullpointer was found and unpacked, otherwise false
	 * @throws MyRodsException
	 */
	public abstract boolean unpackNullPointer(String name) throws MyRodsException;
	public abstract DataInt unpackInt(String name) throws MyRodsException;
	public abstract DataInt16 unpackInt16(String name) throws MyRodsException;
	public abstract DataInt64 unpackInt64(String name) throws MyRodsException;
	public abstract DataString unpackString(String name, int allocSize) throws MyRodsException;
	public abstract DataPIStr unpackPIStr(String name, int allocSize) throws MyRodsException;
	public abstract DataBinArray unpackBinArray(String name, int allocSize) throws MyRodsException;
	public abstract DataCharArray unpackCharArray(String name, int allocSize) throws MyRodsException;
	

	

	


	
	


}
