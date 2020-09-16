import xyz.roahaskel.utils.dbutils.BeanModel;

import java.sql.Date;
import java.sql.Timestamp;

public class Data implements BeanModel {
    private int id;
    private double num;
    private Date birth;
    private Timestamp now;
    private boolean flag;
    private long age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public double getNum() {
        return num;
    }
    public void setNum(double num) {
        this.num = num;
    }

    public Timestamp getNow() {
        return now;
    }

    public void setNow(Timestamp now) {
        this.now = now;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", num=" + num +
                ", birth=" + birth +
                ", now=" + now +
                ", flag=" + flag +
                ", age=" + age +
                '}';
    }
}
