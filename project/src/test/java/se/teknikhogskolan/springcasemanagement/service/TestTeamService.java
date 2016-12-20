package se.teknikhogskolan.springcasemanagement.service;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.RecoverableDataAccessException;

import se.teknikhogskolan.springcasemanagement.model.Team;
import se.teknikhogskolan.springcasemanagement.model.User;
import se.teknikhogskolan.springcasemanagement.repository.TeamRepository;
import se.teknikhogskolan.springcasemanagement.repository.UserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.DatabaseException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAllowedException;
import se.teknikhogskolan.springcasemanagement.service.exception.InvalidInputException;
import se.teknikhogskolan.springcasemanagement.service.exception.MaximumQuantityException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public final class TestTeamService {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Team mockedTeam;

    @InjectMocks
    private TeamService teamService;

    private Team teamInDb;
    private Team team;
    private User user;
    private Long teamId;
    private Long userId;

    private final DataAccessException dataAccessException = new RecoverableDataAccessException("Exception");

    @Before
    public void setUp() {
        this.teamInDb = new Team("Team in db");
        this.team = new Team("Team");
        this.user = new User(4L, "test", "test", "test").setTeam(team);
        this.teamId = 5L;
        this.userId = 1L;
    }

    @Test
    public void canGetTeamById() {
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        teamService.getById(teamId);
        verify(teamRepository).findOne(teamId);
    }

    @Test
    public void shouldThrowNoSearchResultExceptionWhenGettingTeamByIdThatDoNotExist() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("Team with id '%d' do not exist", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(null);
        teamService.getById(teamId);
    }

    @Test
    public void shouldThrowDatabaseExceptionIfErrorOccursGettingTeamById() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot find Team with id '%d'.", teamId));
        doThrow(dataAccessException).when(teamRepository).findOne(teamId);
        teamService.getById(teamId);
    }

    @Test
    public void canGetTeamByName() {
        String name = teamInDb.getName();
        when(teamRepository.findByName(name)).thenReturn(teamInDb);
        teamService.getByName(name);
        verify(teamRepository).findByName(name);
    }

    @Test
    public void shouldThrowNoSearchResultExceptionWhenGettingTeamByNameThatDoNotExist() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("Team with name '%s' do not exist", team.getName()));
        when(teamRepository.findByName(team.getName())).thenReturn(null);
        teamService.getByName(team.getName());
    }

    @Test
    public void shouldThrowDatabaseExceptionIfErrorOccursGettingTeamByName() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot get Team with name '%s'.", team.getName()));
        doThrow(dataAccessException).when(teamRepository).findByName(team.getName());
        teamService.getByName(team.getName());
    }

    @Test
    public void canCreateTeam() {
        teamService.create(team.getName());
        verify(teamRepository).save(team);
    }

    @Test
    public void shouldThrowInvalidInputExceptionExceptionWhenCreatingTeamWithAnAlreadyExistingName() {
        thrown.expect(NotAllowedException.class);
        thrown.expectMessage(String.format("Team with name '%s' already exist.", team.getName()));
        doThrow(DuplicateKeyException.class).when(teamRepository).save(team);
        teamService.create(team.getName());
    }

    @Test
    public void shouldThrowDatabaseExceptionIfErrorOccursWhenCreatingTeam() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot create Team with name '%s'.", team.getName()));
        doThrow(dataAccessException).when(teamRepository).save(team);
        teamService.create(team.getName());
    }

    @Test
    public void canUpdateTeamName() {
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        when(teamRepository.save(teamInDb)).thenReturn(teamInDb);
        teamInDb.setName("Old name");
        String newName = "New name";
        Team team = teamService.updateName(teamId, newName);

        verify(teamRepository).save(teamInDb);
        assertEquals(team.getName(), newName);
    }

    @Test
    public void shouldThrowNotAllowedExceptionIfTeamIsInactiveWhenUpdatingName() {
        thrown.expect(NotAllowedException.class);
        thrown.expectMessage(String.format("Cannot update name on Team with id '%d' since it's inactive.", teamId));
        String newName = "New name";
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        teamInDb.setActive(false);
        teamService.updateName(teamId, newName);
    }

    @Test
    public void shouldThrowNoSearchResultExceptionWhenUpdatingNameOnANonExistingTeam() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("Team with id '%d' do not exist.", teamId));
        String newName = "New name";
        when(teamRepository.findOne(teamId)).thenReturn(null);
        teamInDb.setActive(false);
        teamService.updateName(teamId, newName);
    }

    @Test
    public void shouldThrowDatabaseExceptionIfErrorOccursWhenUpdatingTeamName() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot update name on Team with id '%d'.", teamId));
        String newName = "New name";
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        doThrow(dataAccessException).when(teamRepository).save(teamInDb);
        teamService.updateName(teamId, newName);
    }

    @Test
    public void canInactivateTeam() {
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        when(teamRepository.save(teamInDb)).thenReturn(teamInDb);
        Team teamFromDb = teamService.inactivateTeam(teamId);
        verify(teamRepository).save(teamInDb);
        assertFalse(teamFromDb.isActive());
    }

    @Test
    public void shouldThrowNoSearchResultExceptionWhenTryingToInactivateANonExistingTeam() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("Team with id '%d' do not exist.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(null);
        teamService.inactivateTeam(teamId);
    }

    @Test
    public void shouldThrowDatabaseExceptionIfAnErrorOccursWhenInactivatingOnATeam() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot inactivate Team with id '%d'.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        doThrow(dataAccessException).when(teamRepository).save(teamInDb);
        teamService.inactivateTeam(teamId);
    }


    @Test
    public void canActivateTeam() {
        teamInDb.setActive(false);
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        when(teamRepository.save(teamInDb)).thenReturn(teamInDb);
        Team teamFromDb = teamService.activateTeam(teamId);
        verify(teamRepository).save(teamInDb);
        assertTrue(teamFromDb.isActive());
    }

    @Test
    public void shouldThrowNoSearchResultExceptionWhenTryingToActivateANonExistingTeam() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("Team with id '%d' do not exist.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(null);
        teamService.activateTeam(teamId);
    }

    @Test
    public void shouldThrowDatabaseExceptionIfAnErrorOccursWhenActivatingOnATeam() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot activate Team with id '%d'.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(teamInDb);
        doThrow(dataAccessException).when(teamRepository).save(teamInDb);
        teamService.activateTeam(teamId);
    }

    @Test
    public void canGetAllTeams() {
        when(teamRepository.findAll()).thenReturn(new ArrayList<>());
        teamService.getAll();
        verify(teamRepository).findAll();
    }

    @Test
    public void shouldReturnEmptyIterableWhenThereAreNoTeams() {
        when(teamRepository.findAll()).thenReturn(createEmptyIterable());
        Iterable<Team> teams = teamService.getAll();
        if (teams.iterator().hasNext()) fail();
    }

    private Iterable<Team> createEmptyIterable() {
        return () -> new Iterator<Team>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Team next() {
                return null;
            }
        };
    }

    @Test
    public void shouldThrowDatabaseExceptionIfAnErrorOccursWhenGettingAllTeams() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage("Cannot get all Teams.");
        doThrow(dataAccessException).when(teamRepository).findAll();
        teamService.getAll();
    }

    @Test
    public void canAddUserToTeam() {
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        teamService.addUserToTeam(teamId, userId);
        verify(userRepository).save(user);
    }

    @Test
    public void shouldThrowMaximumQuantityExceptionIfTeamIsFullWhenAddingUserToTeam() {
        thrown.expect(MaximumQuantityException.class);
        thrown.expectMessage(String.format("Team with id '%d' already have max amount of 10 allowed Users.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(mockedTeam);
        when(userRepository.findOne(userId)).thenReturn(user);
        when(mockedTeam.isActive()).thenReturn(true);
        int maxAmountOfUsers = 10;
        when(mockedTeam.getUsers()).thenReturn(getUsersFromTeam(maxAmountOfUsers));
        teamService.addUserToTeam(teamId, userId);
    }

    @Test
    public void shouldThrowNoSearchResultExceptionIfUserIdIsNullWhenAddingUserToTeam() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("No User with id '%d' exist.", userId));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(null);
        teamService.addUserToTeam(teamId, userId);
    }

    @Test
    public void shouldThrowNoSearchResultExceptionIfTeamIdIsNullWhenAddingUserToTeam() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("Team with id '%d' do not exist.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(null);
        when(userRepository.findOne(userId)).thenReturn(user);
        teamService.addUserToTeam(teamId, userId);
    }

    @Test
    public void shouldThrowNotAllowedExceptionIfUserIsInactiveWhenAddingUserToTeam() {
        thrown.expect(NotAllowedException.class);
        thrown.expectMessage(String.format("Adding inactive User to Team is not allowed. User with id '%d' is inactive.",
                userId));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        user.setActive(false);
        teamService.addUserToTeam(teamId, userId);
    }

    @Test
    public void shouldThrowNotAllowedExceptionIfTeamIsInactiveWhenAddingUserToTeam() {
        thrown.expect(NotAllowedException.class);
        thrown.expectMessage(String.format("Adding User to inactive Team is not allowed. Team with id '5' is inactive.",
                team.getId()));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        team.setActive(false);
        teamService.addUserToTeam(teamId, userId);
    }

    @Test
    public void shouldThrowDatabaseExceptionIfErrorOccursWhenAddingUserToTeam() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot add User with id '%d' to Team with id '%d'.", userId, teamId));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        doThrow(dataAccessException).when(userRepository).save(user);
        teamService.addUserToTeam(teamId, userId);
    }

    @Test
    public void canDeleteUserFromTeam() {
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        teamService.removeUserFromTeam(teamId, userId);
        verify(userRepository).save(user);
        assertNull(user.getTeam());
    }

    @Test
    public void shouldThrowNoSearchResultExceptionIfUserIdIsNullWhenRemovingUserToTeam() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("No User with id '%d' exist.", userId));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(null);
        teamService.removeUserFromTeam(teamId, userId);
    }

    @Test
    public void shouldThrowNoSearchResultExceptionIfTeamIdIsNullWhenRemovingUserFromTeam() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage(String.format("Team with id '%d' do not exist.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(null);
        when(userRepository.findOne(userId)).thenReturn(user);
        teamService.removeUserFromTeam(teamId, userId);
    }

    @Test
    public void shouldThrowNotAllowedExceptionIfUserIsInactiveWhenRemovingUserToTeam() {
        thrown.expect(NotAllowedException.class);
        thrown.expectMessage(String.format("Removing inactive User from Team is not allowed. User with id '1' is inactive.", userId));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        user.setActive(false);
        teamService.removeUserFromTeam(teamId, userId);
    }

    @Test
    public void shouldThrowNotAllowedExceptionIfTeamIsInactiveWhenRemovingUserFromTeam() {
        thrown.expect(NotAllowedException.class);
        thrown.expectMessage(String.format(
                "Removing User from inactive Team is not allowed. Team with id '1' is inactive.", teamId));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        team.setActive(false);
        teamService.removeUserFromTeam(teamId, userId);
    }

    @Test
    public void shouldThrowDatabaseExceptionIfErrorOccursWhenRemovingUserToTeam() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot remove User with id '%d' from Team with id '%d'.", userId, teamId));
        when(teamRepository.findOne(teamId)).thenReturn(team);
        when(userRepository.findOne(userId)).thenReturn(user);
        doThrow(dataAccessException).when(userRepository).save(user);
        teamService.removeUserFromTeam(teamId, userId);
    }

    @Test
    public void shouldThrowDatabaseExceptionIfErrorOccursWhenFindingUser() {
        thrown.expect(DatabaseException.class);
        thrown.expectMessage(String.format("Cannot find User with id '%d'.", userId));
        doThrow(dataAccessException).when(userRepository).findOne(userId);
        teamService.removeUserFromTeam(teamId, userId);
    }

    private List<User> getUsersFromTeam(int amount) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            users.add(new User(10L + i, "username" + i, "", ""));
        }
        return users;
    }
}