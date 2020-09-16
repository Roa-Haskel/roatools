import xyz.roahaskel.utils.dbutils.BeanModel;

public class User implements BeanModel {
    private int id;
    private String email,passwd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String primaryKey() {
        return "id";
    }

    @Override
    public String tableName() {
        return "user";
    }
}
