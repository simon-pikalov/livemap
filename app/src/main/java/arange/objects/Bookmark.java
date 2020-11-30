package arange.objects;

public class Bookmark {
    private String name;
    private double lat;
    private double lng;
    private boolean isPublic;
    private int id;
    private static int lastFreeId;


    public Bookmark(String name, double lat, double lng, boolean isPublic) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.isPublic = isPublic;
        this.id = lastFreeId;
        lastFreeId++;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }


    public int getId() {
        return id;
    }

}
