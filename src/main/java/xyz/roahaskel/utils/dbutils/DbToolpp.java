package xyz.roahaskel.utils.dbutils;
import javax.sql.DataSource;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DbToolpp implements DbabsTool{
    private List<String> fields;
    private String insSql,updSql,pmKey,tbname;
    private DataSource ds;
    private Class<?extends BeanModel> clazz;
    public String getInsSql(){
        return insSql;
    }
    public String getUpdSql(){
        return updSql;
    }
    public DbToolpp(DataSource ds, Class<?extends BeanModel> clazz){
        try {
            this.ds=ds;
            this.clazz=clazz;
            fields= getFields();
            initSql(clazz);
        }catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
    @Override
    public int insert(BeanModel... beanModels)throws SQLException{
        return executesql(false,insSql,beanModels);
    }

    @Override
    public int insert(boolean autoCommitEverySql, BeanModel... beanModels) throws SQLException{
        return executesql(autoCommitEverySql,insSql,beanModels);
    }

    @Override
    public int update(BeanModel... beanModels) throws SQLException{
        return executesql(false,updSql,beanModels);
    }

    @Override
    public int update(boolean autoCommitEverySql, BeanModel... beanModels)throws SQLException {
        return executesql(autoCommitEverySql,updSql,beanModels);
    }
    public BeanModel queryByPrimaryKey(int id) throws Exception{
        String sql="select * from "+tbname+" where "+pmKey+" = ?";

        List<BeanModel> ls = queryBySql(sql, id);
        return ls.get(0);
    }
    public List<BeanModel> queryByPrimaryKey(int ...ids) throws Exception {
        StringBuilder strb=new StringBuilder("select * from "+tbname+" where "+pmKey+" in (");
        for (int i : ids) {
            strb.append(i);
            strb.append(',');
        }
        strb.replace(strb.length()-1,strb.length()-1,")");
        queryBySql(strb.toString());
        return null;
    }
    public List<BeanModel> queryBySql(String sql,Object ...objs) throws Exception{
        ResultSet res = exeSqlgetRes(sql, objs);
        List<BeanModel> ls=new ArrayList<>();
        BeanModel m;
        Map<String,Object> map;
        while (res.next()){
            m=clazz.newInstance();
            map=resToMap(res);
            encBean(map,m);
            ls.add(m);
        }
        return ls;
    }
    //获取传入对象的属性值，能和getFields得到的字段对应
    public Object[] getValues(BeanModel obj){
        Object[] objs=new Object[fields.size()];
        int index=0;
        for (String f : fields) {
            try{
                objs[index++]= new PropertyDescriptor(f, obj.getClass()).getReadMethod().invoke(obj);
            }catch (Exception e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        if(objs[fields.size()-1].equals(0)){
            objs[fields.size()-1]=null;
        }
        return objs;
    }
    //执行sql提供事务控制参数
    private int executesql(boolean flag,String sql,BeanModel ...beanModels) throws SQLException{
        Connection con=ds.getConnection();
        if(!flag){
            con.setAutoCommit(false);
        }
        PreparedStatement ps = con.prepareStatement(sql);
        try {
            int res=execute(ps,beanModels);
            con.commit();
            return res;
        }catch (SQLException e){
            e.printStackTrace();
            if(!flag){
                con.rollback();
            }
            throw e;
        }finally {
            if(ps!=null){
                ps.close();
            }
        }
    }
    //执行sql查询语句
    private ResultSet exeSqlgetRes(String sql,Object ...objs) throws SQLException{
        PreparedStatement ps = ds.getConnection().prepareStatement(sql);
        for (int i = 0; i < objs.length; i++) {
            ps.setObject(i+1,objs[i]);
        }
        return ps.executeQuery();
    }
    //获取BeanModel的各个字段
    private List<String> getFields() throws Exception {
        int index=0;
        boolean flag=false;
        List<String> list=new ArrayList<String>();
        String str;
        pmKey=(String)clazz.getMethod("primaryKey").invoke(clazz.newInstance());
        for (PropertyDescriptor pro : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            if(pro.getWriteMethod()!=null && pro.getWriteMethod()!=null){
                str=pro.getName();
                list.add(pro.getName());
                if(!flag){
                    if(str.equals(pmKey)){
                        flag=true;
                        index--;
                    }
                    index++;
                }
            }
        }
        if(index>=list.size()){
            throw new Exception("The primary key set in the entity class is inconsistent with the corresponding table");
        }
        if (index != list.size()-1) {
            str = list.get(index);
            list.set(index, list.get(list.size() - 1));
            list.set(list.size() - 1, str);
        }
        return list;
    }
    //sql执行
    private int execute(PreparedStatement ps, BeanModel[] beanModels) throws SQLException{
        int count=0;
        Object[] pams;
        for(BeanModel md: beanModels){
            pams=getValues(md);
            for(int i=0;i<pams.length;i++){
                ps.setObject(i+1,pams[i]);
            }
            count+=ps.executeUpdate();
        }
        return count;
    }
    //初始化sql语句字符串
    private void initSql(Class<?extends BeanModel> clazz) throws Exception{
        tbname= (String)clazz.getMethod("tableName").invoke(clazz.newInstance());
        insSql=String.format("insert into %s (",tbname);
        String values="";
        for(int i=0;i<fields.size()-1;i++){
            insSql+=fields.get(i).toLowerCase()+",";
            values+="?,";
        }
        insSql+=fields.get(fields.size()-1).toLowerCase();
        values+="?";
        insSql=insSql+")values("+values+")";
        ///////
        updSql=String.format("update %s set ",tbname);
        for(int i=0;i<fields.size()-1;i++){
            updSql+=fields.get(i).toLowerCase()+"=?,";
        }
        updSql=updSql.substring(0,updSql.length()-1);
        updSql+=" where "+fields.get(fields.size()-1).toLowerCase()+"=?";
    }
    //封装BeanModel,提供一个map，map存储了对象属性，和属性值的键值对
    public void encBean(Map<String,Object> dict,BeanModel obj) throws Exception{
        if(obj.getClass()!=clazz){
            throw new Exception("obj type error,must instance of "+clazz.getName());
        }
        for (String field : dict.keySet()) {
            Object o=dict.get(field);
            Method method = new PropertyDescriptor(field, obj.getClass()).getWriteMethod();
            try{
                method.invoke(obj,o);
            }catch (Exception e){
                if(o.toString().matches("\\d+")){
                    try{
                        method.invoke(obj,new Long(o.toString()));
                    }catch (Exception ex){
                        method.invoke(obj,new Integer(o.toString()));
                    }
                }else if(o.toString().matches("\\d+\\.\\d+")){
                    try{
                        method.invoke(obj,new Double(o.toString()));
                    }catch (Exception ex){
                        method.invoke(obj,new Float(o.toString()));
                    }
                }else if(o.toString().matches("[1-9]\\d{0,4}-\\d{1,2}-\\d{1,2}")){
                    Date d = new SimpleDateFormat("yyyy-MM-dd").parse(o.toString());
                    method.invoke(obj,new java.sql.Date(d.getTime()));
                }else if(o.toString().matches("[1-9]\\d{0,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d+")||
                        o.toString().matches("[1-9]\\d{0,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")){
                    Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o.toString());
                    method.invoke(obj,new java.sql.Timestamp(d.getTime()));
                }else if(o.toString().equals("true")|| o.toString().equals("false")) {
                    method.invoke(obj, new Boolean(o.toString()));
                }
            }
        }
    }
    private Map<String,Object> resToMap(ResultSet res) throws SQLException {
        HashMap<String,Object> map=new HashMap<>();
        for(String str:fields){
            Object obj = res.getObject(str);
            if(obj!=null){
                map.put(str,obj);
            }
        }
        return map;
    }
}
