package Model;

public class Note {

    String note , time , date;

    public Note()
    {

    }

    public Note(String note, String time, String date) {
        this.note = note;
        this.time = time;
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

