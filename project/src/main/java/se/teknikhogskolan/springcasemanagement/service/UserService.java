package se.teknikhogskolan.springcasemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.User;
import se.teknikhogskolan.springcasemanagement.model.WorkItem.Status;
import se.teknikhogskolan.springcasemanagement.repository.UserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.DatabaseException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAllowedException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final int minimumUsernameLength = 10;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(Long userNumber, String username, String firstName, String lastName) {
        if (usernameToShort(username)) throw new IllegalArgumentException(String.format(
                "Username '%s' is to short. Minimum length is %d characters.", username, minimumUsernameLength));
        User user = new User(userNumber, username, firstName, lastName);
        return saveUser(user, String.format("Cannot create User '%s'.", user.toString()));
    }

    private boolean usernameToShort(String username) {
        return null == username || username.length() < minimumUsernameLength;
    }

    private User saveUser(User user, String dataConnectivityExceptionMessage) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseException(dataConnectivityExceptionMessage, e);
        }
    }

    public User getById(Long userId) {
        User user;
        try {
            user = userRepository.findOne(userId);
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get User with id '%d'.", userId), e);
        }
        if (null == user) throw new NotFoundException(String.format("No User with id '%d' exist.", userId));
        return user;
    }

    public User getByUserNumber(Long userNumber) {
        User user;
        try {
            user = userRepository.findByUserNumber(userNumber);
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get User with usernumber '%d'.", userNumber), e);
        }
        if (null == user) throw new NotFoundException(String.format("No User with usernumber '%d' exist.", userNumber));
        return user;
    }

    public User updateFirstName(Long userNumber, String firstName) {
        User user = getByUserNumber(userNumber);
        if (!user.isActive()) throw new NotAllowedException(String.format(
                "Not allowed to update first name on User with usernumber '%d' because User is inactive. Activate User to enable updating.",
                userNumber));
        user.setFirstName(firstName);
        return saveUser(user, String.format("Cannot update last name on User with usernumber '%d'.", userNumber));
    }

    public User updateLastName(Long userNumber, String lastName) {
        User user = getByUserNumber(userNumber);
        if (!user.isActive()) throw new NotAllowedException(String.format(
                "Not allowed to update last name on User with usernumber '%d' because User is inactive. Activate User to enable updating.",
                userNumber));
        user.setLastName(lastName);
        return saveUser(user, String.format("Cannot update username on User with usernumber '%d'.", userNumber));
    }

    public User updateUsername(Long userNumber, String username) {
        if (usernameToShort(username)) throw new IllegalArgumentException(String.format(
                "Username '%s' is to short. Minimum length is %d characters.", username, minimumUsernameLength));
        User user = getByUserNumber(userNumber);
        if (!user.isActive()) throw new NotAllowedException(String.format(
                "Not allowed to update username on User with usernumber '%d' because User is inactive. Activate User to enable updating.",
                userNumber));

        user.setUsername(username);
        return saveUser(user, "Failed to update username on user with user number: " + userNumber);
    }

    public User activate(Long userNumber) {
        User user = getByUserNumber(userNumber);
        return saveUser(user.setActive(true), String.format("Cannot activate User with usernumber '%d'.", userNumber));
    }

    public User inactivate(Long userNumber) {
        User user = getByUserNumber(userNumber);
        if (null != user.getWorkItems()) {
            user.getWorkItems().forEach(workItem -> workItem.setStatus(Status.UNSTARTED));
        }
        return saveUser(user.setActive(false), String.format("Cannot inactivate User with usernumber '%d'.", userNumber));
    }

    public List<User> getAllByTeamId(Long teamId) {
        try {
            List<User> users = userRepository.findByTeamId(teamId);
            return (null == users) ? new ArrayList<>() : users;
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get all Users with Team id '%d'.", teamId), e);
        }
    }

    public List<User> search(String firstName, String lastName, String username) {
        try {
            List<User> users = userRepository.searchUsers(firstName, lastName, username);
            return (null == users) ? new ArrayList<>() : users;
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get Users by criteria: first name = %s, last name = %s, username = %s.",
                    firstName, lastName, username), e);
        }
    }

    public Page<User> getAllByPage(int pageNumber, int pageSize) {
        try {
            return userRepository.findAll(new PageRequest(pageNumber, pageSize));
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get Users by page. Request was 'page %d, size %d'.",
                    pageNumber, pageSize), e);
        }
    }

    public List<User> getByCreationDate(LocalDate startDate, LocalDate endDate) {
        try {
            List<User> users = userRepository.findByCreationDate(startDate, endDate);
            return (null == users) ? new ArrayList<>() : users;
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get Users created between %s and %s.", startDate, endDate), e);
        }
    }
}