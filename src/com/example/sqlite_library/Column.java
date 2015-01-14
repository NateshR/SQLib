package com.example.sqlite_library;

public class Column {

	public String data_name;
	public String data_type;
	public String data_constraint_name;

	public Column(String data_name, String data_type,
			String data_constraint_name) {
		super();
		this.data_name = data_name;
		this.data_type = data_type;
		this.data_constraint_name = data_constraint_name;
	}

}
