package com.pm.process;

import com.pm.dao.datasource.Integral;
import com.pm.dao.datasource.Order;
import com.pm.dao.datasource.User;
import com.pm.dao.datasource.VOrderinfId;
import com.pm.dao.factory.ManagerDAO;
import com.pm.dao.factory.OrderDAO;
import com.pm.util.HibernateUtils;
import org.hibernate.Session;

import javax.transaction.*;
import java.util.List;

public class OrderProcess {

    private Session session;
    private ManagerDAO managerDAO;
    private OrderDAO orderDAO;
    private Transaction transaction;

    public OrderProcess() {

        session = HibernateUtils.getSession();
        managerDAO = new ManagerDAO(session);
        orderDAO = new OrderDAO(session);
    }

    public List<Order> getAllOrder() {
        try {
            return orderDAO.getAllOrder();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Order getOrderByID(int id) {
        try {
            return orderDAO.getOrderById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //通过UserID获取Order对象
    public List<Order> getOrderByUserId(int userId) {
        try {
            return orderDAO.getOrderByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //取消订单
//  id	os_type
//  1	未支付
//  2	已发货
//  3	已完成
//  4	无效
//  5	已删除
//  6	已取消
//  7   未发货
    public boolean cancelOrder(int orderId) {
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            int status = orderDAO.getOrderById(orderId).getOsId();
            if (status == 3 || status == 5 || status == 6 || status == 4)

                return false;
            else {
                orderDAO.updateOrderStatus(orderId, 6);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            tx.rollback();
            System.out.println(e.getMessage());
            return false;
        }

    }

    //完成订单。

    public boolean accomplishOrder(int orderId) {
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            int status = orderDAO.getOrderById(orderId).getOsId();
            if (status != 2)

                return false;
            else {
                orderDAO.updateOrderStatus(orderId, 3);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            tx.rollback();
            System.out.println(e.getMessage());
            return false;
        }

    }

    //删除订单
    public boolean deleteOrder(int orderId) {
        org.hibernate.Transaction tx = session.beginTransaction();
        try {
            int status = orderDAO.getOrderById(orderId).getOsId();
            if (status == 1 || status == 2 || status == 7 || status == 5)

                return false;
            else {
                orderDAO.updateOrderStatus(orderId, 5);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            tx.rollback();
            System.out.println(e.getMessage());
            return false;
        }

    }

    public boolean frozenOrderById(int id) throws SystemException {
        Transaction transaction = (Transaction) session.beginTransaction();
        try {
            Order order = orderDAO.getOrderById(id);
            orderDAO.frozenOrderById(order);
            transaction.commit();
            return true;
        } catch (SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            transaction.rollback();
            System.out.println(e.getMessage());
            return false;
        }
    }

    //订单状态修改
    public boolean updateOSType(VOrderinfId vOrderinfId) {
        org.hibernate.Transaction transaction = session.beginTransaction();
        try {
            orderDAO.updateOsId(vOrderinfId);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            System.out.println(e.getMessage());
            return false;
        }
    }
    public void addOrder(Order order ) {

        org.hibernate.Transaction transaction = session.beginTransaction();
        System.out.println(order);
        orderDAO.addOrder(order);
            transaction.commit();

    }
}
