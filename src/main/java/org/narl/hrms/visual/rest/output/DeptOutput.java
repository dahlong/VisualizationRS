package org.narl.hrms.visual.rest.output;

public class DeptOutput {

	 private String id;
	  private String dept_id;	  
	  private String dept_name;

		public String getIid()
		{
				return id;
		}

		public void setId(String id)
		{
				this.id = id;
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
	
	
}
