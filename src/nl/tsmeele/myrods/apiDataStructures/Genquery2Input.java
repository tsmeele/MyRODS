package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class Genquery2Input extends DataStruct {
	//  Genquery2Input_PI "str *query_string; str *zone; int sql_only; int column_mappings;"

	//  STATUS EXPERIMENTAL IN iRODS 4.3.2, API/STRUCT MAY CHANGE
	
	
	// if sql_only" is set to 1, "output" will hold the SQL derived from the GenQuery2 syntax
	public Genquery2Input(String query, String zone, int sqlOnly, int columnMappings) {
		super("Genquery2Input_PI");
		add(new DataPtr("query_string", new DataString("query_string", query)));
		add(new DataPtr("zone", new DataString("zone", zone)));
		add(new DataInt("sql_only",sqlOnly));
		add(new DataInt("column_mappings", columnMappings));
	}

}
