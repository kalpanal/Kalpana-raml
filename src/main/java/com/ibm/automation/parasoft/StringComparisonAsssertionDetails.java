package com.ibm.automation.parasoft;

public class StringComparisonAsssertionDetails {
	
	public StringComparisonAsssertionDetails(String dataSourceName,
			String assertorStringName, String assertion_Xpath,
			String parameterizedColumnName) {
		super();
		this.dataSourceName = dataSourceName;
		this.assertorStringName = assertorStringName;
		this.assertion_Xpath = assertion_Xpath;
		this.parameterizedColumnName = parameterizedColumnName;
	}
	public StringComparisonAsssertionDetails() {
		// TODO Auto-generated constructor stub
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public  void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public  String getAssertorStringName() {
		return assertorStringName;
	}
	public  void setAssertorStringName(String assertorStringName) {
		this.assertorStringName = assertorStringName;
	}
	public  String getAssertion_Xpath() {
		return assertion_Xpath;
	}
	public  void setAssertion_Xpath(String assertion_Xpath) {
		this.assertion_Xpath = assertion_Xpath;
	}
	public  String getParameterizedColumnName() {
		return parameterizedColumnName;
	}
	public  void setParameterizedColumnName(String parameterizedColumnName) {
		this.parameterizedColumnName = parameterizedColumnName;
	}
	public  String dataSourceName;
	public  String assertorStringName;
	public  String assertion_Xpath;
	public  String parameterizedColumnName;
	public String timestamp;
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
