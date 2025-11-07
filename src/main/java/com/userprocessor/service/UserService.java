package com.userprocessor.service;

import com.userprocessor.dto.ProcessingResult;
import com.userprocessor.dto.UserDto;
import com.userprocessor.dto.UserResponseDto;
import com.userprocessor.entity.User;
import com.userprocessor.enums.FileType;
import com.userprocessor.enums.OutputFormat;
import com.userprocessor.exception.FileProcessingException;
import com.userprocessor.factory.FileProcessorFactory;
import com.userprocessor.processor.FileProcessor;
import com.userprocessor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FileProcessorFactory fileProcessorFactory;

    @Autowired
    public UserService(UserRepository userRepository, FileProcessorFactory fileProcessorFactory) {
        this.userRepository = userRepository;
        this.fileProcessorFactory = fileProcessorFactory;
    }

    public ProcessingResult processFileUpload(MultipartFile file, String fileTypeString) throws Exception {
        FileType fileType = FileType.fromString(fileTypeString);
        FileProcessor processor = fileProcessorFactory.getProcessor(fileType);

        List<UserDto> userDtos = processor.processFile(file);
        
        ProcessingResult result = new ProcessingResult();
        result.setTotalRecords(userDtos.size());

        int processedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        for (UserDto userDto : userDtos) {
            try {
                if (userRepository.existsByEmail(userDto.getEmail())) {
                    result.addWarning("User with email " + userDto.getEmail() + " already exists - skipped");
                    skippedCount++;
                    continue;
                }

                User user = new User(userDto.getName(), userDto.getEmail(), fileType.getValue());
                User savedUser = userRepository.save(user);
                result.addProcessedUser(new UserResponseDto(savedUser));
                processedCount++;

            } catch (Exception e) {
                result.addError("Error processing user " + userDto.getEmail() + ": " + e.getMessage());
                errorCount++;
            }
        }

        result.setProcessedRecords(processedCount);
        result.setSkippedRecords(skippedCount);
        result.setErrorRecords(errorCount);

        return result;
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserResponseDto::new);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by("createdAt").descending());
        return users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponseDto::new);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersBySource(String source) {
        List<User> users = userRepository.findBySource(source);
        return users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getUsersBySource(String source, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findBySource(source, pageable);
        return users.map(UserResponseDto::new);
    }

    @Transactional(readOnly = true)
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long getUserCountBySource(String source) {
        return userRepository.countBySource(source);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
