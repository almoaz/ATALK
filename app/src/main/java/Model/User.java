package Model;

public class User
{
    String fullName, profileImage, permission;

    public User()
    {

    }

    public User(String fullName, String profileImage, String permission) {
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.permission = permission;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
