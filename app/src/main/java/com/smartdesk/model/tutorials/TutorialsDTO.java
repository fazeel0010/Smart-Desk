package com.smartdesk.model.tutorials;

public class TutorialsDTO {

    String title;
    String videoId;



    public TutorialsDTO() {
    }

    public TutorialsDTO(String title, String videoId) {
        this.title = title;
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
