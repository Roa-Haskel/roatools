package xyz.roahaskel.utils.dbutils;

public interface BeanModel {
    //获取实体类对应表的主键id，默认列名为id，如果不是请重写此方法
    default String primaryKey(){
        return "id";
    }
    //获取实体类对应表的表明，默认为实体类的类名，如果不是，请重写此方法
    default String tableName(){
        return this.getClass().getSimpleName().toLowerCase();
    }
}
