package net.baronofclubs.botofclubs;

import java.util.*;

/**
 * DESCRIPTION: Class for handling Permissions and permission checks
 */

public class Permissions {

    public enum UserLevels {
        DEV,
        OWNER,
        ADMIN,
        MANAGER,
        SUPER,
        GENERIC,
        LIMITED,
        BANNED
    }

    public class UserPermissions {

        List<UserLevels> permList;

        public UserPermissions() {
            permList = defaultPerms();
        }

        public boolean contains(UserLevels ul) {
            return permList.contains(ul);
        }

        private List<UserLevels> defaultPerms() {
            List<UserLevels> list = new ArrayList<>();
            list.add(UserLevels.GENERIC);
            return list;
        }
    }

}
