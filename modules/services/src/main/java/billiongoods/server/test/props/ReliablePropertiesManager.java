package billiongoods.server.test.props;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ReliablePropertiesManager {
    int getInt(String group, String name, int def);

    long getLong(String group, String name, long def);

    Date getDate(String group, String name, Date def);

    double getFloat(String group, String name, double def);

    double getDouble(String group, String name, double def);

    String getString(String group, String name, String def);

    boolean getBoolean(String group, String name, boolean def);


    void setInt(String group, String name, int value);

    void setLong(String group, String name, long value);

    void setDate(String group, String name, Date value);

    void setFloat(String group, String name, double value);

    void setDouble(String group, String name, double value);

    void setString(String group, String name, String value);

    void setBoolean(String group, String name, boolean value);


    void removeProperty(String group, String name);

    boolean hasProperty(String group, String name);
}