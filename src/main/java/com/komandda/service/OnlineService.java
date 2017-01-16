package com.komandda.service;

import com.komandda.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by Yevhen on 16.01.2017.
 */
@Service
public class OnlineService {
    private SortedMap<User, LocalDateTime> onlineContainer = new TreeMap<>();

    public void markOnline(User user) {
        onlineContainer.put(user, LocalDateTime.now());
        removeOfflineUsers();
    }

    public Collection<String> getOnlineUsers() {
        return onlineContainer.keySet().stream().map(User::getName).collect(Collectors.toList());
    }

    private void removeOfflineUsers() {
        LocalDateTime offlineBorder = LocalDateTime.now().minusSeconds(5);
        List<User> offlineUsers = onlineContainer.keySet().stream()
                .filter(key -> onlineContainer.get(key).isBefore(offlineBorder))
                .collect(toList());
        offlineUsers.forEach(onlineContainer::remove);
    }
}
