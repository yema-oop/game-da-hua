package come.tool.FightingDataAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import come.tool.FightingData.*;
import come.tool.FightingLingAction.LingActionType;

public class Lingbao implements DataAction {

    @Override
    public void analysisAction(ManData manData, FightingEvents fightingEvents,
                               String type, Battlefield battlefield) {
        // TODO Auto-generated method stub
        //获取其他未出手的灵宝
        if (manData.xzstate(TypeUtil.TZ_SGJQ) != null) {
            return;
        }
        List<ManData> ful = getHelp(battlefield.fightingdata, manData);
        FightingSkill skill = manData.getlingskill(ful.size() + 1, battlefield.BattleType);
        if (skill == null)
			return;
        int zltype = 0;
        int id = skill.getSkillid();
        if (id >= 3001 && id <= 3010) {
            zltype = 1;
        } else if (id == 3011 || id == 3012) {
            zltype = 2;
        } else if (id >= 3013 && id <= 3023 || id == 3032) {
            zltype = 3;
        } else if (id == 3033) {
            zltype = 4;
        } else if (id == 3026) {
            zltype = 5;
        } else if (id >= 3029 && id <= 3031) {
            zltype = 6;
        } else if (id == 3024) {
            zltype = 7;
        } else if (id == 3027) {
            zltype = 8;
        } else if (id == 3028) {
            zltype = 11;
        } else if (id == 3025) {
            zltype = 9;

        }
        if (zltype == 0)
			return;
        LingActionType.getActionById(zltype).lingAction(manData, ful, skill, battlefield);
        huiling(manData, battlefield);
    }
    public void huiling(ManData manData, Battlefield battlefield) {
        if (manData.getHuiling() != 0) {
            ManData data = battlefield.getlingbaoparent(manData);
            ChangeFighting changeFighting = new ChangeFighting();
            List<FightingState> Accepterlist1 = new ArrayList<>();
            FightingEvents fightingEvents1 = new FightingEvents();
            if (manData.getHuiling() == 1)
                changeFighting.setChangemp(BigDecimal.valueOf(3000));
            else {
                changeFighting.setChangehp(BigDecimal.ONE);
                changeFighting.setChangemp(BigDecimal.valueOf(4000));
            }
            FightingState org3 = new FightingState();
            org3.setCamp(data.getCamp());
            org3.setMan(data.getMan());
            org3.setSkillskin("1843");
            org3.setText("回灵");
            //org3.setStartState(TypeUtil.JN);
            //Accepterlist1.add(org3);
            FightingPackage.ChangeProcess(changeFighting, null, data, org3, "回血", Accepterlist1, battlefield);
            fightingEvents1.setAccepterlist(Accepterlist1);
            battlefield.NewEvents.add(fightingEvents1);
//            int k = 0;
        }
    }
    /**
     * 获取辅助灵宝
     */
    public static List<ManData> getHelp(List<ManData> datas, ManData manData) {
        List<ManData> ful = new ArrayList<>();
        for (int i = datas.size() - 1; i >= 0; i--) {
            ManData data = datas.get(i);
            if (data.getType() == 3 && data.getCamp() == manData.getCamp() && data.getStates() == 0) {//
                if (isxy(data.getQihe()) && data.xzstate(TypeUtil.TZ_SGJQ) == null) {//
                    ful.add(data);
                }
            }
        }
        return ful;
    }

    /**
     * 获取灵宝触发技能
     */
    public FightingSkill getskill(int size) {

        return null;
    }

    /**
     * 根据契合度判断是否相应
     */
    public static boolean isxy(long qh) {
        if (Battlefield.random.nextInt(100) < (Math.pow(qh, 0.245) + 10)) {
            return true;
        }
        return false;
    }
}
