package se.openhack.jobsweeper.recommendation.controllers;

import org.springframework.web.bind.annotation.*;
import se.openhack.jobsweeper.recommendation.database.DatabaseClient;
import se.openhack.jobsweeper.recommendation.entities.Job;
import se.openhack.jobsweeper.recommendation.entities.TagWithCounter;
import se.openhack.jobsweeper.recommendation.requests.JobSwipeBodyRequest;
import se.openhack.jobsweeper.recommendation.requests.UserPreferencesUpdateBody;
import se.openhack.jobsweeper.recommendation.responses.JobApplicants;
import se.openhack.jobsweeper.recommendation.responses.JobRecommendationResponse;
import se.openhack.jobsweeper.recommendation.responses.JobStats;
import se.openhack.jobsweeper.recommendation.responses.OverallEmployerStats;

import javax.annotation.PreDestroy;
import java.util.List;

@RestController
public class MainController {

    private DatabaseClient db = new DatabaseClient();

    @RequestMapping(path = "/get_job_recs",
            method = RequestMethod.GET)
    public @ResponseBody
    JobRecommendationResponse getJobRecs(@RequestParam(value="userId") int id,
                                         @RequestParam(value="recNumber") int recNumber) {
        return db.recommendJobs(id, recNumber);
    }

    @RequestMapping(path = {"/update_user_recommendations"},
            method = RequestMethod.POST)
    public Object updateUser(@RequestBody UserPreferencesUpdateBody input) {
        int userId = input.getUserId();
        db.updateTags(userId, input.getTagDeltas());
        return "OK";
    }

    @RequestMapping(path = {"/create_user"},
            method = RequestMethod.GET)
    public Object createUser(@RequestParam(value="userId") int id, @RequestParam(value="name") String name) {
        db.createUser(id, name);
        return "OK";
    }

    @RequestMapping(path = {"/insert_new_jobs"},
            method = RequestMethod.POST)
    public Object insertNewJobs(@RequestBody List<Job> input) {
        db.insertNewJobs(input);
        return "OK";
    }

    @RequestMapping(path = "/get_tags_for_user",
            method = RequestMethod.GET)
    public @ResponseBody
    List<TagWithCounter> getTagsForUser(@RequestParam(value="userId") int id) {
        return db.getTagsForUser(id);
    }

    @RequestMapping(path = {"/job_swipe"},
            method = RequestMethod.POST)
    public Object jobSwipe(@RequestBody JobSwipeBodyRequest input) {
        db.jobSwipe(input.getUserId(), input.getJobId(), input.isLike());
        return "OK";
    }

    @RequestMapping(path = {"/overall_stats_employer"},
            method = RequestMethod.GET)
    public OverallEmployerStats getOverallEmployerStats(@RequestParam(value="employerId") int id) {
        return db.getOverallEmployerStats(id);
    }

    @RequestMapping(path = {"/job_stats"},
            method = RequestMethod.GET)
    public List<JobStats> getJobStats(@RequestParam(value="employerId") int id) {
        return db.getJobStats(id);
    }

    @RequestMapping(path = {"/job_applicants"},
            method = RequestMethod.GET)
    public List<JobApplicants> getJobApplicants(@RequestParam(value="employerId") int id) {
        return db.getJobApplicants(id);
    }

    @RequestMapping(path = {"/positive_swiped_jobs"},
            method = RequestMethod.GET)
    public List<Integer> getPositiveSwiped(@RequestParam(value="userId") int id) {
        return db.getPositiveSwipes(id);
    }

    @RequestMapping(path = {"/negative_swiped_jobs"},
            method = RequestMethod.GET)
    public List<Integer> getNegativeSwiped(@RequestParam(value="userId") int id) {
        return db.getNegativeSwipes(id);
    }

    @PreDestroy
    public void cleanup() {
        db.close();
    }
}
