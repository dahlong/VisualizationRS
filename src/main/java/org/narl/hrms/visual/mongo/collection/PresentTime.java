package org.narl.hrms.visual.mongo.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "example_present_hours_2015_12")
public class PresentTime {
	@Id
	private String id;
	
	private String pid;
	
	private String fdate;
	
	private String fmonth;
	
	private String forg;
	
	private String fname;
	
	private String fstart;
	
	private String fstartrange;
	
	private String fend;
	
	private String fendrange;
	
	private long fhours;
	
	private long qty;
	
	public PresentTime()
	{
			
			System.out.println("Calling default cons");
	}

	@PersistenceConstructor
	public PresentTime(String id, String pid, String fdate, String fmonth, String fname, String forg, 
			String fstart, String fstartrange,   String fend, String fendrange,  
			long fhours, long qty)
	{
			super();
			this.id = id;
			this.pid=pid;
			this.fdate = fdate;
			this.fmonth = fmonth;
			this.fname=fname;
			this.forg=forg;
			this.fhours=fhours;
			this.fstart=fstart;
			this.fstartrange=fstartrange;
			this.fend=fend;
			this.fendrange=fendrange;
			this.qty=qty;
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
	
	public String getFstart()
	{
			return fstart;
	}

	public void setFstart(String fstart)
	{
			this.fstart = fstart;
	}
	
	public String getFend()
	{
			return fend;
	}
	
	public void setFend(String fend)
	{
			this.fend = fend;
	}
	
	public void setFstartrange(String fstartrange)
	{
			this.fstartrange = fstartrange;
	}
	
	public String getFstartrange()
	{
			return fstartrange;
	}
		

	public void setFendrange(String fendrange)
	{
			this.fendrange = fendrange;
	}
	
	public String getFendrange()
	{
			return fendrange;
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
			return "PresenTime [fdate=" + fdate  + ", (年-月)=" + fmonth +", 時數=" +fhours +", 中心=" +forg  + ", 上班時間=" +fstart + ", 上班範圍=" +fstartrange + ", 下班時間=" +fend + ", 下班範圍=" +fendrange+", 時數=" +fhours  +", 人數="+qty +"]";
	}

}
