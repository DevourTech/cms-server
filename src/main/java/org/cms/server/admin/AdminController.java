package org.cms.server.admin;

import java.util.List;
import org.cms.core.admin.Admin;
import org.cms.server.admin.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

	private static final String POST_ADMIN_SUCCESS = "Admin added successfully with id = %s";
	private static final String UPDATE_ADMIN_FAILED = "Admin to be updated with id %s doesn't exist in our database records";
	private static final String UPDATE_ADMIN_SUCCESS = "Admin with id %s is successfully updated";
	private static final String DELETE_ADMIN_FAILED = "Admin to be deleted with id %s doesn't exist in our database records";
	private static final String DELETE_ADMIN_SUCCESS = "Admin with id %s is successfully deleted";

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@RequestMapping("/api/admins")
	public List<Admin> getAllAdmins() {
		return adminService.getAllAdmins();
	}

	@RequestMapping("/api/admins/{id}")
	public ResponseEntity<Admin> getAdmin(@PathVariable String id) {
		Admin fetchedAdmin = adminService.getAdmin(id);
		if (fetchedAdmin != null) {
			return ResponseEntity.ok(fetchedAdmin);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/admins")
	public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {
		String idSaved = adminService.addAdmin(admin);
		return ResponseEntity.ok(String.format(POST_ADMIN_SUCCESS, idSaved));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/api/admins/{id}")
	public ResponseEntity<String> addAdmin(@RequestBody Admin admin, @PathVariable String id) {
		boolean isSuccessful = adminService.updateAdmin(id, admin);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(UPDATE_ADMIN_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(UPDATE_ADMIN_SUCCESS, id));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/admins/{id}")
	public ResponseEntity<String> deleteAdmin(@PathVariable String id) {
		boolean isSuccessful = adminService.deleteAdmin(id);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(DELETE_ADMIN_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(DELETE_ADMIN_SUCCESS, id));
	}
}
