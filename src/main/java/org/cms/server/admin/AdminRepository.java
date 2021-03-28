package org.cms.server.admin;

import org.cms.core.admin.Admin;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Integer> {
	Admin findById(String id);
}
