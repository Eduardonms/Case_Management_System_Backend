package se.teknikhogskolan.springcasemanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.teknikhogskolan.springcasemanagement.model.Issue;
import se.teknikhogskolan.springcasemanagement.repository.IssueRepository;
import se.teknikhogskolan.springcasemanagement.repository.paging.PagingIssueRepository;
import se.teknikhogskolan.springcasemanagement.service.exception.DatabaseException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotAllowedException;
import se.teknikhogskolan.springcasemanagement.service.exception.NotFoundException;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final PagingIssueRepository pagingIssueRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository, PagingIssueRepository pagingIssueRepository) {
        this.issueRepository = issueRepository;
        this.pagingIssueRepository = pagingIssueRepository;
    }

    public boolean exists(Long issueId) {
        return issueRepository.exists(issueId);
    }

    public Issue getById(Long issueId) {
        return findIssue(issueId);
    }

    private Issue findIssue(Long issueId) {
        Issue issue;
        try {
            issue = issueRepository.findOne(issueId);
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get Issue with id '%d'.", issueId), e);
        }
        if (null == issue) throw new NotFoundException(String.format("No Issue with id '%d' exist.", issueId))
                .setMissingEntity(Issue.class);
        return issue;
    }

    public List<Issue> getByDescription(String description) {
        try {
            List<Issue> issues;
            issues = issueRepository.findByDescription(description);
            return (null == issues) ? new ArrayList<>() : issues;
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get Issues with description '%s'.", description), e);
        }
    }

    public Issue updateDescription(Long issueId, String description) {
        Issue issue = findIssue(issueId);
        if (!issue.isActive()) throw new NotAllowedException(String.format(
                "Updating description on inactive Issue is not allowed. Issue with id '%d' is inactive.", issueId));
        issue.setDescription(description);
        return saveIssue(issue, String.format("Cannot update description on Issue with id '%d'.", issueId));
    }

    private Issue saveIssue(Issue issue, String exceptionMessage) {
        try {
            return issueRepository.save(issue);
        } catch (DataAccessException e) {
            throw new DatabaseException(exceptionMessage, e);
        }
    }

    public Issue inactivate(Long issueId) {
        Issue issue = findIssue(issueId);
        return saveIssue(issue.setActive(false), String.format("Cannot inactivate Issue with id '%d'.", issueId));
    }

    public Issue activate(Long issueId) {
        Issue issue = findIssue(issueId);
        return saveIssue(issue.setActive(true), String.format("Cannot activate Issue with id '%d'.", issueId));
    }

    public Page<Issue> getAllByPage(int pageNumber, int pageSize) {
        try {
            return pagingIssueRepository.findAll(new PageRequest(pageNumber, pageSize));
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Cannot get Issues by page. Request was 'page %d, size %d'.",
                    pageNumber, pageSize), e);
        }
    }
}