package Model;

public class Group {

    String groupName,groupProfileImage,time , date;

    public Group()
    {

    }

    public Group(String groupName, String groupProfileImage, String time, String date) {
        this.groupName = groupName;
        this.groupProfileImage = groupProfileImage;
        this.time = time;
        this.date = date;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupProfileImage() {
        return groupProfileImage;
    }

    public void setGroupProfileImage(String groupProfileImage) {
        this.groupProfileImage = groupProfileImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
