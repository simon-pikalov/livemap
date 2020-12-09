package objects;


import com.google.android.gms.maps.model.LatLng;

public class Bookmark  {
    private String name;
    private boolean isPublic;
    private int id;
    private static int lastFreeId;
    private LatLng cord;

    public Bookmark(){}
    public Bookmark(String name, double lat, double lng, boolean isPublic) {
        cord = new LatLng(lat,lng);
        this.name = name;
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

    public LatLng getCord() {
        return cord;
    }

    public void setCord(LatLng cord) {
        this.cord = cord;
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
