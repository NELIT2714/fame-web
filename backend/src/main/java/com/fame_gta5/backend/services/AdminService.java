package com.fame_gta5.backend.services;

import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fame_gta5.backend.dto.users.CreateUserDTO;
import com.fame_gta5.backend.dto.users.UpdateUserDTO;
import com.fame_gta5.backend.exceptions.HTTPException;
import com.fame_gta5.backend.repositories.PersonalDataRepository;
import com.fame_gta5.backend.repositories.AdminsRepository;
import com.fame_gta5.backend.schemas.PersonalData;
import com.fame_gta5.backend.schemas.Session;
import com.fame_gta5.backend.schemas.Admin;
import com.fame_gta5.backend.utils.mappers.UserMapper;

@Service
public class UsersService {

    private final AdminsRepository usersRepository;
    private final PersonalDataRepository personalDataRepository;
    private final SessionsService sessionsService;
    private final UserMapper userMapper;

    public UsersService(AdminsRepository usersRepository, PersonalDataRepository personalDataRepository, SessionsService sessionsService, UserMapper userMapper) {
        this.usersRepository = usersRepository;
        this.personalDataRepository = personalDataRepository;
        this.sessionsService = sessionsService;
        this.userMapper = userMapper;
    }

    public Admin getUserById(int userId) {
        return usersRepository.findById(userId).orElseThrow(() -> new HTTPException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    public Admin getUserByToken(String jwtToken) {
        Session session = sessionsService.getSession(jwtToken);
        Admin user = session.getUser();

        if (user == null) {
            throw new HTTPException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return user;
    }

    @Transactional
    public String create(CreateUserDTO userDto) {
        String username = userDto.getUsername();
        String email = userDto.getEmail();
        String password = userDto.getPassword();

        if (usersRepository.getByUsername(username).isPresent()) {
            throw new HTTPException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (personalDataRepository.getByEmail(email).isPresent()) {
            throw new HTTPException(HttpStatus.CONFLICT, "Email already exists");
        }

        PersonalData personalData = new PersonalData(email);
        personalDataRepository.save(personalData);
        Admin user = usersRepository.save(new Admin(username, personalData, BCrypt.hashpw(password, BCrypt.gensalt())));

        return sessionsService.createSession(user).getToken();
    }

    public void update(UpdateUserDTO userDTO, int userId) {
        Admin user = getUserById(userId);

        userMapper.updateUserFromDTO(userDTO, user);

        if (userDTO.getPersonalData() != null) {
            PersonalData userPersonalData = user.getPersonalData();
            userMapper.updatePersonalDataFromDTO(userDTO.getPersonalData(), userPersonalData);
        }

        usersRepository.save(user);
    }

    public void delete(int userId) {
        usersRepository.delete(getUserById(userId));
    }

    public String login(String usernameOrEmail, String password) {
        Admin user = usersRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(() -> new HTTPException(HttpStatus.UNAUTHORIZED, "Invalid username or email"));

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new HTTPException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return sessionsService.createSession(user).getToken();
    }

    public void logout(String jwtToken) {
        sessionsService.deleteSession(jwtToken);
    }
}
