package filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import filmorate.dto.EventDto;
import filmorate.service.feed.FeedService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/{id}/feed")
    public List<EventDto> getUserFeed(@PathVariable("id") long userId) {
        return feedService.getUsersEventFeed(userId);
    }
}