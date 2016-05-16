package org.narl.hrms.visual.rest.output;

public class EmpOutput {
	
	 private String id;
	  private String emp_id = null;	  
	  private String emp_name = null;
	  private String emp_number = null;	 
	  
	  private String org_id = null;
	  private String org_name = null;
	 
	  private String dept_id= null;
	  private String dept_name = null;
	  
	  private String user_id = null;
	  private String current_role = null;  //只能抓當下的角色，無法抓某年的角色
	  
	  public String getIid()
		{
				return id;
		}

		public void setId(String id)
		{
				this.id = id;
		}
		
	  public void setUser_id(String user_id)
	  {
	    this.user_id = user_id;
	  }


	  public String getUser_id()
	  {
	    return user_id;
	  }

	  
	  public void setEmp_id(String emp_id)
	  {
	    this.emp_id = emp_id;
	  }


	  public String getEmp_id()
	  {
	    return emp_id;
	  }


	  public void setEmp_number(String emp_number)
	  {
	    this.emp_number = emp_number;
	  }


	  public String getEmp_number()
	  {
	    return emp_number;
	  }
	
	  public void setEmp_name(String emp_name)
	  {
	    this.emp_name = emp_name;
	  }


	  public String getEmp_name()
	  {
	    return emp_name;
	  }
	  
	  public void setCurrent_role(String current_role)
	  {
	    this.current_role = current_role;
	  }


	  public String getCurrent_role()
	  {
	    return current_role;
	  }
	  
	  public void setDept_id(String dept_id)
	  {
	    this.dept_id = dept_id;
	  }


	  public String getDept_id()
	  {
	    return dept_id;
	  }


	  public void setDept_name(String dept_name)
	  {
	    this.dept_name = dept_name;
	  }


	  public String getDept_name()
	  {
	    return dept_name;
	  }


	  public void setOrg_id(String org_id)
	  {
	    this.org_id = org_id;
	  }


	  public String getOrg_id()
	  {
	    return org_id;
	  }


	  public void setOrg_name(String org_name)
	  {
	    this.org_name = org_name;
	  }


	  public String getOrg_name()
	  {
	    return org_name;
	  }

}
