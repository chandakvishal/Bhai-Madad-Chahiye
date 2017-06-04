package com.bhaimadadchahiye.club.Answers;

public class Answer {

    public String email;
    public String title;

    /**
     *
     * @param email email of the user who answered the question
     * @param title title of the question answered
     */
    Answer(String email, String title) {
        this.title = title;
        this.email = email;
    }
}
