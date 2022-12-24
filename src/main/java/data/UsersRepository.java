package data;

import model.User;

public interface UsersRepository {
    boolean save(User user);
}
