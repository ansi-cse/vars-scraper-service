package com.resdii.vars.controller.projectController;

import com.resdii.ms.common.utils.RestUtils;
import com.resdii.vars.enums.BotStatus;
import com.resdii.vars.services.projectService.ProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ANSI.
 */

@RestController
@RequestMapping("/project")
public class ProjectController {

    ProjectServiceImpl projectServiceImpl;


    @GetMapping("/runGetLinksProject")
    public ResponseEntity runGetLinksProject() {
        projectServiceImpl.runGetLinks();
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/runGetLinksProjectFailed")
    public ResponseEntity runGetLinksProjectFailed() {
        projectServiceImpl.runGetLinksFailed();
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/runGetDetailProject")
    public ResponseEntity runGetDetailProject(@RequestParam int numOfItems, @RequestParam int numOfThreads) {
        projectServiceImpl.runGetDetailProject(numOfItems, numOfThreads);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/runGetDetailProjectFailed")
    public ResponseEntity runGetDetailProjectFailed(@RequestParam int numOfItems, @RequestParam int numOfThreads) {
        projectServiceImpl.runGetDetailProjectFailed(numOfItems, numOfThreads);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/runGetDetailProjectFailedTry")
    public ResponseEntity runGetDetailProjectFailedTry(@RequestParam int numOfItems, @RequestParam int numOfThreads) {
        projectServiceImpl.runGetDetailProjectFailedTry(numOfItems, numOfThreads);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/runGetDetailProjectProcessing")
    public ResponseEntity runGetDetailProjectProcessing(@RequestParam int numOfItems, @RequestParam int numOfThreads) {
        projectServiceImpl.runGetDetailProjectProcessing(numOfItems, numOfThreads);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/runUpdateProjectDTO")
    public ResponseEntity runUpdateProjectDTO(@RequestParam String projectTable) {
        projectServiceImpl.updateProjectDTO(projectTable);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/concatFile")
    public ResponseEntity concatFile() {
        projectServiceImpl.concatFile();
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @Autowired
    public void setProjectService(ProjectServiceImpl projectServiceImpl) {
        this.projectServiceImpl = projectServiceImpl;
    }
}
