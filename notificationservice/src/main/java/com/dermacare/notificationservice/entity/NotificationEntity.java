package com.dermacare.notificationservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Document(collection = "Notifications")
public class NotificationEntity {
@Id
    private String id;
	private String message;
	private String date;
	private String time;
	private Booking data;
	private String[] actions;
}
