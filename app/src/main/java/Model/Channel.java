package Model;

public class Channel {

    String channelName,channelProfileImage, time, date;

    public Channel()
    {

    }

    public Channel(String channelName, String channelProfileImage, String time, String date) {
        this.channelName = channelName;
        this.channelProfileImage = channelProfileImage;
        this.time = time;
        this.date = date;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelProfileImage() {
        return channelProfileImage;
    }

    public void setChannelProfileImage(String channelProfileImage) {
        this.channelProfileImage = channelProfileImage;
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
