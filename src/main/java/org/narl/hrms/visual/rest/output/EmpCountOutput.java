package org.narl.hrms.visual.rest.output;

public class EmpCountOutput {

	private String id;
	
	private Long count;
		
	private String org_name;
	
	private String dept_id ;	
	
	private String dept_name ;	

	
	public String getDept_id() {
		return dept_id;
	}

	public void setDept_id(String dept_id) {
		this.dept_id = dept_id;
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
	

	public Long getCount()
	{
			return count;
	}

	public void setCount(Long count)
	{
			this.count = count;
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
