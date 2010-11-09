package collab.fm.server.bean.persist.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.transfer.DataItem2;

/**
 * Attribute of Enumeration type.
 * @author mark
 *
 */
public class EnumAttributeType extends AttributeType {
	
	private static Logger logger = Logger.getLogger(EnumAttributeType.class);
	
	private List<String> validValues = new ArrayList<String>();

	public EnumAttributeType() {
		super();
		this.typeName = AttributeType.TYPE_ENUM;
	}
	
	@Override
	public void transfer(DataItem2 a) {
//		EnumAttribute2 a2 = (EnumAttribute2) a;
//		super.transfer(a2);
//		for (String s: this.getValidValues()) {
//			a2.addEnum(s);
//		}
	}
	
	@Override
	public boolean valueConformsToType(Value v) {
		// For an enumeration type, a valid value must be one of the type's predefined values.
		for (String predefinedValue: validValues) {
			if (predefinedValue.equals(v.toValueString())) {
				return true;
			}
		}
		return false;
	}
	
	public void addValidValue(String value) {
		validValues.add(value);
	}
	
	public List<String> getValidValues() {
		return validValues;
	}

	public void setValidValues(List<String> validValues) {
		this.validValues = validValues;
	}
	
}