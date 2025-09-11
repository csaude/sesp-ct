package org.openmrs.module.sespct.api.util;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A simple, immutable data carrier for a date-time range. This is the Java 8 equivalent of the
 * DateRange record.
 */
public final class DateRange {
	
	private final LocalDateTime startDateTime;
	
	private final LocalDateTime endDateTime;
	
	public DateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}
	
	// Accessor methods (getters)
	public LocalDateTime startDateTime() {
		return startDateTime;
	}
	
	public LocalDateTime endDateTime() {
		return endDateTime;
	}
	
	// Optional but highly recommended for data classes
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DateRange dateRange = (DateRange) o;
		return Objects.equals(startDateTime, dateRange.startDateTime) && Objects.equals(endDateTime, dateRange.endDateTime);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(startDateTime, endDateTime);
	}
	
	@Override
	public String toString() {
		return "DateRange[" + "startDateTime=" + startDateTime + ", endDateTime=" + endDateTime + ']';
	}
}
