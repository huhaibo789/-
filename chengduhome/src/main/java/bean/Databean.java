package bean;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Databean {
    boolean isChoose;
    String  title;

    public Databean() {

    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Databean{" +
                "isChoose=" + isChoose +
                ", title='" + title + '\'' +
                '}';
    }
}
