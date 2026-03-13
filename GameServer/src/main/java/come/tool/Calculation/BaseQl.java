package come.tool.Calculation;

import java.math.BigDecimal;

public class BaseQl {
	private String key;
	private BigDecimal value;
	
	public BaseQl(String key, BigDecimal value) {
		super();
		this.key = key;
		this.value = value;
	}
	public BaseQl(String key, Double value) {
		super();
		this.key = key;
		this.value = BigDecimal.valueOf(value);
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
}
