package com.youmai.project.utils.chat;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;

import com.youmai.project.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置聊天表情包
 * Created by Administrator on 2017/11/3 0003.
 */

public class ChatIcon {

    private static ChatIcon chatIcon=null;

    public static List<String> iconList=new ArrayList<>();

    public static Map<String ,Integer> iconMap=new HashMap<>();

    private static final Factory spannableFactory = Spannable.Factory.getInstance();

    public static ChatIcon getIntesen(){
        if(null==chatIcon){
            chatIcon=new ChatIcon();
        }
        return chatIcon;
    }


    static {
        if(iconList.size()==0){
            for(int i=0;i<54;i++){
                iconList.add("[dyzem"+i+"]");
            }
        }

//        if(iconMap.size()==0){
//            iconMap.put("[dyzem0]", R.mipmap.dyzem0);
//            iconMap.put("[dyzem1]",R.mipmap.dyzem1);
//            iconMap.put("[dyzem2]",R.mipmap.dyzem2);
//            iconMap.put("[dyzem3]",R.mipmap.dyzem3);
//            iconMap.put("[dyzem4]",R.mipmap.dyzem4);
//            iconMap.put("[dyzem5]",R.mipmap.dyzem5);
//            iconMap.put("[dyzem6]",R.mipmap.dyzem6);
//            iconMap.put("[dyzem7]",R.mipmap.dyzem7);
//            iconMap.put("[dyzem8]",R.mipmap.dyzem8);
//            iconMap.put("[dyzem9]",R.mipmap.dyzem9);
//            iconMap.put("[dyzem10]",R.mipmap.dyzem10);
//            iconMap.put("[dyzem11]",R.mipmap.dyzem11);
//            iconMap.put("[dyzem12]",R.mipmap.dyzem12);
//            iconMap.put("[dyzem13]",R.mipmap.dyzem13);
//            iconMap.put("[dyzem14]",R.mipmap.dyzem14);
//            iconMap.put("[dyzem15]",R.mipmap.dyzem15);
//            iconMap.put("[dyzem16]",R.mipmap.dyzem16);
//            iconMap.put("[dyzem17]",R.mipmap.dyzem17);
//            iconMap.put("[dyzem18]",R.mipmap.dyzem18);
//            iconMap.put("[dyzem19]",R.mipmap.dyzem19);
//            iconMap.put("[dyzem20]",R.mipmap.dyzem20);
//            iconMap.put("[dyzem21]",R.mipmap.dyzem21);
//            iconMap.put("[dyzem22]",R.mipmap.dyzem22);
//            iconMap.put("[dyzem23]",R.mipmap.dyzem23);
//            iconMap.put("[dyzem24]",R.mipmap.dyzem24);
//            iconMap.put("[dyzem25]",R.mipmap.dyzem25);
//            iconMap.put("[dyzem26]",R.mipmap.dyzem26);
//            iconMap.put("[dyzem27]",R.mipmap.dyzem27);
//            iconMap.put("[dyzem28]",R.mipmap.dyzem28);
//            iconMap.put("[dyzem29]",R.mipmap.dyzem29);
//            iconMap.put("[dyzem30]",R.mipmap.dyzem30);
//            iconMap.put("[dyzem31]",R.mipmap.dyzem31);
//            iconMap.put("[dyzem32]",R.mipmap.dyzem32);
//            iconMap.put("[dyzem33]",R.mipmap.dyzem33);
//            iconMap.put("[dyzem34]",R.mipmap.dyzem34);
//            iconMap.put("[dyzem35]",R.mipmap.dyzem35);
//
//            iconMap.put("[dyzem36]",R.mipmap.dyzem36);
//            iconMap.put("[dyzem37]",R.mipmap.dyzem37);
//            iconMap.put("[dyzem38]",R.mipmap.dyzem38);
//            iconMap.put("[dyzem39]",R.mipmap.dyzem39);
//            iconMap.put("[dyzem40]",R.mipmap.dyzem40);
//            iconMap.put("[dyzem41]",R.mipmap.dyzem41);
//            iconMap.put("[dyzem42]",R.mipmap.dyzem42);
//            iconMap.put("[dyzem43]",R.mipmap.dyzem43);
//            iconMap.put("[dyzem44]",R.mipmap.dyzem44);
//            iconMap.put("[dyzem45]",R.mipmap.dyzem45);
//            iconMap.put("[dyzem46]",R.mipmap.dyzem46);
//            iconMap.put("[dyzem47]",R.mipmap.dyzem47);
//            iconMap.put("[dyzem48]",R.mipmap.dyzem48);
//            iconMap.put("[dyzem49]",R.mipmap.dyzem49);
//            iconMap.put("[dyzem50]",R.mipmap.dyzem50);
//            iconMap.put("[dyzem51]",R.mipmap.dyzem51);
//            iconMap.put("[dyzem52]",R.mipmap.dyzem52);
//            iconMap.put("[dyzem53]",R.mipmap.dyzem53);
//
//        }
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        return spannable;
    }
}
