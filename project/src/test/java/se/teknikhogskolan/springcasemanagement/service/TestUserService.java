package se.teknikhogskolan.springcasemanagement.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import se.teknikhogskolan.springcasemanagement.model.User;
import se.teknikhogskolan.springcasemanagement.model.WorkItem;
import se.teknikhogskolan.springcasemanagement.model.WorkItem.Status;
import se.teknikhogskolan.springcasemanagement.repository.UserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;
import se.teknikhogskolan.springcasemanagement.service.exception.ServiceException;

@RunWith(MockitoJUnitRunner.class)
public final class TestUserService {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockedUser;

    @InjectMocks
    private UserService userService;

    @Mock
    WorkItem mockedWorkItem;

    private User user;
    private List<User> users;
    private final DataAccessException dataAccessException = new RecoverableDataAccessException("Exception");

    @Before
    public void init() {
        user = new User(1L, "Long enough name", "First", "Last");
        users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(user);
        }
    }

    @Test
    public void canCheckIfUserExists() {
        when(userRepository.exists(user.getId())).thenReturn(true);
        boolean result = userRepository.exists(user.getId());
        assertTrue(result);
    }

    @Test
    public void createUserThatFillsRequirements() {
        userService.create(user.getUserNumber(), user.getUsername(), user.getFirstName(), user.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void createUserTooShortUsername() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Username 'Too short' is to short. Minimum length is 10 characters.");
        userService.create(user.getUserNumber(), "Too short", user.getFirstName(), user.getLastName());
    }

    @Test
    public void createUserThrowsDataAccessException() {
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot create User '" + user + "'.");
        doThrow(dataAccessException).when(userRepository).save(user);
        userService.create(user.getUserNumber(), user.getUsername(), user.getFirstName(), user.getLastName());
    }

    @Test
    public void getUserByIdReturnsCorrectUser() {
        when(userRepository.findOne(1L)).thenReturn(user);
        User userFromDatabase = userService.getById(1L);
        assertEquals(user, userFromDatabase);
    }

    @Test
    public void getUserByIdThrowsServiceExceptionIfDataAccessException() {
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get User with id '1'.");
        doThrow(dataAccessException).when(userRepository).findOne(1L);
        userService.getById(1L);
    }

    @Test
    public void getUserByIdThrowsNoSearchResultExceptionIfNoUserFound() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No User with id '1' exist.");
        when(userRepository.findOne(1L)).thenReturn(null);
        userService.getById(1L);
    }

    @Test
    public void getUserByUserNumberReturnsCorrectUser() {
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        User userFromDatabase = userService.getByUserNumber(1L);
        assertEquals(user, userFromDatabase);
    }

    @Test
    public void getUserByNumberThrowsServiceExceptionIfDataAccessException() {
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get User with usernumber '1'.");
        doThrow(dataAccessException).when(userRepository).findByUserNumber(1L);
        userService.getByUserNumber(1L);
    }

    @Test
    public void getUserByNumberThrowsNotFoundExceptionIfNoUserFound() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No User with usernumber '1' exist.");
        when(userRepository.findByUserNumber(1L)).thenReturn(null);
        userService.getByUserNumber(1L);
    }

    @Test
    public void updateFirstNameCallsCorrectMethodWithNewFirstName() {
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        String newFirstName = "New first name";
        userService.updateFirstName(1L, newFirstName);
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capturedUser.capture());
        assertEquals(newFirstName, capturedUser.getValue().getFirstName());
    }

    @Test
    public void updateFirstNameInactiveUserThrowsServiceException() {
        user.setActive(false);
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("User is inactive");
        userService.updateFirstName(1L, "Some name");
    }

    @Test
    public void updateFirstNameThrowsNotFoundExceptionIfNoUserFound() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No User with usernumber '1' exist.");
        when(userRepository.findByUserNumber(1L)).thenReturn(null);
        userService.updateFirstName(1L, "some name");
    }

    @Test
    public void updateFirstNameThrowsNotFoundExceptionIfExceptionIsThrown() {
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get User with usernumber '1'.");
        doThrow(dataAccessException).when(userRepository).findByUserNumber(1L);
        userService.updateFirstName(1L, "some name");
    }

    @Test
    public void updateLastNameCallsCorrectMethodWithNewLastName() {
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        String newLastName = "New last name";
        userService.updateLastName(1L, newLastName);
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capturedUser.capture());
        assertEquals(newLastName, capturedUser.getValue().getLastName());
    }

    @Test
    public void updateLastNameInactiveUserThrowsServiceException() {
        user.setActive(false);
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("User is inactive");
        userService.updateLastName(1L, "Some name");
    }

    @Test
    public void updateLastNameThrowsNotFoundExceptionIfNoUserFound() {
        when(userRepository.findByUserNumber(1L)).thenReturn(null);
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No User with usernumber '1' exist.");
        userService.updateLastName(1L, "some name");
    }

    @Test
    public void updateLastNameThrowsServiceExceptionIfExceptionIsThrown() {
        doThrow(dataAccessException).when(userRepository).findByUserNumber(1L);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get User with usernumber '1'.");
        userService.updateLastName(1L, "some name");
    }

    @Test
    public void updateUsernameCallsCorrectMethodWithNewUsername() {
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        String newUsername = "New user name";
        userService.updateUsername(1L, newUsername);
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capturedUser.capture());
        assertEquals(newUsername, capturedUser.getValue().getUsername());
    }

    @Test
    public void updateUsernameInactiveUserThrowsServiceException() {
        user.setActive(false);
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("User is inactive");
        userService.updateUsername(1L, "Some long enough name");
    }

    @Test
    public void updateUsernameTooShortUsernameThrowsServiceException() {
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Username 'Too short' is to short. Minimum length is 10 characters.");
        userService.updateUsername(1L, "Too short");
    }

    @Test
    public void updateUsernameThrowsNoSearchResultExceptionIfNoUserFound() {
        when(userRepository.findByUserNumber(1L)).thenReturn(null);
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No User with usernumber '1' exist.");
        userService.updateUsername(1L, "some long enough name");
    }

    @Test
    public void updateUsernameThrowsServiceExceptionIfExceptionIsThrown() {
        doThrow(dataAccessException).when(userRepository).findByUserNumber(1L);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get User with usernumber '1'.");
        userService.updateUsername(1L, "some long enough name");
    }

    @Test
    public void activateUserCallsCorrectMethod() {
        user.setActive(false);
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        userService.activate(1L);
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capturedUser.capture());
        assertEquals(true, capturedUser.getValue().isActive());
    }

    @Test
    public void activateUserThrowsNoSearchResultIfNoUserFound() {
        when(userRepository.findByUserNumber(1L)).thenReturn(null);
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No User with usernumber '1' exist.");
        userService.activate(1L);
    }

    @Test
    public void activateUserThrowsServiceExcptionIfExceptionIsThrown() {
        doThrow(dataAccessException).when(userRepository).findByUserNumber(1L);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get User with usernumber '1'.");
        userService.activate(1L);
    }

    @Test
    public void inactivateUserSetsAllWorkItemsToUnstartedAndInactivatesUser() {
        List<WorkItem> workItems = new ArrayList<>();
        workItems.add(mockedWorkItem);
        when(userRepository.findByUserNumber(1L)).thenReturn(mockedUser);
        when(mockedUser.getWorkItems()).thenReturn(workItems);
        when(mockedUser.setActive(false)).thenReturn(mockedUser);
        userService.inactivate(1L);
        verify(mockedWorkItem).setStatus(Status.UNSTARTED);
        verify(userRepository).save(mockedUser);
    }

    @Test
    public void inactivateUserNoWorkItemsAttachedStillInactivatesUser() {
        when(userRepository.findByUserNumber(1L)).thenReturn(user);
        userService.inactivate(1L);
        ArgumentCaptor<User> capturedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(capturedUser.capture());
        assertEquals(false, capturedUser.getValue().isActive());
    }

    @Test
    public void inactivateUserThrowsNotFoundExceptionIfNoUserFound() {
        when(userRepository.findByUserNumber(1L)).thenReturn(null);
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No User with usernumber '1' exist.");
        userService.inactivate(1L);
    }

    @Test
    public void inactivateUserThrowsServiceExceptionIfExceptionIsThrown() {
        doThrow(dataAccessException).when(userRepository).findByUserNumber(1L);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get User with usernumber '1'.");
        userService.inactivate(1L);
    }

    @Test
    public void getAllByTeamIdCallsCorrectMethod() {
        when(userRepository.findByTeamId(1L)).thenReturn(users);
        List<User> usersFromDatabase = userService.getAllByTeamId(1L);
        verify(userRepository, times(1)).findByTeamId(1L);
        assertEquals(users, usersFromDatabase);
    }

    @Test
    public void getAllByTeamIdWithNoMatchShouldReturnEmptyList() {
        when(userRepository.findByTeamId(1L)).thenReturn(new ArrayList<>());
        Collection<User> users = userService.getAllByTeamId(1L);
        assertTrue(users.isEmpty());
    }

    @Test
    public void getAllByTeamIdReturnsEmptyListIfNullReturned() {
        when(userRepository.findByTeamId(1L)).thenReturn(null);
        Collection<User> users = userService.getAllByTeamId(1L);
        assertTrue(users.isEmpty());
    }

    @Test
    public void getAllByTeamIdThrowsServiceExceptionIfExceptionThrown() {
        doThrow(dataAccessException).when(userRepository).findByTeamId(1L);
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get all Users with Team id '1'.");
        userService.getAllByTeamId(1L);
    }

    @Test
    public void searchUsersCallsCorrectMethod() {
        String name = "some name";
        when(userRepository.searchUsers(name, name, name))
                .thenReturn(users);
        List<User> usersFromDatabase = userService.search(name, name, name);
        verify(userRepository, times(1)).searchUsers(name, name,
                name);
        assertEquals(users, usersFromDatabase);
    }

    @Test
    public void searchResultsAsEmptyListReturnsEmptyList() {
        when(userRepository.searchUsers("first", "last", "user")).thenReturn(new ArrayList<>());
        Collection<User> users = userService.search("first", "last", "user");
        assertTrue(users.isEmpty());
    }

    @Test
    public void searchResultsInNullReturnsEmptyList() {
        when(userRepository.searchUsers("first", "last", "user")).thenReturn(null);
        Collection<User> users = userService.search("first", "last", "user");
        assertTrue(users.isEmpty());
    }

    @Test
    public void searchUsersThrowsServiceExceptionIfExceptionIsThrown() {
        doThrow(dataAccessException).when(userRepository)
                .searchUsers("first", "last", "user");
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get Users by criteria: first name = first, last name = last, username = user.");
        userService.search("first", "last", "user");
    }

    @Test
    public void getAllByPageCallsCorrectMethodAndReturnsCorrectSliceOfUsers() {
        Page<User> pageUsers = new PageImpl<>(users);
        PageRequest pageRequest = new PageRequest(0, 10);
        when(userRepository.findAll(pageRequest)).thenReturn(pageUsers);
        Slice<User> pageUsersFromDatabase = userService.getAllByPage(0, 10);
        assertEquals(pageUsers, pageUsersFromDatabase);
    }

    @Test
    public void getAllByPageThrowsServiceExceptionIfExceptionThrown() {
        doThrow(dataAccessException).when(userRepository).findAll(new PageRequest(4, 10));
        thrown.expect(ServiceException.class);
        thrown.expectMessage("Cannot get Users by page. Request was 'page 4, size 10'.");
        userService.getAllByPage(4, 10);
    }

    @Test
    public void getAllByPageReturnsNullIfNoUsersFound() {
        when(userRepository.findAll(new PageRequest(4, 10))).thenReturn(null);
        Page page = userService.getAllByPage(4, 10);
        assertTrue(null == page);
    }

    @Test
    public void getByCreationDateCallsCorrectMethodAndReturnsCorrectUsers() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        when(userRepository.findByCreationDate(startDate, endDate)).thenReturn(users);
        List<User> usersReturned = userService.getByCreationDate(startDate, endDate);
        verify(userRepository, times(1)).findByCreationDate(startDate, endDate);
        assertEquals(users, usersReturned);
    }

    @Test
    public void getByCreationDateThrowsServiceExceptionIfExceptionThrown() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        doThrow(dataAccessException).when(userRepository).findByCreationDate(startDate, endDate);
        thrown.equals(ServiceException.class);
        thrown.expectMessage("Cannot get Users created between " + startDate + " and " + endDate + ".");
        userService.getByCreationDate(startDate, endDate);
    }

    @Test
    public void getByCreationDateReturnsEmptyListIfNullIsReturned() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        when(userRepository.findByCreationDate(startDate, endDate)).thenReturn(null);
        Collection<User> users = userService.getByCreationDate(startDate, endDate);
        assertTrue(users.isEmpty());
    }

    @Test
    public void getByCreationDateShouldReturnEmptyListWhenNoMatchFound() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        when(userRepository.findByCreationDate(startDate, endDate)).thenReturn(new ArrayList<>());
        Collection<User> users = userService.getByCreationDate(startDate, endDate);
        assertTrue(users.isEmpty());
    }
}