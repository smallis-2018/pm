package com.pm.ui.user;

import com.pm.dao.datasource.Goods;
import com.pm.dao.datasource.Integral;
import com.pm.dao.datasource.Order;
import com.pm.dao.datasource.Point;
import com.pm.dao.datasource.User;
import com.pm.process.GoodsProcess;
import com.pm.process.IntegralProcess;
import com.pm.process.OrderProcess;
import com.pm.process.*;
import com.pm.util.Page;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Blob;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 管理商品的UI
 */
public class UserMain {
    private User user;
    private IntegralProcess integralProcess = new IntegralProcess();
    private OrderProcess orderProcess = new OrderProcess();
    private GoodsProcess goodsProcess = new GoodsProcess();
    private PointProcess pointProcess = new PointProcess();
    private JPanel jp1;
    private JPanel jp2;
    private JPanel jp3;
    private JPanel jp4;

    private JTable table;

    /*    private JButton addGoodsButton;
        private JButton editGoodsButton;
        private JButton deleGoodsButton;*/
    private JButton personalButton;
    private JButton signIn;
    private JButton fristPageButton;
    private JButton nextPageButton;
    private JButton previousPageButton;
    private JButton lastPageButton;
    private JLabel jLabel;
    private JFrame mainFrame;
    private JTextField jTextField;
    private JButton jButton;
    private int currentPage;
    private final int firstPage = 1;
    private int lastPage;
    private int tableRows;
    private List<Goods> goodsList;

    public UserMain(User user) {

        personalButton = new JButton("个人中心");
        jTextField = new JTextField();
        jTextField.setPreferredSize(new Dimension (200,30));
        jButton = new JButton("兑换");
        this.user = user;
        signIn = new JButton("签到");
        fristPageButton = new JButton("首页");
        previousPageButton = new JButton("上一页");
        nextPageButton = new JButton("下一页");
        lastPageButton = new JButton("末页");
        jLabel = new JLabel("剩余积分: "+pointProcess.getPointByUserId(user.getId()).getPointValue());
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel(new GridLayout(1, 5,20,10));
        jp4 = new JPanel();

        //设置表格
        String[] columnNames = {"ID", "商品编号", "商品名", "兑换价格", "商品图片"};

        table = new JTable(){
            //设置禁止编辑单元格
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        //设置自定义表格模型
        table.setModel(new CustomModel(null, columnNames));

        //table.setFillsViewportHeight(true);
        //设置行高
        table.setRowHeight(100);
        //列排序功能
        //table.setAutoCreateRowSorter(true);
        //设置表格列不可移动
        table.getTableHeader().setReorderingAllowed(false);
        //行单选
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //表格载入滑动面板
        JScrollPane scrollPane = new JScrollPane(table);


        //加载组件
        jp1.add(personalButton);
        jp2.add(scrollPane);
        jp3.add(signIn);
        jp3.add(fristPageButton);
        jp3.add(previousPageButton);
        jp3.add(nextPageButton);
        jp3.add(lastPageButton);
        jp4.add(jLabel);
        jp4.add(jTextField);
        jp4.add(jButton);


        //设置主面板属性
        mainFrame = new JFrame("商品列表   用户:"+user.getUserName());
        mainFrame.setSize(500,580);
        //使用流式布局
        mainFrame.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        mainFrame.setResizable(false);
        //居中显示
        mainFrame.setLocationRelativeTo(null);

        //加载组件
        mainFrame.add(jp1);
        mainFrame.add(jp2);
        mainFrame.add(jp3);
        mainFrame.add(jp4);
        mainFrame.setVisible(true);
    }

    private int getCurrentPage() {
        return currentPage;
    }

    private void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private int getFirstPage() {
        return firstPage;
    }

    private int getLastPage() {
        return lastPage;
    }

    public void go() {
        initGoodsList();

        initPageNumber();

        showData();

        //首页按钮的监听器，点击后设置首页并刷新table
        fristPageButton.addActionListener(e -> {
            setCurrentPage(firstPage);
            showData();
        });
        //首页按钮的监听器，点击后设置首页并刷新table
        jButton.addActionListener(e -> {

            String id = jTextField.getText();
            Order order = new Order();
            order.setCreateDate(new Date());
            order.setCompDate(null);
            order.setUserId(user.getId());
            order.setOsId(7);
            order.setId(new Random().nextInt(100000));
            order.setgId(Integer.parseInt(id));
            Goods goods = goodsProcess.getGoods(Integer.parseInt(id));

            //生成订单编号为：日期＋时间＋该商品编号后四位
            String goodsId = goods.getGoodsId();

            Date d = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            String cc = sdf.format(d);
            order.setOrderId(cc + goodsId.substring(goodsId.length() - 4));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    orderProcess.addOrder(order);
                }
            }).start();

            pointProcess.addpoints(-goods.getGoodsPrice(),user.getId());
            pointProcess = new PointProcess();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jLabel.setText("剩余积分:"+pointProcess.getPointByUserId(user.getId()).getPointValue());
                    jp4.add(jLabel);
                }
            }).start();
        });
        //签到监听器
        signIn.addActionListener(e -> {
            pointProcess.addpoints(20,user.getId());
            pointProcess = new PointProcess();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    jLabel.setText("剩余积分:"+pointProcess.getPointByUserId(user.getId()).getPointValue());
                    jp4.add(jLabel);
                }
            }).start();
            signIn.setEnabled(false);

        });
        personalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UOrder uOrder = new UOrder(user);
                uOrder.go();
            }
        });
        //上一页按钮的监听器
        previousPageButton.addActionListener(e -> {
            if(getCurrentPage() > getFirstPage()){
                setCurrentPage(currentPage - 1);
                showData();
            }else{
                setCurrentPage(getFirstPage());
                showData();
            }
        });

        //下一页按钮的监听器
        nextPageButton.addActionListener(e -> {
            if(getCurrentPage() < getLastPage()){
                setCurrentPage(currentPage + 1);
                showData();
            }else{
                setCurrentPage(lastPage);
                showData();
            }
        });

        //尾页按钮的监听器
        lastPageButton.addActionListener(e -> {
            setCurrentPage(lastPage);
            showData();
        });
    }

    /**
     * 初始化页码
     */
    private void initPageNumber(){

        this.tableRows = 8;
        this.currentPage = firstPage;

        //计算尾页的页码
        if(this.goodsList.size() % this.tableRows ==0){
            this.lastPage = this.goodsList.size() / this.tableRows;
        }else{
            this.lastPage = this.goodsList.size() / this.tableRows + 1;
        }
    }

    private void initGoodsList(){
        GoodsProcess goodsProcess = new GoodsProcess();
        this.goodsList = goodsProcess.getGoods();
    }

    /**
     *显示所有商品信息
     */
    private void showData(){
        DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
        //清楚原有数据
        defaultTableModel.setRowCount(0);

        //列表分页
        Page page = new Page<Goods>(tableRows);
        List<Goods> list =  page.cutList(currentPage, goodsList);

        try {
            for(Goods goods : list){
                Vector v = new Vector();
                if (goods.getIsDele() == 0){
                    v.add(goods.getId());
                    v.add(goods.getGoodsId());
                    v.add(goods.getGoodsName());
                    v.add(goods.getGoodsPrice());
                    //获取图片数据，有就加载，无则加载默认图片
                    Blob blob = goods.getPicStream();
                    if(blob != null){
                        byte[] data = blob.getBytes(1,(int)blob.length());
                        v.add(new ImageIcon(data));
                    }else {
                        v.add(new ImageIcon("image/default.png"));
                    }
                    //添加一行数据
                    defaultTableModel.addRow(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //自定义table模型，让其可以加载图片内容
    static class CustomModel extends DefaultTableModel{

        CustomModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        //让table显示图片
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if(columnIndex == 4){
                return ImageIcon.class;
            }else {
                return super.getColumnClass(columnIndex);
            }
        }
    }
}