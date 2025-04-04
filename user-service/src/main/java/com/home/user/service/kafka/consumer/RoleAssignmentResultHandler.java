package com.home.user.service.kafka.consumer;

import com.home.growme.common.module.events.RoleAssignmentResult;
import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class RoleAssignmentResultHandler {


    public static final String USER_ROLE_ASSIGNMENT_RESULT = "user.role.assignments.result";
    private final UserRepository userRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public RoleAssignmentResultHandler(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = USER_ROLE_ASSIGNMENT_RESULT)
    private void handleSuccess(RoleAssignmentResult result) {
//        User user = userRepository.findById(UUID.fromString(result.getUserId()))
//                .orElseThrow(()-> new UserNotFoundException(result.getUserId()));

        log.info("System works fine so far");
    }
}
