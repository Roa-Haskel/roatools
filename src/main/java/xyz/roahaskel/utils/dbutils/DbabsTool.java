package xyz.roahaskel.utils.dbutils;

import java.sql.SQLException;
import java.util.Map;

public interface DbabsTool {
    int insert(BeanModel... beanModels)throws SQLException;
    int insert(boolean autoCommitEverySql, BeanModel... beanModels) throws SQLException;
    int update(BeanModel... beanModels)throws SQLException;
    int update(boolean autoCommitEverySql, BeanModel... beanModels) throws SQLException;
    void encBean(Map<String, Object> dict, BeanModel obj) throws Exception;
}
