package org.narl.hrms.visual.mongo.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LeaveTypes {
	@Id
	private String id;
	
	private String org_id;
	
	private String org_name;
	
	private String dept_id;
	
	private String dept_name;
	
	private String emp_id;
	
	private String emp_name;
	
	private String ferial_name;
	
	private long leave_hours;
	
	private long qty;
	
	public LeaveTypes()
	{
			
			System.out.println("Calling default cons");
	}

	@PersistenceConstructor
	public LeaveTypes(String org_id, String org_name, String dept_name, String dept_id, 
			String emp_id, String emp_name,   String ferial_name, 
			long leave_hours, long qty)
	{
			super();
			this.org_id = org_id;
			this.org_name = org_name;
			this.dept_name=dept_name;
			this.dept_id=dept_id;
			this.emp_id=emp_id;
			this.emp_name=emp_name;
			this.ferial_name=ferial_name;
			this.leave_hours=leave_hours;
			this.qty=qty;
	}
	
	public String getOrg_id()
	{
			return org_id;
	}

	public void setOrg_id(String org_id)
	{
			this.org_id = org_id;
	}
	

	public String getOrg_name()
	{
			return org_name;
	}

	public void setOrg_name(String org_name)
	{
			this.org_name = org_name;
	}
	
	public String getDept_name()
	{
			return dept_name;
	}

	public void setDept_name(String dept_name)
	{
			this.dept_name = dept_name;
	}	

	public String getDept_id()
	{
			return dept_id;
	}

	public void setDept_id(String dept_id)
	{
			this.dept_id = dept_id;
	}	

	public String getEmp_id()
	{
			return emp_id;
	}

	public void setEmp_id(String emp_id)
	{
			this.emp_id = emp_id;
	}
	
	public String getEmp_name()
	{
			return emp_name;
	}

	public void setEmp_name(String emp_name)
	{
			this.emp_name = emp_name;
	}
	
	public String getFerial_name()
	{
			return ferial_name;
	}
	
	public void setFerial_name(String ferial_name)
	{
			this.ferial_name = ferial_name;
	}
	
	public long getLeave_hours()
	{
			return leave_hours;
	}

	public void setLeave_hours(long leave_hours)
	{
			this.leave_hours = leave_hours;
	}
		
	public long getQty()
	{
			return qty;
	}

	public void setQty(long qty)
	{
			this.qty = qty;
	}
	
	@Override
	public String toString()
	{
			return "Leave Count [ 組織 ID=" + org_id  + ", 組織 =" + org_name +", 部門 ID=" +dept_id +", 部門 =" +dept_name  + ", 姓名 ID=" +emp_id 
					+ ", 姓名=" +emp_name + ", 假別=" +ferial_name +", 時數=" +leave_hours  +", 次數="+qty +"]";
	}

}
