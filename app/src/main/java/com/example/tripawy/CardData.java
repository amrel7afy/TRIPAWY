package com.example.tripawy;

import java.sql.Time;
import java.util.Date;

public class CardData {

    private int cardId;
    private String tripName;
    private String startPoint;
    private String endPoint;
    private String from;
    private String to;
    private Date date;
    private Time time;


    public CardData(int cardId, String tripName, String startPoint, String endPoint, String from, String to, Date date, Time time) {
        this.cardId = cardId;
        this.tripName = tripName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }


}
