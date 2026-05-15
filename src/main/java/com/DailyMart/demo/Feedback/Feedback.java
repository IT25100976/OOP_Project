package com.DailyMart.demo.Feedback;

public class Feedback {
    private String name;
    private String email;
    private String rating;   // e.g. "5"
    private String message;

    public Feedback() {}
    public Feedback(String name, String email, String rating, String message) {
        this.name = name;
        this.email = email;
        this.rating = rating;
        this.message = message;
    }

    public String getName()    { return name; }
    public void setName(String name)       { this.name = name; }

    public String getEmail()   { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getRating()  { return rating; }
    public void setRating(String rating)   { this.rating = rating; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
