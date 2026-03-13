package org.come.action.role;

import java.util.ArrayList;
import java.util.List;

public class RoleSkill {
    private static  RoleSkill roleSkill;
    public static RoleSkill getRoleSkill(){
        if (roleSkill==null) roleSkill=new RoleSkill();
        return roleSkill;
    }
    /**
     * 获取该类型的全部技能加上熟练度
     */
    public String[] getAllSkill(int type,int sld){
        List<String> skills=add(type);
        String[] vs=new String[skills.size()];
        for (int i = 0; i <skills.size(); i++) {
            vs[i]=skills.get(i)+"_"+sld;
        }
        return vs;
    }
    public List<String> add(int type){
        List<Integer> s=new ArrayList<>();
        if (type<1||type>10) {
            s.add(type);
        }else {
            switch ((type+1)/2) {
                case 1:
                    s.add(23);
                    s.add(24);
                    if (type%2==0)s.add(25);
                    else s.add(26);
                    break;
                case 2:
                    s.add(27);
                    s.add(28);
                    if (type%2==0)s.add(29);
                    else s.add(30);
                    break;
                case 3:
                    s.add(19);
                    s.add(22);
                    if (type%2==0)s.add(20);
                    else s.add(21);
                    break;
                case 4:
                    s.add(72);
                    s.add(74);
                    if (type%2==0)s.add(75);
                    else s.add(73);
                    break;
                case 5:
                    s.add(76);
                    s.add(77);
                    if (type%2==0)s.add(79);
                    else s.add(78);
                    break;
            }
        }
        List<String> skills=new ArrayList<>();
        for (int i = s.size()-1; i >=0 ; i--) {
            switch (s.get(i)) {
                case 19:
                    skills.add("1046");skills.add("1047");skills.add("1048");
                    skills.add("1049");skills.add("1050");break;
                case 20:
                    skills.add("1056");skills.add("1057");skills.add("1058");
                    skills.add("1059");skills.add("1060");break;
                case 21:
                    skills.add("1041");skills.add("1042");skills.add("1043");
                    skills.add("1044");skills.add("1045");break;
                case 22:
                    skills.add("1051");skills.add("1052");skills.add("1053");
                    skills.add("1054");skills.add("1055");break;
                case 23:
                    skills.add("1006");skills.add("1007");skills.add("1008");
                    skills.add("1009");skills.add("1010");break;
                case 24:
                    skills.add("1011");skills.add("1012");skills.add("1013");
                    skills.add("1014");skills.add("1015");break;
                case 25:
                    skills.add("1016");skills.add("1017");skills.add("1018");
                    skills.add("1019");skills.add("1020");break;
                case 26:
                    skills.add("1001");skills.add("1002");skills.add("1003");
                    skills.add("1004");skills.add("1005");break;
                case 27:
                    skills.add("1021");skills.add("1022");skills.add("1023");
                    skills.add("1024");skills.add("1025");break;
                case 28:
                    skills.add("1026");skills.add("1027");skills.add("1028");
                    skills.add("1029");skills.add("1030");break;
                case 29:
                    skills.add("1031");skills.add("1032");skills.add("1033");
                    skills.add("1034");skills.add("1035");break;
                case 30:
                    skills.add("1036");skills.add("1037");skills.add("1038");
                    skills.add("1039");skills.add("1040");break;
                case 72:
                    skills.add("1061");skills.add("1062");skills.add("1063");
                    skills.add("1064");skills.add("1065");break;
                case 73:
                    skills.add("1066");skills.add("1067");skills.add("1068");
                    skills.add("1069");skills.add("1070");break;
                case 74:
                    skills.add("1071");skills.add("1072");skills.add("1073");
                    skills.add("1074");skills.add("1075");break;
                case 75:
                    skills.add("1076");skills.add("1077");skills.add("1078");
                    skills.add("1079");skills.add("1080");break;
                case 76:
                    skills.add("1081");skills.add("1082");skills.add("1083");
                    skills.add("1084");skills.add("1085");break;
                case 77:
                    skills.add("1091");skills.add("1092");skills.add("1093");
                    skills.add("1094");skills.add("1095");break;
                case 78:
                    skills.add("1096");skills.add("1097");skills.add("1098");
                    skills.add("1099");skills.add("1100");break;
                case 79:
                    skills.add("1086");skills.add("1087");skills.add("1088");
                    skills.add("1089");skills.add("1090");break;
                default:
                    break;
            }
        }
        return skills;
    }
}
