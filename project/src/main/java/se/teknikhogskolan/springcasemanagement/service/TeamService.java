package se.teknikhogskolan.springcasemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import se.teknikhogskolan.springcasemanagement.model.Team;
import se.teknikhogskolan.springcasemanagement.model.User;
import se.teknikhogskolan.springcasemanagement.repository.TeamRepository;
import se.teknikhogskolan.springcasemanagement.repository.UserRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.DatabaseException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAllowedException;
import se.teknikhogskolan.springcasemanagement.service.exception.InvalidInputException;
import se.teknikhogskolan.springcasemanagement.service.exception.MaximumQuantityException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public Team getById(Long teamId) {
        return findTeam(teamId);
    }

    private Team findTeam(Long teamId) {
        Team team;
        try {
            team = teamRepository.findOne(teamId);
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot find Team with id '%d'.", teamId), e);
        }
        if (null == team) throw new NotFoundException(String.format("Team with id '%d' do not exist.", teamId))
                .setMissingEntity(Team.class);
        return team;
    }

    public Team getByName(String teamName) {
        Team team;
        try {
            team = teamRepository.findByName(teamName);
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get Team with name '%s'.", teamName), e);
        }
        if (null == team) throw new NotFoundException(String.format("Team with name '%s' do not exist.", teamName));
        return team;
    }

    public Team create(String teamName) {
        String exceptionMessage = String.format("Cannot create Team with name '%s'.", teamName);
        return saveTeam(new Team(teamName), exceptionMessage);
    }

    private Team saveTeam(Team team, String databaseExceptionMessage) {
        try {
            return teamRepository.save(team);
        } catch (DataIntegrityViolationException e) {
            throw new NotAllowedException(String.format("Team with name '%s' already exist.", team.getName()), e);
        } catch (DataAccessException e) {
            throw new DatabaseException(databaseExceptionMessage, e);
        }
    }

    public Team updateName(Long teamId, String teamName) {
        Team team = findTeam(teamId);
        if (!team.isActive()) throw new NotAllowedException(String.format(
                "Cannot update name on Team with id '%d' since it's inactive.", teamId));

        team.setName(teamName);
        String exceptionMessage = String.format("Cannot update name on Team with id '%d'.", teamId);
        return saveTeam(team, exceptionMessage);
    }

    public Team activateTeam(Long teamId) {
        Team team = findTeam(teamId);
        return saveTeam(team.setActive(true), String.format("Cannot activate Team with id '%d'.", teamId));
    }

    public Team inactivateTeam(Long teamId) {
        Team team = findTeam(teamId);
        return saveTeam(team.setActive(false), String.format("Cannot inactivate Team with id '%d'.", teamId));
    }

    public Iterable<Team> getAll() {
        try {
            return teamRepository.findAll();
        } catch (DataAccessException e) {
            throw new DatabaseException("Cannot get all Teams.", e);
        }
    }

    public Team addUserToTeam(Long teamId, Long userId) {
        User user = findUser(userId);
        if (!user.isActive()) throw new NotAllowedException(String.format(
                "Adding inactive User to Team is not allowed. User with id '%d' is inactive.", userId));
        Team team = findTeam(teamId);
        if (!team.isActive()) throw new NotAllowedException(String.format(
                "Adding User to inactive Team is not allowed. Team with id '%d' is inactive.", teamId));

        int maxAllowedUsersInTeam = 10;
        if (team.getUsers().size() >= maxAllowedUsersInTeam) throw new MaximumQuantityException(String.format(
                "Team with id '%d' already have max amount of %d allowed Users.", teamId, maxAllowedUsersInTeam));

        user.setTeam(team);
        saveUser(user, String.format("Cannot add User with id '%d' to Team with id '%d'.", userId, teamId));
        return findTeam(teamId);
    }

    private User findUser(Long userId) {
        User user;
        try {
            user = userRepository.findOne(userId);
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot find User with id '%d'.", userId), e);
        }
        if (null == user) throw new NotFoundException(String.format("No User with id '%d' exist.", userId));
        return user;
    }

    public Team removeUserFromTeam(Long teamId, Long userId) {
        User user = findUser(userId);
        if (!user.isActive()) throw new NotAllowedException(String.format(
                "Removing inactive User from Team is not allowed. User with id '%d' is inactive.", userId));
        Team team = findTeam(teamId);
        if (!team.isActive()) throw new NotAllowedException(String.format(
                "Removing User from inactive Team is not allowed. Team with id '%d' is inactive.", userId));

        user.setTeam(null);
        String exceptionMessage = String.format("Cannot remove User with id '%d' from Team with id '%d'.", userId, teamId);
        saveUser(user, exceptionMessage);
        return findTeam(teamId);
    }

    private User saveUser(User user, String databaseExceptionMessage) {
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            throw new DatabaseException(databaseExceptionMessage, e);
        }
    }
}