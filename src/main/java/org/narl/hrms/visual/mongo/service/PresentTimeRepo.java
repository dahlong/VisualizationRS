package org.narl.hrms.visual.mongo.service;

import java.util.List;

import org.narl.hrms.visual.mongo.collection.PresentTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

public interface PresentTimeRepo  {

public List<PresentTime> findAll();
	
	public List<PresentTime> findAll(Sort sort);
	
	public List<PresentTime> findByQuery(Query q);
	
	public List<PresentTime> findByMonth(String search);
	
	public List<PresentTime> findByOrg(String search);

	public long count() ;
	
	public Page<PresentTime> findAll(Pageable arg0) ;
	
}
