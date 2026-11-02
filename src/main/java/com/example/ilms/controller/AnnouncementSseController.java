package com.example.ilms.controller;

import com.example.ilms.entity.User;
import com.example.ilms.service.AnnouncementSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.print.attribute.standard.Media;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/classes/{classId}/announcements")
public class AnnouncementSseController {
    private final AnnouncementSseService announcementSseService;
    @GetMapping(value = "/stream",
                produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("classId") UUID classId, @AuthenticationPrincipal User user ) {
        return announcementSseService.subscribe(classId, user);
    }
}
