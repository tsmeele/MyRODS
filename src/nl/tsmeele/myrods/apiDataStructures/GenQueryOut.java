package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataArray;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/**
 * The class GenQueryOut contains an output datastructure as returned by RcGenQuery.
 * For convenience, its elements are (also) accessible as instance attributes.
 * @author Ton Smeele
 *
 */
public class GenQueryOut extends DataStruct {
	public int intInfo = 0;
	public DataStruct errorMessage = null;
	public int rowCount = 0;		// rowCnt
	public int columnCount = 0;		// attriCnt
	public int totalRowCount = 0;
	public int continueInx = 0;
	public String[][] data = null;
	public String[] columnNames = null;
	
	
	// "SqlResult_PI", "int attriInx; int reslen; str *value(rowCnt)(reslen);",
	// "GenQueryOut_PI", "int rowCnt; int attriCnt; int continueInx; int totalRowCount; struct SqlResult_PI[MAX_SQL_ATTR];",
	
	public GenQueryOut(DataStruct dataStruct) {
		super("GenQueryOut_PI");
		addFrom(dataStruct);
		rowCount = lookupInt("rowCnt");
		columnCount = lookupInt("attriCnt");
		continueInx = lookupInt("continueInx");
		totalRowCount = lookupInt("totalRowCount");
		DataArray results = (DataArray) lookupName("SqlResult_PI", true);
		
		// put returned rows/columns in an array
		data = new String[rowCount][columnCount];
		columnNames = new String[columnCount];
		for (int col = 0; col < columnCount; col++) {
			DataStruct colResult = (DataStruct) results.get(col);
			// get column name
			int attriInx = colResult.lookupInt("attriInx");
			columnNames[col] = Columns.findById(attriInx).getLabel();
			// get array with row data for this column
			DataPtr pValue = (DataPtr) colResult.lookupName("value", true);
			DataArray value = (DataArray) pValue.get(0);
			// extract data 
			for (int row = 0; row < rowCount; row++) {
				data[row][col] = ((DataString) value.get(row)).get();
			}
		}
	}
	
	public String toString() {
		if (rowCount == 0) {
			return "GENQUERY_OUT: {0 rows returned}";
		}
		StringBuilder sb = new StringBuilder();

		sb.append("GENQUERY_OUT: {\nRow: ");
		for (int col = 0; col < columnCount; col++) {
			if (col > 0) {
				sb.append(", ");
			}
			sb.append(columnNames[col]);
		}
		sb.append("\n");
		for (int row = 0; row < rowCount; row++) {
			sb.append("" + row + ": ");
			for (int col = 0; col < columnCount; col++) {
				if (col > 0) {
					sb.append(", ");
				}
				sb.append("\"" + data[row][col] + "\"");
			}
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}
	
}
