package org.narl.hrms.visual.rest.output;

public class LeaveTypesOutput {

	private String id;
	
	private Long total;
	
	private String emp_name;
	
	private String org_name;
	
		
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