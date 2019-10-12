package com.pm.ui.user;
import com.pm.dao.datasource.VOrderinfId;
import com.pm.process.OrderProcess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UOdetail extends JFrame {
    private VOrderinfId vo;
    private UOrder Uorder;
    private JPanel JPTable,JPBut;
    private JLabel JLNum,JLUserName,JLGoodsName,JLPrice,JLCTime,JLETime,JLStatus;
    private JTextField JTNum,JTUserName,JTGoodsName,JTPrice,JTCTime,JTETime,JTStatus;
    private JButton butDelete,butCancel,butAccomplish;
    public UOdetail(VOrderinfId vo,UOrder Uorder){

        this.Uorder=Uorder;
        this.vo = vo;
        this.setTitle("订单详情");
        this.setLayout(new BorderLayout());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setSize(320,340);
        JPTable = new JPanel();
        JPTable.setLayout(new GridLayout(7,2));

        JPTable.setVisible(true);
        JPTable.setSize(240,300);
        JLNum=new JLabel("订单号",JLabel.CENTER);
        JTNum= new JTextField(vo.getOrderId());
        JTNum.setEditable(false);
        JLUserName=new JLabel("用户名",JLabel.CENTER);

        JTUserName=new JTextField(vo.getUserName());
        JTUserName.setEnabled(false);
        JLGoodsName=new JLabel("商品名",JLabel.CENTER);
        JTGoodsName=new JTextField(vo.getGoodsName());
        JTGoodsName.setEnabled(false);
        JLPrice=new JLabel("兑换价格",JLabel.CENTER);
        JTPrice = new JTextField(vo.getGoodsPrice()+"");
        JTPrice.setEnabled(false);
        JLCTime=new JLabel("创建时间",JLabel.CENTER);

        JTCTime=new JTextField(vo.getCreateDate()+"");
        JTCTime.setEnabled(false);

        JLETime=new JLabel("完成时间",JLabel.CENTER);

        JTETime=new JTextField(vo.getCompDate()+"");
        JTETime.setEnabled(false);
        JLStatus=new JLabel("订单状态",JLabel.CENTER);
        JTStatus=new JTextField(vo.getOsType());
        JTStatus.setEnabled(false);
        JPTable.add(JLNum);

        JPTable.add(JTNum);
        JPTable.add(JLUserName);
        JPTable.add(JTUserName);
        JPTable.add(JLGoodsName);
        JPTable.add(JTGoodsName);
        JPTable.add(JLPrice);
        JPTable.add(JTPrice);
        JPTable.add(JLCTime);
        JPTable.add(JTCTime);
        JPTable.add(JLETime);
        JPTable.add(JTETime);

        JPTable.add(JLStatus);
        JPTable.add(JTStatus);
        butAccomplish = new JButton("完成");
        butCancel = new JButton("取消");

        butDelete = new JButton("删除");
        JPBut = new JPanel();
        JPBut.setSize(100,40);
        JPBut.setLayout(new GridLayout(1,3));
        JPBut.add(butAccomplish);
        JPBut.add(butCancel);
        JPBut.add(butDelete);
        this.add(JPTable,BorderLayout.CENTER);
        this.add(JPBut,BorderLayout.SOUTH);

        this.setLocationRelativeTo(null);
    }
    public void go(){
        this.setVisible(true);//设置dialog显示
        butCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //UOrder.this.dispose();
                try {
                    int ID = Integer.valueOf(vo.getoId());
                    OrderProcess op = new OrderProcess();
                    if (op.cancelOrder(ID) == true) {
                        Uorder.showData();


                        JOptionPane.showMessageDialog(null, "操作成功");
                        UOdetail.this.dispose();
                    } else {


                        JOptionPane.showMessageDialog(null, "操作失败该订单不可取消");
                    }
                } catch (Exception el) {
                    JOptionPane.showMessageDialog(null,
                            "请正确操作",
                            "警告",
                            JOptionPane.ERROR_MESSAGE);
                }


            }
        });

        butDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //UOrder.this.dispose();
                try {


                    int ID = Integer.valueOf(vo.getoId());
                    OrderProcess op = new OrderProcess();
                    if (op.deleteOrder(ID) == true) {
                        Uorder.showData();
                        JOptionPane.showMessageDialog(null, "操作成功该订单已删除");
                        UOdetail.this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "操作失败该订单不可删除");
                    }
                } catch (Exception el) {
                    el.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "请正确操作",
                            "警告",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        butAccomplish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //UOrder.this.dispose();
                try {


                    int ID = Integer.valueOf(vo.getoId());
                    OrderProcess op = new OrderProcess();
                    if (op.accomplishOrder(ID) == true) {
                        Uorder.showData();
                        JOptionPane.showMessageDialog(null, "操作成功该订单已完成");
                        UOdetail.this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "操作失败该订单不可完成");
                    }
                } catch (Exception el) {
                    el.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "请正确操作",
                            "警告",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }
}

