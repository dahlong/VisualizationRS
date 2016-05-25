package org.narl.hrms.visual.rest.output;

public class OrgOutput {

	 private String id;
	  private String org_id;	  
	  private String org_name;

		public String getIid()
		{
				return id;
		}

		public void setId(String id)
		{
				this.id = id;
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
