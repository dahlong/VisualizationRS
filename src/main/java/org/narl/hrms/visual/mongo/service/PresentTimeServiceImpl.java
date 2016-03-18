package org.narl.hrms.visual.mongo.service;

import java.util.List;

import org.narl.hrms.visual.mongo.collection.PresentTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public  class PresentTimeServiceImpl  implements  PresentTimeRepo {
	
	@Autowired
    private MongoOperations mongoOperation;


	public List<PresentTime> findAll() {
		List<PresentTime> result=mongoOperation.findAll(PresentTime.class);
		return result;
	}

	public List<PresentTime> findByQuery(Query q) {
		List<PresentTime> result=mongoOperation.find(q, PresentTime.class);
		return result;
	}
	
	public List<PresentTime> findByMonth(String fmonth) {
		Query query = new Query(Criteria.where("fmonth").is(fmonth));
		query.with(new Sort(Sort.Direction.ASC, "forg"));
		List<PresentTime> result=findByQuery(query) ;
		return result;
	}

	public List<PresentTime> findByOrg(String forg) {
		Query query = new Query(Criteria.where("forg").is(forg));
		query.with(new Sort(Sort.Direction.DESC, "fmonth"));
		List<PresentTime> result=findByQuery(query) ;
		return result;
	}

	public List<PresentTime> findAll(Sort sortString) {
		Query query = new Query();
		query.with(sortString);
		List<PresentTime> result=findByQuery(query) ;
		return result;
	}

	public long count() {
		List<PresentTime> result=findAll();
		return result==null?0:result.size();
	}

	public Page<PresentTime> findAll(Pageable arg0) {
		return null;
	}
}
