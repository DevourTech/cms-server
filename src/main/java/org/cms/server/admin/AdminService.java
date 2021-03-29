package org.cms.server.admin;

import java.util.List;
import org.cms.core.admin.Admin;
import org.cms.server.admin.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

	private final Logger logger = LoggerFactory.getLogger(AdminService.class);
	private static final String getAllAdminsPrefix = "GET - getAllAdmins";
	private static final String getAdminPrefix = "GET - getAdmin";
	private static final String addAdminPrefix = "POST - addAdmin";
	private static final String updateAdminPrefix = "PUT - updateAdmin";
	private static final String deleteAdminPrefix = "DELETE - deleteAdmin";

	private final AdminRepository adminRepository;

	public AdminService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}

	public List<Admin> getAllAdmins() {
		logger.info(String.format("[%s] getAllAdmins HIT", getAllAdminsPrefix));
		return (List<Admin>) adminRepository.findAll();
	}

	// If admin doesn't exist in db, it returns null
	public Admin getAdmin(String id) {
		logger.info(String.format("[%s] id received to fetch admin = %s", getAdminPrefix, id));

		Admin byId = adminRepository.findById(id);
		if (byId == null) {
			logger.error(String.format("[%s] Admin with id %s doesn't exist in db", getAdminPrefix, id));
		} else {
			logger.info(String.format("[%s] Admin fetched from db with id %s is %s", getAdminPrefix, id, byId));
		}

		return byId;
	}

	// Returns the id of the added admin
	public String addAdmin(Admin admin) {
		logger.info(String.format("[%s] addAdmin HIT", addAdminPrefix));
		logger.info(String.format("[%s] Admin received to be added - %s", addAdminPrefix, admin));

		Admin savedAdmin = adminRepository.save(admin);
		String genId = String.valueOf(savedAdmin.getId());
		logger.info(String.format("[%s] Admin successfully added with id = %s", addAdminPrefix, genId));

		return genId;
	}

	// Returns true if update was successful
	// Returns false if admin to be updated doesn't exist
	public boolean updateAdmin(String id, Admin admin) {
		logger.info(String.format("[%s] updateAdmin HIT for id = %s", updateAdminPrefix, id));
		logger.info(String.format("[%s] Admin details = %s", updateAdminPrefix, admin));

		Admin existingAdmin = adminRepository.findById(id);
		if (existingAdmin == null) {
			logger.error(String.format("[%s] Admin to be updated (id = %s) doesn't exist", updateAdminPrefix, id));
			return false;
		}

		updateExistingAdmin(existingAdmin, admin);
		adminRepository.save(existingAdmin);
		logger.info(String.format("[%s] Admin with id %s successfully updated", updateAdminPrefix, id));

		return true;
	}

	private void updateExistingAdmin(Admin existingAdmin, Admin otherAdmin) {
		if (otherAdmin.getName() != null) {
			existingAdmin.setName(otherAdmin.getName());
		}
	}

	// Returns true if admin is successfully deleted
	// Returns false if admin doesn't exist
	public boolean deleteAdmin(String id) {
		logger.info(String.format("[%s] deleteAdmin HIT for id = %s", deleteAdminPrefix, id));

		Admin toBeRemoved = adminRepository.findById(id);
		if (toBeRemoved == null) {
			logger.error(String.format("[%s] Admin to be deleted (id = %s) doesn't exist", deleteAdminPrefix, id));
			return false;
		}

		adminRepository.delete(toBeRemoved);
		logger.info(String.format("[%s] Admin with id %s is successfully deleted", deleteAdminPrefix, id));
		return true;
	}
}
