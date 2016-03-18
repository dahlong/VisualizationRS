package org.narl.hrms.visual.mongo.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "example_ferial_code_14_2015")
public class BusinessTrip {
	
	@Id
	private String id;
	
//	private String pid;
	
	private String fdate;
	
	private String fmonth;
	
	private String fname;
	
	private String forg;
	
	private long fhours;
	
	private String fcountry;
	
	private String funit;
	
	private long fleaveon;
	
	private long fleaveoff;
	
	public BusinessTrip()
	{
			
			System.out.println("Calling default cons");
	}

	@PersistenceConstructor
	public BusinessTrip(String id,String fdate, String fmonth, String fname, String forg, 
			long fhours, String fcountry, String funit, long fleaveon, long fleaveoff)
	{
			super();
			this.id = id;
//			this.pid=pid;
			this.fdate = fdate;
			this.fmonth = fmonth;
			this.fname=fname;
			this.forg=forg;
			this.fhours=fhours;
			this.fcountry=fcountry;
			this.funit=funit;
			this.fleaveon=fleaveon;
			this.fleaveoff=fleaveoff;
	}

//	public String getPid()
//	{
//			return pid;
//	}
//
//	public void setPid(String pid)
//	{
//			this.pid = pid;
//	}
	
	
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
	
	public String getFcountry()
	{
			return fcountry;
	}

	public void setFcountry(String fcountry)
	{
			this.fcountry = fcountry;
	}
	
	public String getFunit()
	{
			return funit;
	}

	public void setFunit(String funit)
	{
			this.funit = funit;
	}

	public void setFleaveon(long fleaveon)
	{
			this.fleaveon = fleaveon;
	}	


	public long getFfleaveon()
	{
			return fleaveon;
	}
	
	public void setFleaveoff(long fleaveoff)
	{
			this.fleaveoff = fleaveoff;
	}	


	public long getFfleaveoff()
	{
			return fleaveoff;
	}
	
	@Override
	public String toString()
	{
			return "BusinessTrip [date=" + fdate + ", (年-月)=" + fmonth +", 時數=" +fhours +", 中心=" +forg  + ", 國家=" +fcountry +", 單位=" +funit  +", leave_on="+fleaveon +", leave_off="+fleaveoff +"]";
	}

}
