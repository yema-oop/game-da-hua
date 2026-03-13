package org.come.bean;

import org.come.entity.DianKaEntity;
import org.come.entity.Gang;

import java.util.List;

public class DianKaListBean {
	// 所有帮派信息
	private List<DianKaEntity> diankas;

	public List<DianKaEntity> getDianKa() {
		return diankas;
	}

	public void setDianKa(List<DianKaEntity> dianka) {
		this.diankas = dianka;
	}


}
