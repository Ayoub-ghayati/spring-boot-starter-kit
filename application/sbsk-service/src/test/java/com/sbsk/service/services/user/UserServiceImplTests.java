package com.sbsk.service.services.user;

import com.sbsk.dtos.user.UserRequestDto;
import com.sbsk.dtos.user.UserResponseDto;
import com.sbsk.persistence.entities.user.UserEntity;
import com.sbsk.persistence.repositories.UserRepository;
import com.sbsk.service.converters.user.UserConverter;
import com.sbsk.service.validators.UserValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class UserServiceImplTests {

    @Mock UserValidator userValidator;
    @Mock UserConverter userConverter;
    @Mock UserRepository userRepository;
    @Mock UserRequestDto userRequestDto;
    @Mock UserEntity userEntity;
    @Mock UserResponseDto userResponseDto;
    @InjectMocks UserServiceImpl userServiceImpl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllUser_shouldFollowTheRightFlow() {
        List<UserEntity> userEntities = new ArrayList<>();
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(userEntities);
        when(userConverter.convertUserEntitiesToUserResponseDtos(userEntities)).thenReturn(userResponseDtos);

        userServiceImpl.getAllUsers();

        verify(userRepository, times(1)).findAll();
        verify(userConverter, times(1)).convertUserEntitiesToUserResponseDtos(userEntities);
    }

    @Test
    public void getUser_shouldFollowTheRightFlow_whenEverythingIsValidatedCorrectly() {
        Long id = new Long(0);
        when(userValidator.userExists(id)).thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userConverter.convertUserEntityToUserResponseDto(userEntity)).thenReturn(userResponseDto);

        userServiceImpl.getUser(id);

        verify(userValidator, times(1)).userExists(id);
        verify(userRepository, times(1)).findById(id);
        verify(userConverter, times(1)).convertUserEntityToUserResponseDto(userEntity);
    }

    @Test
    public void getUser_shouldRaiseError_whenUserDoesNotExist() {
        Long id = new Long(0);
        when(userValidator.userExists(id)).thenReturn(false);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userConverter.convertUserEntityToUserResponseDto(userEntity)).thenReturn(userResponseDto);

        try {
            userServiceImpl.getUser(id);
            Assert.fail("Exception should have thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "User does not exist");
        }

        verify(userValidator, times(1)).userExists(id);
        verify(userRepository, times(0)).findById(id);
        verify(userConverter, times(0)).convertUserEntityToUserResponseDto(userEntity);
    }

    @Test
    public void createUser_shouldFollowTheRightFlow_whenEverythingIsValidatedCorrectly() {
        when(userValidator.validateUser(userRequestDto)).thenReturn(true);
        when(userConverter.convertUserRequestDtoToUserEntity(userRequestDto)).thenReturn(userEntity);
        when(userConverter.convertUserEntityToUserResponseDto(userEntity)).thenReturn(userResponseDto);

        userServiceImpl.createUser(userRequestDto);

        verify(userValidator, times(1)).validateUser(userRequestDto);
        verify(userConverter, times(1)).convertUserRequestDtoToUserEntity(userRequestDto);
        verify(userRepository, times(1)).save(userEntity);
        verify(userConverter, times(1)).convertUserEntityToUserResponseDto(userEntity);
    }

    @Test
    public void createUser_shouldRaiseError_whenSomethingIsNotValidatedCorrectly() {
        when(userValidator.validateUser(userRequestDto)).thenReturn(false);
        when(userConverter.convertUserRequestDtoToUserEntity(userRequestDto)).thenReturn(userEntity);
        when(userConverter.convertUserEntityToUserResponseDto(userEntity)).thenReturn(userResponseDto);

        try {
            userServiceImpl.createUser(userRequestDto);
            fail("Exception should have thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Invalid input parameters");
        }

        verify(userValidator, times(1)).validateUser(userRequestDto);
        verify(userConverter, times(0)).convertUserRequestDtoToUserEntity(userRequestDto);
        verify(userRepository, times(0)).save(userEntity);
        verify(userConverter, times(0)).convertUserEntityToUserResponseDto(userEntity);
    }

    @Test
    public void updateUser_shouldFollowTheRightFlow_whenEverythingIsValidatedCorrectly() {
        when(userEntity.getId()).thenReturn(new Long(1));
        when(userValidator.userExists(userEntity.getId())).thenReturn(true);
        when(userValidator.validateUser(userRequestDto)).thenReturn(true);
        when(userConverter.convertUserRequestDtoToUserEntity(userRequestDto)).thenReturn(userEntity);
        when(userConverter.convertUserEntityToUserResponseDto(userEntity)).thenReturn(userResponseDto);

        userServiceImpl.updateUser(userEntity.getId(), userRequestDto);

        verify(userValidator,times(1)).userExists(userEntity.getId());
        verify(userValidator, times(1)).validateUser(userRequestDto);
        verify(userConverter, times(1)).convertUserRequestDtoToUserEntity(userRequestDto);
        verify(userEntity, times(1)).setId(anyLong());
        verify(userRepository, times(1)).save(userEntity);
        verify(userConverter, times(1)).convertUserEntityToUserResponseDto(userEntity);
    }

    @Test
    public void updateUser_shouldRaiseError_whenSomethingIsNotValidatedCorrectly() {
        when(userEntity.getId()).thenReturn(new Long(1));
        when(userValidator.userExists(userEntity.getId())).thenReturn(true);
        when(userValidator.validateUser(userRequestDto)).thenReturn(false);
        when(userConverter.convertUserRequestDtoToUserEntity(userRequestDto)).thenReturn(userEntity);
        when(userConverter.convertUserEntityToUserResponseDto(userEntity)).thenReturn(userResponseDto);

        try {
            userServiceImpl.updateUser(userEntity.getId(), userRequestDto);
            fail("Exception should have thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Invalid input parameters");
        }

        verify(userValidator, times(1)).userExists(userEntity.getId());
        verify(userValidator, times(1)).validateUser(userRequestDto);
        verify(userConverter, times(0)).convertUserRequestDtoToUserEntity(userRequestDto);
        verify(userEntity, times(0)).setId(anyLong());
        verify(userRepository, times(0)).save(userEntity);
        verify(userConverter, times(0)).convertUserEntityToUserResponseDto(userEntity);
    }

    @Test
    public void updateUser_shouldRaiseError_whenUserDoesNotExist() {
        when(userEntity.getId()).thenReturn(new Long(1));
        when(userValidator.userExists(userEntity.getId())).thenReturn(false);
        when(userValidator.validateUser(userRequestDto)).thenReturn(true);
        when(userConverter.convertUserRequestDtoToUserEntity(userRequestDto)).thenReturn(userEntity);
        when(userConverter.convertUserEntityToUserResponseDto(userEntity)).thenReturn(userResponseDto);

        try {
            userServiceImpl.updateUser(userEntity.getId(), userRequestDto);
            fail("Exception should have thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "User does not exist");
        }

        verify(userValidator, times(1)).userExists(userEntity.getId());
        verify(userValidator, times(0)).validateUser(userRequestDto);
        verify(userConverter, times(0)).convertUserRequestDtoToUserEntity(userRequestDto);
        verify(userEntity, times(0)).setId(anyLong());
        verify(userRepository, times(0)).save(userEntity);
        verify(userConverter, times(0)).convertUserEntityToUserResponseDto(userEntity);
    }

    @Test
    public void deleteUser_shouldFollowTheRightFlow_whenEverythingIsValidatedCorrectly() {
        Long id = new Long(0);

        when(userValidator.userExists(id)).thenReturn(true);

        userServiceImpl.deleteUser(id);

        verify(userValidator, times(1)).userExists(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteUser_shouldRaiseError_whenUserDoesNotExist() {
        Long id = new Long(0);

        when(userValidator.userExists(id)).thenReturn(false);

        try {
            userServiceImpl.deleteUser(id);
            fail("Exception should have thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "User does not exist");
        }

        verify(userValidator, times(1)).userExists(id);
        verify(userRepository, times(0)).deleteById(id);
    }
}
