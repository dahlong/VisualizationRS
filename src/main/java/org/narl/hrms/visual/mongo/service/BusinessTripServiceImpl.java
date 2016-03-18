package org.narl.hrms.visual.mongo.service;

import java.util.List;

import org.narl.hrms.visual.mongo.collection.BusinessTrip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class BusinessTripServiceImpl  implements  BusinessTripRepo {
	
	@Autowired
    private MongoOperations mongoOperation;

	public List<BusinessTrip> findAll() {
		List<BusinessTrip> result=mongoOperation.findAll(BusinessTrip.class);
		return result;
	}

	public List<BusinessTrip> findByQuery(Query q) {
		List<BusinessTrip> result=mongoOperation.find(q, BusinessTrip.class);
		return result;
	}
	
	public List<BusinessTrip> findByMonth(String fmonth) {
		Query query = new Query(Criteria.where("fmonth").is(fmonth));
		query.with(new Sort(Sort.Direction.ASC, "forg"));
		List<BusinessTrip> result=findByQuery(query) ;
		return result;
	}

	public List<BusinessTrip> findByOrg(String forg) {
		Query query = new Query(Criteria.where("forg").is(forg));
		query.with(new Sort(Sort.Direction.DESC, "fmonth"));
		List<BusinessTrip> result=findByQuery(query) ;
		return result;
	}

	public List<BusinessTrip> findAll(Sort sortString) {
		Query query = new Query();
		query.with(sortString);
		List<BusinessTrip> result=findByQuery(query) ;
		return result;
	}

	public long count() {
		List<BusinessTrip> result=findAll();
		return result==null?0:result.size();
	}

	public Page<BusinessTrip> findAll(Pageable arg0) {
		return null;
	}
}
