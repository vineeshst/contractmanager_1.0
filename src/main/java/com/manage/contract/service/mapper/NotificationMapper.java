package com.manage.contract.service.mapper;

import com.manage.contract.domain.Notification;
import com.manage.contract.service.dto.NotificationsDTO;

public class NotificationMapper {

    public static NotificationsDTO notificationsToNotificationsDTO(Notification notification) {
        NotificationsDTO notificationsDTO = new NotificationsDTO();

        notificationsDTO.setId(notification.getId());
        notificationsDTO.setType(notification.getType());
        notificationsDTO.setMessage(notification.getMessage());
        notificationsDTO.setRead(notification.isRead());
        notificationsDTO.setActionUser(notification.getActionUser());
        notificationsDTO.setDate(notification.getCreatedDate());
        notificationsDTO.setEntityName(notification.getEntityName());
        notificationsDTO.setEntityId(notification.getEntityId());
        notificationsDTO.setEntityType(notification.getEntityType());
        return notificationsDTO;
    }
}
