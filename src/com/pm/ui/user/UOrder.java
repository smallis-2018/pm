package com.pm.ui.user;

import com.pm.dao.datasource.*;
import com.pm.process.OrderInfProcess;
import com.pm.process.OrderProcess;
import com.pm.util.Page;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class UOrder extends JFrame {
    private static final long serialVersionUID = 9527L;
    private JPanel JPInf, JPTable, jPanelButton;
    ;
    private JTable table;
    private JButton btnEdit,
            btnTop,
            btnLast,
            btnNext,
            btnEnd;
    private int currentPage,

    firstPage,
            lastPage,
            tableRows,
            intOId;
    private List<VOrderinfId> orderList;
    private Object[][] data;
    private User loginUser;
    private JLabel JLUserInf;

    public UOrder(User user) {

        btnTop = new JButton("首页");
        btnLast = new JButton("上一页");
        btnNext = new JButton("下一页");
        btnEnd = new JButton("尾页");
        loginUser = user;
        JPInf = new JPanel();
        JPTable = new JPanel();

        jPanelButton = new JPanel();
        btnEdit = new JButton("修改密码");
        JLUserInf = new JLabel("欢迎您，" + loginUser.getUserName());
        //设置表格
        String[] columnNames = {"ID", "订单号", "商品名", "状态"};
        table = new JTable(data, columnNames) {
            //禁止编辑单元格
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(new DefaultTableModel(null,
                columnNames));
        table.setFillsViewportHeight(true);
        //排序
        table.setAutoCreateRowSorter(true);
        //不可移动
        table.getTableHeader().setReorderingAllowed(false);
        //不可多选
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        JPTable.add(scrollPane);

        JPInf.add(JLUserInf);
        JPInf.add(btnEdit);
        //分页块
        jPanelButton.add(btnTop);
        jPanelButton.add(btnLast);
        jPanelButton.add(btnNext);
        jPanelButton.add(btnEnd);

        add(JPInf);
        add(JPTable);
        add(jPanelButton);
        setTitle("个人主页");// 标题
        setSize(500, 400);// 窗口大小
        setLayout(new GridLayout(3, 1));
        setResizable(false);

        setLocationRelativeTo(null);// 窗口居中
        this.setVisible(true);
    }


    /**
     * 获取当前页
     *
     * @return currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 设置当前页
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * 获取首页
     *
     * @return firstPage
     */
    public int getFirstPage() {
        return firstPage;
    }

    /**
     * 设置首页
     *
     * @param firstPage
     */
    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * 获取尾页
     *
     * @return lastPage
     */
    public int getLastPage() {
        return lastPage;
    }

    /**
     * 设置尾页
     *
     * @param lastPage
     */
    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public void go() {

        setOrderList(loginUser);
        initPageNumber();
        showData();

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditPwd editPwd = new EditPwd(loginUser);
                editPwd.Edit();
            }
        });
        //首页按钮功能
        btnTop.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setCurrentPage(firstPage);
                showData();
            }
        });
        //上一页按钮功能
        btnLast.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (getCurrentPage() > getFirstPage()) {
                    setCurrentPage(currentPage - 1);
                    showData();
                } else {
                    setCurrentPage(firstPage);
                    showData();
                }
            }
        });
        //下一页按钮功能
        btnNext.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (getCurrentPage() < getLastPage()) {
                    setCurrentPage(currentPage + 1);
                    showData();
                } else {
                    setCurrentPage(lastPage);
                    showData();
                }
            }
        });
        //尾页按钮功能
        btnEnd.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setCurrentPage(lastPage);
                showData();
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {

                    try {
                        String id = table.getValueAt(table.getSelectedRow(), 0).toString();
                        int ID = Integer.valueOf(id);
                        OrderInfProcess op = new OrderInfProcess();
                        ;
                        UOdetail UO = new UOdetail(op.getOrderInfByOId1(ID),
                                UOrder.this);
                        UO.go();
                    } catch (Exception el) {
                        JOptionPane.showMessageDialog(null,
                                "请正确操作",
                                "警告",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    public void initPageNumber(){
        this.firstPage=1;
        this.tableRows=5;
        this.currentPage=firstPage;

        if(this.orderList.size()%this.tableRows==0){
            this.lastPage=this.orderList.size()/this.tableRows;
        }else {
            this.lastPage=this.orderList.size()/this.tableRows+1;
        }
    }

    public void setOrderList(User user){
        OrderInfProcess orderInfProcess=new OrderInfProcess();
        this.orderList=orderInfProcess.getOrderInfByUId(user.getId());
    }
    /***
     *显示用户订单信息

     */
    public void showData() {

        DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
        defaultTableModel.setRowCount(0);

        //分页
        Page page = new Page(tableRows);
        List<VOrderinfId> list = page.cutList(currentPage, orderList);

        for (VOrderinfId order : list) {
            Vector v = new Vector();
            if (order != null) {
                v.add(order.getoId());
                v.add(order.getOrderId());
                v.add(order.getGoodsName());
                v.add(order.getOsType());
                defaultTableModel.addRow(v);
                defaultTableModel.addRow(v);
            }
        }

    }
}

