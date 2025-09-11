package org.openmrs.module.sespct.api.util; // Or your preferred package

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Teaches Hibernate how to handle the Java 8 LocalDateTime type by converting it to a standard
 * java.sql.Timestamp and back.
 */
@Converter
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {
	
	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
		return (attribute == null ? null : Timestamp.valueOf(attribute));
	}
	
	/**
	 * This method is now corrected. It correctly returns a LocalDateTime.
	 */
	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp dbData) { // <-- CORRECTED return type
		return (dbData == null ? null : dbData.toLocalDateTime());
	}
}
