package com.pratham.admin.modalclasses;

public class CourseTopicItem {
    // Save name.
    private String CourseIDs;
    private String Course;
    private String TopicIDs;
    private String Topic;
    private Boolean isSelected;

    public CourseTopicItem(String cid, String c, String tid, String t, boolean isSelected) {
        this.CourseIDs = cid;
        this.Course = c;
        this.TopicIDs = tid;
        this.Topic = t;
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return "CourseTopicItem{" +
                "CourseIDs='" + CourseIDs + '\'' +
                ", Course='" + Course + '\'' +
                ", TopicIDs='" + TopicIDs + '\'' +
                ", Topic='" + Topic + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    public String getCourseIDs() {
        return CourseIDs;
    }

    public void setCourseIDs(String courseIDs) {
        CourseIDs = courseIDs;
    }

    public String getCourse() {
        return Course;
    }

    public void setCourse(String course) {
        Course = course;
    }

    public String getTopicIDs() {
        return TopicIDs;
    }

    public void setTopicIDs(String topicIDs) {
        TopicIDs = topicIDs;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
