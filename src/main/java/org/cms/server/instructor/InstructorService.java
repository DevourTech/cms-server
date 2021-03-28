package org.cms.server.instructor;

import java.util.List;
import org.cms.core.instructor.Instructor;
import org.cms.server.instructor.InstructorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InstructorService {

	private final Logger logger = LoggerFactory.getLogger(InstructorService.class);
	private static final String getAllInstructorsPrefix = "GET - getAllInstructors";
	private static final String getInstructorPrefix = "GET - getInstructor";
	private static final String addInstructorPrefix = "POST - addInstructor";
	private static final String updateInstructorPrefix = "PUT - updateInstructor";
	private static final String deleteInstructorPrefix = "DELETE - deleteInstructor";

	private final InstructorRepository instructorRepository;

	public InstructorService(InstructorRepository instructorRepository) {
		this.instructorRepository = instructorRepository;
	}

	public List<Instructor> getAllInstructors() {
		logger.info(String.format("[%s] getAllInstructors HIT", getAllInstructorsPrefix));
		return (List<Instructor>) instructorRepository.findAll();
	}

	// If instructor doesn't exist in db, it returns null
	public Instructor getInstructor(String id) {
		logger.info(String.format("[%s] id received to fetch instructor = %s", getInstructorPrefix, id));

		Instructor byId = instructorRepository.findById(id);
		if (byId == null) {
			logger.error(String.format("[%s] Instructor with id %s doesn't exist in db", getInstructorPrefix, id));
		} else {
			logger.info(String.format("[%s] Instructor fetched from db with id %s is %s", getInstructorPrefix, id, byId));
		}

		return byId;
	}

	// Returns the id of the added instructor
	public String addInstructor(Instructor instructor) {
		logger.info(String.format("[%s] addInstructor HIT", addInstructorPrefix));
		logger.info(String.format("[%s] Instructor received to be added - %s", addInstructorPrefix, instructor));

		Instructor savedInstructor = instructorRepository.save(instructor);
		String genId = String.valueOf(savedInstructor.getId());
		logger.info(String.format("[%s] Instructor successfully added with id = %s", addInstructorPrefix, genId));

		return genId;
	}

	// Returns true if update was successful
	// Returns false if instructor to be updated doesn't exist
	public boolean updateInstructor(String id, Instructor instructor) {
		logger.info(String.format("[%s] updateInstructor HIT for id = %s", updateInstructorPrefix, id));
		logger.info(String.format("[%s] Instructor details = %s", updateInstructorPrefix, instructor));

		Instructor existingInstructor = instructorRepository.findById(id);
		if (existingInstructor == null) {
			logger.error(String.format("[%s] Instructor to be updated (id = %s) doesn't exist", updateInstructorPrefix, id));
			return false;
		}

		updateExistingInstructor(existingInstructor, instructor);
		instructorRepository.save(existingInstructor);
		logger.info(String.format("[%s] Instructor with id %s successfully updated", updateInstructorPrefix, id));

		return true;
	}

	private void updateExistingInstructor(Instructor existingInstructor, Instructor otherInstructor) {
		if (!otherInstructor.getName().equals("")) {
			existingInstructor.setName(otherInstructor.getName());
		}
	}

	// Returns true if instructor is successfully deleted
	// Returns false if instructor doesn't exist
	public boolean deleteInstructor(String id) {
		logger.info(String.format("[%s] deleteInstructor HIT for id = %s", deleteInstructorPrefix, id));

		Instructor toBeRemoved = instructorRepository.findById(id);
		if (toBeRemoved == null) {
			logger.error(String.format("[%s] Instructor to be deleted (id = %s) doesn't exist", deleteInstructorPrefix, id));
			return false;
		}

		instructorRepository.delete(toBeRemoved);
		logger.info(String.format("[%s] Instructor with id %s is successfully deleted", deleteInstructorPrefix, id));
		return true;
	}
}
