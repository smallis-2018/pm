package com.pm.dao.factory;

import com.pm.dao.datasource.Order;
import com.pm.dao.datasource.VOrderinfId;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class OrderDAO {
    private Session session;

    public OrderDAO(Session session){
        this.session = session;
    }
    public void addOrder(Order order){
        session.save(order) ;
    }
    public List<Order>getAllOrder(){
        Query<Order>query = session.createQuery("from Order",Order.class);
        return query.getResultList();
    }
    public Order getOrderById(int id){
        Query query = session.createQuery("from Order where id = ?1");
        query.setParameter(1,id);
        return (Order)query.uniqueResult();
    }
    //根据UserId获取订单
    public List<Order> getOrderByUserId(int userId){
        Query query = session.createQuery("from Order where userId = ?1 and osId != 5");
        query.setParameter(1,userId);
        List<Order> list = query.list();
        return list;
    }
    //更新订单状态
    public int updateOrderStatus(int orderId, int status){
        String sql = "update Order set osId = ?1 where id = ?2";
        Query query = session.createQuery(sql);
        query.setParameter(1,status);
        query.setParameter(2,orderId);
        return query.executeUpdate();

    };
    //更新订单状态2
    public void updateOsId(VOrderinfId vOrderinfId){
        Query query = session.createQuery(
                "update Order set osId=?1 where id=?2"
        );
        query.setParameter(1,vOrderinfId.getOsId());
        query.setParameter(2,vOrderinfId.getoId());
        query.executeUpdate();
    }

    public void frozenOrderById(Order id){
        Query query = session.createQuery("update Order set osId = 5 where id = ?1");
        query.setParameter(1,id);
    }

}
