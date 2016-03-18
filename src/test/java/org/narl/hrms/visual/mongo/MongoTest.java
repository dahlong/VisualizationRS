package org.narl.hrms.visual.mongo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.narl.hrms.visual.mongo.collection.BusinessTrip;
import org.narl.hrms.visual.mongo.collection.Overtime;
import org.narl.hrms.visual.mongo.collection.PresentTime;
import org.narl.hrms.visual.mongo.service.BusinessTripServiceImpl;
import org.narl.hrms.visual.mongo.service.OvertimeServiceImpl;
import org.narl.hrms.visual.mongo.service.PresentTimeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader; 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMongoConfigForUnitTest.class, loader = AnnotationConfigContextLoader.class)
public class MongoTest {

	@Autowired
	BusinessTripServiceImpl btest ;
	
	@Autowired
	OvertimeServiceImpl otest ;
	
	@Autowired
	PresentTimeServiceImpl ptest ;
	
	@Before
	public void init() {
		
	}
 
	@Test
	public void testBusinessTrip(){
		System.out.println("Business Trip Test=====================================");
		assertEquals(200,btest.count());
		 
//		List<BusinessTrip> list = btest.findByMonth("2015-11");
//		assertEquals(200, list.size()); 
		
		{
			Query query = new Query(Criteria.where("forg").is("NCHC"));
			query.with(new Sort(Sort.Direction.DESC, "fmonth"));
			List<BusinessTrip> list = btest.findByQuery(query);
			 for (BusinessTrip b : list) {
				System.out.println("find by query>>" +b);
			}
		}
		
		{
			Sort s=new Sort(Sort.Direction.DESC, "forg");
	    	 List<BusinessTrip> list = btest.findAll(s);
	    	 for (BusinessTrip b : list) {
					System.out.println("find all and sort>>" +b);
				}        
		}
		
		
		{
			 Query query = new Query(Criteria.where("fname").is("林雅雯"));
				query.with(new Sort(Sort.Direction.DESC, "fmonth"));
				List<BusinessTrip> list = btest.findByQuery(query);
				 for (BusinessTrip b : list) {
					System.out.println("find by name>>" +b);
				}
		}
		
	}
	
	@Test
	public void testOverTime(){
		System.out.println("Overtime Test=====================================");
//		assertEquals(200, otest.count());
		 
//		List<Overtime> list = otest.findByMonth("2015-11");
//		assertEquals(200, list.size()); 
		
		{
			Query query = new Query(Criteria.where("forg").is("NCHC"));
			query.with(new Sort(Sort.Direction.DESC, "fmonth"));
			List<Overtime> list = otest.findByQuery(query);
			 for (Overtime b : list) {
				System.out.println("find by query>>" +b);
			}
		}
		
		{
			Sort s=new Sort(Sort.Direction.DESC, "forg");
	    	 List<Overtime> list = otest.findAll(s);
	    	 for (Overtime b : list) {
					System.out.println("find all and sort>>" +b);
				}        
		}
		
		
		{
			 Query query = new Query(Criteria.where("fname").is("林雅雯"));
				query.with(new Sort(Sort.Direction.DESC, "fmonth"));
				List<Overtime> list = otest.findByQuery(query);
				 for (Overtime b : list) {
					System.out.println("find by name>>" +b);
				}
		}
	}
	
	
	@Test
	public void testPresentTime(){
		System.out.println("PresentTime Test=====================================");
//		assertEquals(200, ptest.count());
		 
//		List<PresentTime> list = ptest.findByMonth("2015-11");
//		assertEquals(200, list.size()); 
		
		{
			Query query = new Query(Criteria.where("forg").is("NCHC"));
			query.with(new Sort(Sort.Direction.DESC, "fmonth"));
			List<PresentTime> list = ptest.findByQuery(query);
			 for (PresentTime b : list) {
				System.out.println("find by query>>" +b);
			}
		}
		
		{
			Sort s=new Sort(Sort.Direction.DESC, "forg");
	    	 List<PresentTime> list = ptest.findAll(s);
	    	 for (PresentTime b : list) {
					System.out.println("find all and sort>>" +b);
				}        
		}
		
		
		{
			 Query query = new Query(Criteria.where("fname").is("林雅雯"));
				query.with(new Sort(Sort.Direction.DESC, "fmonth"));
				List<PresentTime> list = ptest.findByQuery(query);
				 for (PresentTime b : list) {
					System.out.println("find by name>>" +b);
				}
		}
	}

}
