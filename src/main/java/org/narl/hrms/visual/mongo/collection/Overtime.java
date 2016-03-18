package org.narl.hrms.visual.mongo.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "example_overtime_hours_2015")
public class Overtime {
	@Id
	private String id;
	
	private String pid;
	
	private String fdate;
	
	private String fmonth;
	
	private String fname;
	
	private String forg;
	
	private long fhours;
	
	private String ftype;
	
	private long fqty;
	
	public Overtime()
	{
			
			System.out.println("Calling default cons");
	}

	@PersistenceConstructor
	public Overtime(String id, String pid, String fdate, String fmonth, String fname, String forg, 
			long fhours, String ftype,  long fqty)
	{
			super();
			this.id = id;
			this.pid=pid;
			this.fdate = fdate;
			this.fmonth = fmonth;
			this.fname=fname;
			this.forg=forg;
			this.fhours=fhours;
			this.ftype=ftype;
			this.fqty=fqty;
	}

	public String getPid()
	{
			return pid;
	}

	public void setPid(String pid)
	{
			this.pid = pid;
	}
	
	public String getFdate()
	{
			return fdate;
	}

	public void setFdate(String fdate)
	{
			this.fdate = fdate;
	}
	

	public String getFmonth()
	{
			return fmonth;
	}

	public void setFmonth(String fmonth)
	{
			this.fmonth = fmonth;
	}
	
	public String getFname()
	{
			return fname;
	}

	public void setFname(String fname)
	{
			this.fname = fname;
	}	

	public String getForg()
	{
			return forg;
	}

	public void setForg(String forg)
	{
			this.forg = forg;
	}	


	public long getFhours()
	{
			return fhours;
	}

	public void setFhours(long fhours)
	{
			this.fhours = fhours;
	}
	
	public String getFtype()
	{
			return ftype;
	}

	public void setFtype(String ftype)
	{
			this.ftype = ftype;
	}
	
	public long getFqty()
	{
			return fqty;
	}

	public void setFqty(long fqty)
	{
			this.fqty = fqty;
	}
	
	@Override
	public String toString()
	{
			return "Overtime [日期=" + fdate + ", (年-月)=" + fmonth +", 姓名=" +fname +", 時數=" +fhours +", 中心=" +forg  + ", 加班種類=" +ftype +", 時數=" +fhours  +", 人數="+fqty +"]";
	}

}
