package come.tool.FightingDataAction;

import come.tool.FightingData.Battlefield;
import come.tool.FightingData.ChangeFighting;
import come.tool.FightingData.FightingState;
import come.tool.FightingData.ManData;

import java.util.List;

public interface GxgfInterface {
    public void gxgf(FightingState org2, ManData mydata, ManData data, ChangeFighting changeFighting,
                     Battlefield battlefield, List<FightingState> list);
}
