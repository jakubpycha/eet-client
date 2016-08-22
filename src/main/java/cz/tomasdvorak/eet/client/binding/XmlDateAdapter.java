package cz.tomasdvorak.eet.client.binding;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * dateTime attribute is in EET defined by pattern \d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(Z|[+\-]\d\d:\d\d) according to ISO 8601.
 * The default CXF conversion includes also millis and timezone in format HHMM. We need to get rid of millis and timezone
 * has to be in format HH:MM. Thus we implement our own converter.
 *
 * TODO: is it needed? Exists any other or default solution?
 *
 * Valid date example: 2016-12-09T16:45:36+01:00
 *
 * @see XMLGregorianCalendar#toXMLFormat()
 */
public class XmlDateAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(final String inputDate) throws Exception {
    	return DatatypeConverter.parseDateTime(inputDate).getTime();
    }

    @Override
    public String marshal(final Date inputDate) throws Exception {
    	Calendar c = new GregorianCalendar();
    	c.setTime(inputDate);
    	return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(c);
    	
    }

}
