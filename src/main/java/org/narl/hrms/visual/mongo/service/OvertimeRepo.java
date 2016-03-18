package org.narl.hrms.visual.mongo.service;

import java.util.List;

import org.narl.hrms.visual.mongo.collection.Overtime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

public interface OvertimeRepo {
	
	public List<Overtime> findAll();
	
	public List<Overtime> findAll(Sort sort);
	
	public List<Overtime> findByQuery(Query q);
	
	public List<Overtime> findByMonth(String search);
	
	public List<Overtime> findByOrg(String search);

	public long count() ;
	
//	public Page<Overtime> findAll(Pageable arg0) ;	
//	public List<Overtime> getAll(int page) ;	
//	public <S extends Overtime> S insert(S entity) 
//	public <S extends Overtime> List<S> save(Iterable<S> entites) 
//	public <S extends Overtime> List<S> insert(Iterable<S> entities) //
//	public void delete(String arg0) 
//	public void delete(Overtime arg0) 
//	public void delete(Iterable<? extends Overtime> arg0) 
//	public void deleteAll() 
//	public boolean exists(String arg0) 
//	public Iterable<Overtime> findAll(Iterable<String> arg0) 
//	public Overtime findOne(String arg0) 
//	public <S extends Overtime> S save(S arg0) 
}
