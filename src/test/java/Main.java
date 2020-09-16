import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;
import xyz.roahaskel.utils.dbutils.DbToolpp;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;

public class Main {
    @Test
    public void t1() throws Exception{
        System.out.println("fdsafsaf");
        ComboPooledDataSource ds = new ComboPooledDataSource();
        DbToolpp dpp = new DbToolpp(ds, Data.class);

        /*Data d=new Data();
        d.setNow(Timestamp.valueOf("2018-12-13 15:18:22"));
        d.setBirth(Date.valueOf("2020-8-12"));
        d.setNum(1.04e30);
        d.setFlag(true);
        d.setAge(3243);
        d.setId(999);
        dpp.insert(d);*/

        Data bm = (Data)dpp.queryByPrimaryKey(2);
        System.out.println(bm);
    }
    public static void testt() throws Exception{
        Method method = User.class.getMethod("setEmail", String.class);
        User u=new User();
        method.invoke(u,"fdsa");
        System.out.println(u.getEmail());
    }
}

