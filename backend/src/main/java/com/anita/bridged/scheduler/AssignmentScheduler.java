package com.anita.bridged.scheduler;

import com.anita.bridged.service.AssignmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AssignmentScheduler {

    private final AssignmentService assignmentService;

    public AssignmentScheduler(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Scheduled(
            fixedDelayString = "${bridged.assignment.fixed-delay-ms:2000}",
            initialDelayString = "${bridged.assignment.initial-delay-ms:2000}"
    )
/*    initialDelay: wait two seconds after startup before the first attempt.
            fixedDelay: after one attempt finishes, wait two seconds before the next.
:2000 supplies a default if the property is not configured.
    One invocation assigns at most one chat, using the existing transaction and locks.*/

    public void assignNextWaitingChat() {
        assignmentService.assignNextChat();
    }

}
