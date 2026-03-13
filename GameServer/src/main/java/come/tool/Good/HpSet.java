package come.tool.Good;

/**血条参数*/
public class HpSet {

    private long hp;//当前生命值
    private long hpMax;//最大生命值
    private boolean isMuch; //true表示多人同时挑战   false 单人挑战
    public HpSet(String msg) {
        if(msg!=null && msg.split("=").length>=4){
            String[] v=msg.split("=");
            this.hp = Integer.valueOf(v[1]);
            this.hpMax = Integer.valueOf(v[2]);
            int isM = Integer.valueOf(v[3]);
            if(isM == 1){
                this.isMuch = true;
            }else {
                this.isMuch = false;
            }
        }

    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public long getHpMax() {
        return hpMax;
    }

    public void setHpMax(long hpMax) {
        this.hpMax = hpMax;
    }

    public boolean isMuch() {
        return isMuch;
    }

    public void setMuch(boolean much) {
        isMuch = much;
    }
}
