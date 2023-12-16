package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.UserRegistrationDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Tenant;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.enums.Role;
import bg.tuvarna.sit.wms.exceptions.RegistrationException;
import bg.tuvarna.sit.wms.exceptions.UserPersistenceException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class for handling user registration.
 * This class manages the process of registering a new user, including input validation,
 * password hashing, and persisting user data.
 */
public class UserService {

  private final UserDao userDao;
  private final PasswordHashingService passwordHashingService;

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);

  public UserService(UserDao userDao, PasswordHashingService passwordHashingService) {

    this.userDao = userDao;
    this.passwordHashingService = passwordHashingService;
  }

  /**
   * Registers a new user based on the provided registration data.
   *
   * @param registrationDto Data Transfer Object containing user registration details.
   * @throws RegistrationException if there is a problem with user registration, such as invalid input or persistence errors.
   */
  public void registerUser(UserRegistrationDto registrationDto) throws RegistrationException {

    User user = createUserFromDto(registrationDto);
    setUserPassword(user, registrationDto.getPassword());
    saveUser(user);
  }

  /**
   * Creates a User entity from a UserRegistrationDto.
   *
   * @param dto User registration data transfer object.
   * @return A User entity populated with data from the DTO.
   * @throws RegistrationException if the provided role is invalid.
   */
  private User createUserFromDto(UserRegistrationDto dto) throws RegistrationException {

    Optional<User> userOptional = getUserBasedOnRole(dto.getRole());

    if (userOptional.isEmpty()) {
      String errorMessage = "Invalid role provided for user registration.";
      LOGGER.error(errorMessage);
      throw new RegistrationException(errorMessage);
    }

    User user = userOptional.get();
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.setRole(Role.valueOf(dto.getRole()));

    return user;
  }

  /**
   * Hashes a password using a secure cryptographic algorithm.
   *
   * @param password The password to hash.
   * @return The hashed password.
   * @throws RegistrationException if there is an error during password hashing.
   */
  private String hashPassword(String password) throws RegistrationException {

    try {
      return passwordHashingService.generateStrongPasswordHash(password);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      String errorMessage = "Error hashing password for user registration.";
      LOGGER.error(errorMessage, e);
      throw new RegistrationException(errorMessage, e);
    }
  }

  /**
   * Sets the user's password after hashing it.
   *
   * @param user     The user whose password is to be set.
   * @param password The user's plaintext password.
   * @throws RegistrationException if there is an error during password hashing.
   */
  private void setUserPassword(User user, String password) throws RegistrationException {

    String hashedPassword = hashPassword(password);
    user.setPassword(hashedPassword);
  }

  /**
   * Saves the user entity to the database.
   *
   * @param user The user entity to save.
   * @throws RegistrationException if there is an error while persisting the user.
   */
  private void saveUser(User user) throws RegistrationException {

    try {
      userDao.saveUser(user);
      LOGGER.info("User saved successfully: " + user.getEmail());
    } catch (UserPersistenceException e) {
      String errorMessage = "Error persisting user during registration.";
      LOGGER.error(errorMessage, e);
      throw new RegistrationException(errorMessage, e);
    }
  }

  /**
   * Retrieves a user entity based on the specified role.
   *
   * @param role The role of the user.
   * @return An Optional containing the User entity if the role is valid, otherwise an empty Optional.
   */
  private Optional<User> getUserBasedOnRole(String role) {

    return switch (role.toUpperCase()) {
      case "OWNER" -> Optional.of(new Owner());
      case "AGENT" -> Optional.of(new Agent());
      case "TENANT" -> Optional.of(new Tenant());
      default -> Optional.empty();
    };
  }
}