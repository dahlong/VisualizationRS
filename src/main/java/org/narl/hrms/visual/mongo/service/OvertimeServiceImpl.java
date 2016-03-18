package org.narl.hrms.visual.mongo.service;

import java.util.List;

import org.narl.hrms.visual.mongo.collection.Overtime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class OvertimeServiceImpl  implements  OvertimeRepo {
	
	@Autowired
    private MongoOperations mongoOperation;
	
	public List<Overtime> findAll() {
		List<Overtime> result=mongoOperation.findAll(Overtime.class);
		return result;
	}

	public List<Overtime> findByQuery(Query q) {
		List<Overtime> result=mongoOperation.find(q, Overtime.class);
		return result;
	}
	
	public List<Overtime> findByMonth(String fmonth) {
		Query query = new Query(Criteria.where("fmonth").is(fmonth));
		query.with(new Sort(Sort.Direction.ASC, "forg"));
		List<Overtime> result=findByQuery(query) ;
		return result;
	}

	public List<Overtime> findByOrg(String forg) {
		Query query = new Query(Criteria.where("forg").is(forg));
		query.with(new Sort(Sort.Direction.DESC, "fmonth"));
		List<Overtime> result=findByQuery(query) ;
		return result;
	}

	public List<Overtime> findAll(Sort sortString) {
		Query query = new Query();
		query.with(sortString);
		List<Overtime> result=findByQuery(query) ;
		return result;
	}

	public long count() {
		List<Overtime> result=findAll();
		return result==null?0:result.size();
	}

//	public Page<Overtime> findAll(Pageable pageable) {
//		if (pageable == null) {
//		    return new PageImpl<Overtime>(findAll());
//		  }
//		  return (Page<Overtime>)getCriteria(null,pageable).list();
//	}

//	public List<Overtime> getAll(int page) {
//		Pageable pageable = new PageRequest(page, 5); //get 5 profiles on a page
//	    Page<Overtime> p = findAll(pageable);
//	    return Lists.newArrayList(p);
//	}
	
}
