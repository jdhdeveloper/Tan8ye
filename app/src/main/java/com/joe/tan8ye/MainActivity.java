package com.joe.tan8ye;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private EditText et1,et2,et3,et4,et5,et6,et7,et8,et9,et10,et11;
    private EditText et_content;
    private Button btn_getContent,btn_clean,btn_range;
    private TextView txtNumber,txtPrice,txtFreight,txtWeight,txtAgentPrice,txtProfit;

    private Toast toast;
    //川内
    private static final int CHUAN_IN  = 1;
    //川外
    private static final int CHUAN_OUT = 0;
    //当前范围
    private int range = 1;
    //川内首重5块
    private static final double PRICE_IN = 5;
    //川外首重6块
    private static final double PRICE_OUT = 6;
    //每份食品包装重10g
    private static final double WEIGHT = 10;
    //外包装箱重100g
    private static final double WEIGHT_BOX = 100;
    //首重1000g
    private static final double FIRST_WIGHT = 1000;

    private StringBuffer sBuffer = new StringBuffer();
    private EditText[] arrEt = null;
    private String[] arrName = {"兔丁","牛肉","鸭舌","兔腿","鸡尖","掌中宝"
            ,"辣椒面","火锅料","花生糖","蛋丝糖","蛋条糖"};
    private int[] arrPrcie = {25,35,26,28,18,32,22,48,16,12,12};
    private int[] arrAgentPrice = {20,27,20,23,13,26,16,33,12,8,8};
    private int[] arrWeight = {200,170,100,210,170,150,150,600,200,200,200};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        //冷吃兔丁
        et1 = (EditText) findViewById(R.id.et_1);
        //招牌牛肉
        et2 = (EditText) findViewById(R.id.et_2);
        //极品鸭舌
        et3 = (EditText) findViewById(R.id.et_3);
        //销魂兔腿
        et4 = (EditText) findViewById(R.id.et_4);
        //美味鸡尖
        et5 = (EditText) findViewById(R.id.et_5);
        //掌中宝
        et6 = (EditText) findViewById(R.id.et_6);
        //五香辣椒面
        et7 = (EditText) findViewById(R.id.et_7);
        //火锅底料
        et8 = (EditText) findViewById(R.id.et_8);
        //花生糖
        et9 = (EditText) findViewById(R.id.et_9);
        //蛋丝糖
        et10 = (EditText) findViewById(R.id.et_10);
        //蛋糖条
        et11 = (EditText) findViewById(R.id.et_11);
        et_content = (EditText) findViewById(R.id.et_content);
        btn_getContent = (Button) findViewById(R.id.btn_getContent);
        btn_clean = (Button) findViewById(R.id.btn_clean);
        btn_range = (Button) findViewById(R.id.btn_range);
        txtNumber = (TextView) findViewById(R.id.txt_number);
        txtPrice = (TextView) findViewById(R.id.txt_price);
        txtFreight = (TextView) findViewById(R.id.txt_freight);
        txtWeight = (TextView) findViewById(R.id.txt_weight);
        txtAgentPrice = (TextView) findViewById(R.id.txt_agent_price);
        txtProfit = (TextView) findViewById(R.id.txt_profit);

        arrEt = new EditText[]{et1, et2, et3, et4, et5, et6, et7, et8, et9, et10, et11};
    }

    private void initData() {

        btn_getContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空编辑框
                et_content.setText("");
                //清空StringBuffer
                sBuffer.setLength(0);
                //获取当前拼接的字符串
                String strContent = getContent(sBuffer).toString();
                //设置到编辑框
                et_content.setText(strContent);
                //复制到剪贴板
                copy(strContent);
                //计算出总价格并显示
                txtPrice.setText("总价格： "+getPrice()+"元");
                //计算出总数量并显示
                txtNumber.setText("总数量： "+getCount()+"份");
                //计算出运费并显示
                txtFreight.setText("总运费："+(int)getFreight()+"元");
                //计算出总重量并显示
                txtWeight.setText("总重量："+(int)getWeight()+"g");
                //计算出代理价格并显示
                txtAgentPrice.setText("代理价："+getAgentPrice()+"元");
                //计算出利润并显示
                txtProfit.setText("利润："+getProfit()+"元");
            }
        });

        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<arrEt.length;i++){
                    if (isEditTextEmpty(arrEt[i])){
                        arrEt[i].setText("");
                    }
                }
            }
        });

        //切换川内/外状态
        btn_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果当前是川内
                if (range==CHUAN_IN){
                    range = CHUAN_OUT;
                    btn_range.setText("川外");
                }else if (range==CHUAN_OUT){//当前是川外
                    range = CHUAN_IN;
                    btn_range.setText("川内");
                }
            }
        });

    }

    /**
     * 复制到体统剪贴板
     * @param str
     */
    private void copy(String str){

        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text",str);
        manager.setPrimaryClip(clipData);
        showToast(getApplicationContext(),"已复制到剪贴板");

    }

    /**
     * 获取食品+包装的重量（单位：克）
     * 每一份食品包装是10g，外箱包装100g（只算一个），
     * @return
     */
    private double getWeight(){
        double weight = 0.0;
        double number = 0.0;
        for (int i = 0;i<arrEt.length;i++){
            if (isEditTextEmpty(arrEt[i])) {
                number = Integer.parseInt(arrEt[i].getText().toString());
                weight += number * (arrWeight[i] + WEIGHT);
            }
        }
        weight += WEIGHT_BOX;
        return weight;
    }

    /**
     * 获取运费
     * 1000g以内是首重
     * 超过首重，每超过1000g川内+2块，川外+5块
     * @return 运费
     */
    private double getFreight(){
        if (range == CHUAN_IN){//川内
            if (getWeight()<=FIRST_WIGHT){//首重之内
                return PRICE_IN;
            }else{//首重之外
               return PRICE_IN + Math.ceil((getWeight()-FIRST_WIGHT)/1000)*2;
            }
        }else if (range == CHUAN_OUT){//川外
            if (getWeight()<=FIRST_WIGHT){//首重之内
                return PRICE_OUT;
            }else{//首重之外
                return PRICE_OUT + Math.ceil((getWeight()-FIRST_WIGHT)/1000)*5;
            }
        }

        return 0;
    }

    /**
     * 获取利润
     * @return
     */
    private String getProfit(){
        int profit  = Integer.parseInt(getPrice())-Integer.parseInt(getAgentPrice());
        return String.valueOf(profit);
    }

    /**
     * 获取食品总价格
     * @return
     */
    private String getPrice(){
        int sum = 0;
        for (int i = 0;i<arrEt.length;i++){
            if (isEditTextEmpty(arrEt[i])){
                String str = arrEt[i].getText().toString();
                int price = Integer.parseInt(str);
                sum += price*arrPrcie[i];
            }
        }
        return  String.valueOf(sum);
    }

    /**
     * 获取代理价格
     * @return
     */
    private String getAgentPrice(){
        int sum = 0;
        for (int i = 0;i<arrEt.length;i++){
            if (isEditTextEmpty(arrEt[i])){
                String str = arrEt[i].getText().toString();
                int price = Integer.parseInt(str);
                sum += price*arrAgentPrice[i];
            }
        }
        return  String.valueOf(sum);
    }

    /**
     * 获取食品份数
     * @return 食品份数
     */
    private String getCount(){
        int count = 0;
        for (int i = 0;i<arrEt.length;i++){
            if (isEditTextEmpty(arrEt[i])){
                String str = arrEt[i].getText().toString();
                count += Integer.parseInt(str);
            }
        }
        return String.valueOf(count);
    }

    private boolean isEditTextEmpty(EditText et){
        if (et!=null){
            String str = et.getText().toString();
            if (!"".equals(str.trim())&&null!=str){
                return true;
            }
        }
        return false;
    }

    private String getString(EditText et){
        if (isEditTextEmpty(et)){
            return et.getText().toString();
        }
        return null;
    }

    /**
     * 获取菜单数据，拼接字符串
     */
    private StringBuffer getContent(StringBuffer buffer){

        for (int i = 0;i<arrEt.length;i++){
            if (isEditTextEmpty(arrEt[i])){
                buffer.append(getString(arrEt[i])).append(" ").append(arrName[i]).append(" ");
            }
        }
        return buffer;
    }

    private void showToast(Context context, String content){

        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
