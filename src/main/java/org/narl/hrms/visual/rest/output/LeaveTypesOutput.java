package org.narl.hrms.visual.rest.output;

public class LeaveTypesOutput {

	private String id;
	
	private Long total;
	
	private String emp_name;
	
	private String org_name;
	
	private String dept_name ;	
	
	private Float average ;
	
	public Float getAverage() {
		return average;
	}

	public void setAverage(Float average) {
		this.average = average;
	}

	public String getDept_name() {
		return dept_name;
	}

	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}

	public String getIid()
	{
			return id;
	}

	public void setId(String id)
	{
			this.id = id;
	}
	

	public Long getTotal()
	{
			return total;
	}

	public void setTotal(Long total)
	{
			this.total = total;
	}
	
	public String getEmp_name()
	{
			return emp_name;
	}

	public void setEmp_name(String emp_name)
	{
			this.emp_name = emp_name;
	}
	
	public String getOrg_name()
	{
			return org_name;
	}

	public void setOrg_name(String org_name)
	{
			this.org_name = org_name;
	}
	
}
