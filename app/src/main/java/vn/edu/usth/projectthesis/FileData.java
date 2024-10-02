package vn.edu.usth.projectthesis;

public class FileData {
    private int id;
    private String filename;

    public FileData(int id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }
}
