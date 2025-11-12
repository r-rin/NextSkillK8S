package ukma.springboot.nextskill.user.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ukma.springboot.nextskill.common.exceptions.NoAccessException;
import ukma.springboot.nextskill.common.exceptions.ResourceNotFoundException;
import ukma.springboot.nextskill.common.communication.JmsDestinations;
import ukma.springboot.nextskill.common.models.entities.UserEntity;
import ukma.springboot.nextskill.common.dto.enums.UserRole;
import ukma.springboot.nextskill.common.models.mappers.UserMapper;
import ukma.springboot.nextskill.common.dto.responses.CourseResponse;
import ukma.springboot.nextskill.common.dto.responses.UserResponse;
import ukma.springboot.nextskill.common.dto.views.UserView;
import ukma.springboot.nextskill.user.UserService;
import ukma.springboot.nextskill.user.repository.UserRepository;
import ukma.springboot.nextskill.user.validation.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final JmsTemplate topicJmsTemplate;

    public UserServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserValidator userValidator,
        @Qualifier("jmsTopicTemplate") JmsTemplate jmsTemplate
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
        this.topicJmsTemplate = jmsTemplate;
    }

    @Override
    public UserEntity getEntity(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse get(UUID id) {
        return UserMapper.toUserResponse(getEntity(id));
    }

    @Override
    public UserResponse create(UserView userView) {
        userValidator.validateForCreation(userView);
        UserEntity userEntity = userRepository.save(UserMapper.toUserEntity(userView, passwordEncoder));
        topicJmsTemplate.convertAndSend(JmsDestinations.USER_CREATED_TOPIC, userEntity.getEmail());
        return UserMapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse update(UserView userView) {
        userValidator.validateForUpdate(userView);
        UserEntity existingUser = getEntity(userView.getUuid());
        UserEntity userEntity = userRepository.save(UserMapper.toUserEntity(userView, existingUser, passwordEncoder));
        return UserMapper.toUserResponse(userEntity);
    }

    @Override
    public void delete(UUID id) {
        UserEntity userEntity = getEntity(id);
        UserResponse currentUser = getAuthenticatedUser();

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new NoAccessException("You do not have permission to delete users.");
        }

        userRepository.delete(userEntity);
    }

    @Override
    public UserResponse getAuthenticatedUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserMapper.toUserResponse(getUserByUsername(username));
    }

    @Override
    public UserResponse getWithCourses(UUID userId) {
        UserEntity userEntity = getEntity(userId);
        if (userEntity.getRole() == UserRole.STUDENT) {
            Hibernate.initialize(userEntity.getCourses());
        }
        if (userEntity.getRole() == UserRole.TEACHER) {
            Hibernate.initialize(userEntity.getOwnCourses());
        }
        return UserMapper.toUserResponse(userEntity);
    }

    @Override
    public List<CourseResponse> getCourses(UUID userId) {
        UserResponse user = getWithCourses(userId);
        if (user.getRole() == UserRole.STUDENT) {
            return user.getCourses();
        }
        if (user.getRole() == UserRole.TEACHER) {
            return user.getOwnCourses();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isAdmin(UUID uuid) {
        UserEntity userEntity = userRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User", uuid));
        return userEntity.getRole() == (UserRole.ADMIN);
    }

    @Override
    public boolean isTeacher(UUID uuid) {
        UserEntity userEntity = userRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User", uuid));
        return userEntity.getRole() == (UserRole.TEACHER);
    }

    @Override
    public boolean isStudent(UUID uuid) {
        UserEntity userEntity = userRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User", uuid));
        return userEntity.getRole() == (UserRole.STUDENT);
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", UUID.randomUUID()));
    }
}
