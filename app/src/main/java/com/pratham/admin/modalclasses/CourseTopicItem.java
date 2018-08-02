package com.pratham.admin.modalclasses;

public class CourseTopicItem {
    // Save name.
    private String Course;
    private String Topic;

    public CourseTopicItem(String c, String t, boolean chechBox) {
        this.Course = c;
        this.Topic = t;
    }

    @Override
    public String toString() {
        return "CourseTopicItem{" +
                "Course='" + Course + '\'' +
                ", Topic='" + Topic + '\'' +
                '}';
    }

    public String getCourse() {
        return Course;
    }

    public void setCourse(String course) {
        Course = course;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

}
